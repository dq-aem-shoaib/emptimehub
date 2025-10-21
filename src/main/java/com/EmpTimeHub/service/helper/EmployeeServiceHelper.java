package com.EmpTimeHub.service.helper;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.entity.BankDetails;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.model.AddressModel;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.repository.ClientRepository;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.AddressService;
import com.EmpTimeHub.generator.CredentialGenerator;
import com.EmpTimeHub.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceHelper {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final AddressService addressService;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final CredentialGenerator credentialGenerator;

    // 1️⃣ Generate credentials (companyId + raw + encrypted password)
    public Credentials generateCredentials() {
        String rawPassword = CredentialGenerator.generateSecurePassword(12);
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        String companyId = credentialGenerator.generateNextCompanyId();
        log.info("Employee added with username: {} and Password: {}", companyId, rawPassword);
        return new Credentials(companyId, rawPassword, encryptedPassword);
    }

    public User saveUser(EnumConstants.Designation designation, String companyEmail, String companyId, String encryptedPassword) {
        EnumConstants.Role role = mapDesignationToRole(designation);
        User user = User.builder()
                .userName(companyId)
                .companyEmail(companyEmail)
                .password(encryptedPassword) // use the encrypted password
                .role(role)
                .build();
        return userRepository.save(user);
    }

    // Map designation to role
    public EnumConstants.Role mapDesignationToRole(EnumConstants.Designation designation) {
        if (EnumConstants.Designation.REPORTING_MANAGER.equals(designation)) {
            return EnumConstants.Role.MANAGER;
        }
        return EnumConstants.Role.EMPLOYEE;
    }

    // 3️⃣ Fetch client by ID
    public Client getClient(UUID clientId) {
        if (clientId == null) return null;
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    // 4️⃣ Fetch reporting manager
    public Employee getReportingManager(UUID managerId) {
        if (managerId == null) return null;
        return employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Reporting manager not found"));
    }

    // 5️⃣ Build BankDetails from EmployeeModel
    public BankDetails buildBankDetails(EmployeeModel model) {
        if (model.getAccountNumber() == null || model.getAccountHolderName() == null) return null;
        return BankDetails.builder()
                .accountNumber(model.getAccountNumber())
                .accountHolderName(model.getAccountHolderName())
                .bankName(model.getBankName())
                .ifscCode(model.getIfscCode())
                .branchName(model.getBranchName())
                .build();
    }

    // 6️⃣ Save Addresses
    public void saveAddresses(UUID employeeId, List<AddressModel> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            addressService.addAddresses(EnumConstants.EntityType.EMPLOYEE.getValue(), employeeId, addresses);
        }
    }

    // 7️⃣ Convert Employee
    public EmployeeDTO toEmployeeDTO(Employee employee) {
        List<AddressModel> addresses = addressService.getAddressesForEntity(EnumConstants.EntityType.EMPLOYEE.getValue(), employee.getEmployeeId());
        EmployeeDTO dto = employeeMapper.toDTO(employee, addresses);

        // Include BankDetails
        if (employee.getBankDetails() != null) {
            dto.setAccountNumber(employee.getBankDetails().getAccountNumber());
            dto.setAccountHolderName(employee.getBankDetails().getAccountHolderName());
            dto.setBankName(employee.getBankDetails().getBankName());
            dto.setIfscCode(employee.getBankDetails().getIfscCode());
            dto.setBranchName(employee.getBankDetails().getBranchName());
        }

        // Include client info
        if (employee.getClient() != null) {
            dto.setClientId(employee.getClient().getClientId());
            dto.setClientName(employee.getClient().getCompanyName());
        }

        // Enum → String
        if (employee.getDesignation() != null) {
            dto.setDesignation(employee.getDesignation().name());
        }

        return dto;
    }

    // Credentials DTO
    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class Credentials {
        private final String companyId;
        private final String rawPassword;
        private final String encryptedPassword;
    }

    public void updateEmployeeFromModel(Employee employee, EmployeeModel model) {
        // ---------- Basic Info ----------
        updateIfNotNull(model.getFirstName(), employee::setFirstName);
        updateIfNotNull(model.getLastName(), employee::setLastName);
        updateIfNotNull(model.getPersonalEmail(), employee::setPersonalEmail);
        updateIfNotNull(model.getCompanyEmail(), employee::setCompanyEmail);
        updateIfNotNull(model.getContactNumber(), employee::setContactNumber);

        // ---------- Associations ----------
        if (model.getClientId() != null) {
            employee.setClient(getClient(model.getClientId()));
        }
        if (model.getReportingManagerId() != null) {
            employee.setReportingManager(getReportingManager(model.getReportingManagerId()));
        }

        // ---------- Job Info ----------
        updateIfNotNull(model.getDesignation(), employee::setDesignation);
        updateIfNotNull(model.getDateOfBirth(), employee::setDateOfBirth);
        updateIfNotNull(model.getDateOfJoining(), employee::setDateOfJoining);
        updateIfNotNull(model.getRateCard(), employee::setRateCard);
        updateIfNotNull(model.getEmploymentType(), employee::setEmploymentType);

        // ---------- Identification ----------
        updateIfNotNull(model.getPanNumber(), employee::setPanNumber);
        updateIfNotNull(model.getAadharNumber(), employee::setAadharNumber);

        // ---------- Bank Details ----------
        BankDetails bankDetails = employee.getBankDetails();
        if (bankDetails == null) {
            bankDetails = BankDetails.builder().build();
            employee.setBankDetails(bankDetails);
        }
        updateIfNotNull(model.getAccountNumber(), bankDetails::setAccountNumber);
        updateIfNotNull(model.getAccountHolderName(), bankDetails::setAccountHolderName);
        updateIfNotNull(model.getBankName(), bankDetails::setBankName);
        updateIfNotNull(model.getIfscCode(), bankDetails::setIfscCode);
        updateIfNotNull(model.getBranchName(), bankDetails::setBranchName);

        // ---------- Addresses ----------
        if (model.getAddresses() != null && !model.getAddresses().isEmpty()) {
            saveAddresses(employee.getEmployeeId(), model.getAddresses());
        }

        // ---------- Document URLs ----------
        updateIfNotNull(model.getPanCardUrl(), employee::setPanCardUrl);
        updateIfNotNull(model.getAadharCardUrl(), employee::setAadharCardUrl);
        updateIfNotNull(model.getBankPassbookUrl(), employee::setBankPassbookUrl);
        updateIfNotNull(model.getTenthCftUrl(), employee::setTenthCftUrl);
        updateIfNotNull(model.getInterCftUrl(), employee::setInterCftUrl);
        updateIfNotNull(model.getDegreeCftUrl(), employee::setDegreeCftUrl);
        updateIfNotNull(model.getPostGraduationCftUrl(), employee::setPostGraduationCftUrl);

        // ---------- Timestamp ----------
        employee.setUpdatedAt(java.time.LocalDateTime.now());
    }

    // Utility method for conditional update
    private <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

}
