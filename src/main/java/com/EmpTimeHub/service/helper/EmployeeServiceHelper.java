package com.EmpTimeHub.service.helper;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.*;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.model.AddressModel;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.repository.ClientRepository;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.*;
import com.EmpTimeHub.generator.CredentialGenerator;
import com.EmpTimeHub.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceHelper {

    // -------------------- Repositories & Services --------------------
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    private final AddressService addressService;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final CredentialGenerator credentialGenerator;

    private final EmployeeDocumentService employeeDocumentService;
    private final EmployeeSalaryService employeeSalaryService;
    private final EmployeeAdditionalDetailsService employeeAdditionalDetailsService;
    private final EmployeeEmploymentDetailsService employeeEmploymentDetailsService;
    private final EmployeeInsuranceDetailsService employeeInsuranceDetailsService;
    private final EmployeeEquipmentService employeeEquipmentService;
    private final EmployeeStatutoryDetailsService employeeStatutoryDetailsService;

    // -------------------- Credentials Generation --------------------

    /**
     * Generate credentials for a new employee.
     * Generates companyId, raw password, and encrypted password.
     */
    public Credentials generateCredentials() {
        String rawPassword = CredentialGenerator.generateSecurePassword(12);
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        String companyId = credentialGenerator.generateNextCompanyId();

        log.info("Employee added with username: {} and Password: {}", companyId, rawPassword);

        return new Credentials(companyId, rawPassword, encryptedPassword);
    }

    /**
     * Save user in DB with role based on designation.
     */
    public User saveUser(EnumConstants.Designation designation, String companyEmail, String companyId, String encryptedPassword) {
        EnumConstants.Role role = mapDesignationToRole(designation);

        User user = User.builder()
                .userName(companyId)
                .companyEmail(companyEmail)
                .password(encryptedPassword)
                .role(role)
                .build();

        return userRepository.save(user);
    }

    /**
     * Map employee designation to system role.
     */
    public EnumConstants.Role mapDesignationToRole(EnumConstants.Designation designation) {
        if (EnumConstants.Designation.REPORTING_MANAGER.equals(designation)) {
            return EnumConstants.Role.MANAGER;
        }
        return EnumConstants.Role.EMPLOYEE;
    }

    // -------------------- Fetch Related Entities --------------------

    public Client getClient(UUID clientId) {
        if (clientId == null) return null;
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Employee getReportingManager(UUID managerId) {
        if (managerId == null) return null;
        return employeeRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Reporting manager not found"));
    }

    // -------------------- Bank Details --------------------

    /**
     * Build BankDetails entity from EmployeeModel.
     */
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

    // -------------------- Address Handling --------------------

    public void saveAddresses(UUID employeeId, List<AddressModel> addresses) {
        if (addresses != null && !addresses.isEmpty()) {
            addressService.addAddresses(EnumConstants.EntityType.EMPLOYEE.getValue(), employeeId, addresses);
        }
    }

    // -------------------- Document Handling --------------------

    public void saveDocuments(EmployeeDocumentDTO dto, UUID employeeId) {
        employeeDocumentService.saveDocument(dto, employeeId);
    }

    public void processDocument(List<EmployeeDocumentDTO> documents, UUID employeeId) {
        documents.forEach(document -> saveDocuments(document, employeeId));
    }

    // -------------------- Additional Details --------------------

    public void saveAdditionalDetails(EmployeeAdditionalDetailsDTO dto, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        EmployeeAdditionalDetails existingDetails = employeeAdditionalDetailsService.getAdditionalDetailsByEmployeeId(employeeId);
        EmployeeAdditionalDetails entity = mapDtoToEntity(dto, employee, existingDetails);

        employeeAdditionalDetailsService.saveAdditionalDetails(entity);
    }

    public EmployeeAdditionalDetails mapDtoToEntity(EmployeeAdditionalDetailsDTO dto, Employee employee, EmployeeAdditionalDetails existingEntity) {
        EmployeeAdditionalDetails entity = existingEntity != null ? existingEntity : new EmployeeAdditionalDetails();

        entity.setEmployee(employee);
        entity.setOfferLetterUrl(dto.getOfferLetterUrl());
        entity.setContractUrl(dto.getContractUrl());
        entity.setTaxDeclarationFormUrl(dto.getTaxDeclarationFormUrl());
        entity.setWorkPermitUrl(dto.getWorkPermitUrl());
        entity.setBackgroundCheckStatus(dto.getBackgroundCheckStatus() != null ? dto.getBackgroundCheckStatus() : "PENDING");
        entity.setRemarks(dto.getRemarks());

        return entity;
    }

    public EmployeeAdditionalDetailsDTO toDTO(EmployeeAdditionalDetails entity) {
        if (entity == null) return null;

        return EmployeeAdditionalDetailsDTO.builder()
                .offerLetterUrl(entity.getOfferLetterUrl())
                .contractUrl(entity.getContractUrl())
                .taxDeclarationFormUrl(entity.getTaxDeclarationFormUrl())
                .workPermitUrl(entity.getWorkPermitUrl())
                .backgroundCheckStatus(entity.getBackgroundCheckStatus())
                .remarks(entity.getRemarks())
                .build();
    }

    // -------------------- Employee DTO Conversion --------------------

    public EmployeeDTO toEmployeeDTO(Employee employee) {
        // Fetch all related data
        List<AddressModel> addresses = addressService.getAddressesForEntity(EnumConstants.EntityType.EMPLOYEE.getValue(), employee.getEmployeeId());
        List<EmployeeDocument> documents = employeeDocumentService.getDocumentsByEmployeeId(employee.getEmployeeId());
        EmployeeSalaryDTO salaryDTO = employeeSalaryService.getSalaryDetailsForEmployee(employee);
        EmployeeAdditionalDetails additionalDetails = employeeAdditionalDetailsService.getAdditionalDetailsByEmployeeId(employee.getEmployeeId());

        EmployeeEmploymentDetails employmentDetails = employeeEmploymentDetailsService.getEmploymentDetailsByEmployeeId(employee.getEmployeeId());
        EmployeeInsuranceDetails insuranceDetails = employeeInsuranceDetailsService.getInsuranceDetailsByEmployeeId(employee.getEmployeeId());
        List<EmployeeEquipment> equipmentList = employeeEquipmentService.getEquipmentByEmployeeId(employee.getEmployeeId());
        EmployeeStatutoryDetails statutoryDetails = employeeStatutoryDetailsService.getStatutoryDetailsByEmployeeId(employee.getEmployeeId());

        // Map core Employee entity to DTO
        EmployeeDTO dto = employeeMapper.toDTO(employee, addresses, documents);

        // Include Bank Details
        if (employee.getBankDetails() != null) {
            BankDetails bank = employee.getBankDetails();
            dto.setAccountNumber(bank.getAccountNumber());
            dto.setAccountHolderName(bank.getAccountHolderName());
            dto.setBankName(bank.getBankName());
            dto.setIfscCode(bank.getIfscCode());
            dto.setBranchName(bank.getBranchName());
        }

        // Include Client Info
        if (employee.getClient() != null) {
            dto.setClientId(employee.getClient().getClientId());
            dto.setClientName(employee.getClient().getCompanyName());
        }

        // Include Enum designation as String
        if (employee.getDesignation() != null) {
            dto.setDesignation(employee.getDesignation().name());
        }

        // Include Salary, Additional Details, Employment, Insurance, Equipment, Statutory Details
        dto.setEmployeeSalaryDTO(salaryDTO);
        dto.setEmployeeAdditionalDetailsDTO(toDTO(additionalDetails));

        if (employmentDetails != null) {
            dto.setEmployeeEmploymentDetailsDTO(employeeEmploymentDetailsService.toDTO(employmentDetails));
        }

        if (insuranceDetails != null) {
            dto.setEmployeeInsuranceDetailsDTO(employeeInsuranceDetailsService.toDTO(insuranceDetails));
        }

        if (equipmentList != null && !equipmentList.isEmpty()) {
            List<EmployeeEquipmentDTO> equipmentDTOs = new ArrayList<>();
            equipmentList.forEach(equipment -> equipmentDTOs.add(employeeEquipmentService.toDTO(equipment)));
            dto.setEmployeeEquipmentDTO(equipmentDTOs);
        }

        if (statutoryDetails != null) {
            dto.setEmployeeStatutoryDetailsDTO(employeeStatutoryDetailsService.toDTO(statutoryDetails));
        }

        log.info("Employee DTO {}", dto);
        return dto;
    }

    // -------------------- Employee Update from Model --------------------

    /**
     * Update an Employee entity with values from EmployeeModel.
     * Handles nested objects like BankDetails, Addresses, Documents, and other associated entities.
     */
    public void updateEmployeeFromModel(Employee employee, EmployeeModel model) {
        // ---------- Basic Info ----------
        updateIfNotNull(model.getFirstName(), employee::setFirstName);
        updateIfNotNull(model.getLastName(), employee::setLastName);
        updateIfNotNull(model.getPersonalEmail(), employee::setPersonalEmail);
        updateIfNotNull(model.getCompanyEmail(), employee::setCompanyEmail);
        updateIfNotNull(model.getContactNumber(), employee::setContactNumber);
        updateIfNotNull(model.getNationality(), employee::setNationality);
        updateIfNotNull(model.getEmergencyContactName(), employee::setEmergencyContactName);
        updateIfNotNull(model.getEmergencyContactNumber(), employee::setEmergencyContactNumber);
        updateIfNotNull(model.getSkillsAndCertification() ,employee::setSkillsAndCertification);
        updateIfNotNull(model.getRemarks() , employee::setRemarks);

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

        // ---------- Address Update ----------
        addressService.updateAddresses(employee.getEmployeeId(), model.getAddresses());

        // ---------- Bank Details Update ----------
        if (model.getAccountNumber() != null || model.getAccountHolderName() != null
                || model.getBankName() != null || model.getIfscCode() != null || model.getBranchName() != null) {

            BankDetails bankDetails = employee.getBankDetails();
            if (bankDetails == null) {
                bankDetails = new BankDetails();
                employee.setBankDetails(bankDetails);
            }

            updateIfNotNull(model.getAccountNumber(), bankDetails::setAccountNumber);
            updateIfNotNull(model.getAccountHolderName(), bankDetails::setAccountHolderName);
            updateIfNotNull(model.getBankName(), bankDetails::setBankName);
            updateIfNotNull(model.getIfscCode(), bankDetails::setIfscCode);
            updateIfNotNull(model.getBranchName(), bankDetails::setBranchName);
        }

        // ---------- Documents ----------
        if (model.getDocuments() != null && !model.getDocuments().isEmpty()) {
            model.getDocuments().forEach(docDTO ->
                    employeeDocumentService.updateDocumentById(docDTO.getDocumentId(), employee.getEmployeeId(), docDTO));
        }

        // ---------- Additional Details ----------
        if (model.getEmployeeAdditionalDetailsDTO() != null) {
            employeeAdditionalDetailsService.updateAdditionalDetails(model.getEmployeeAdditionalDetailsDTO(), employee.getEmployeeId());
        }

        // ---------- Salary ----------
        if (model.getEmployeeSalaryDTO() != null) {
            employeeSalaryService.updateSalaryDetails(model.getEmployeeSalaryDTO(), employee);
        }

        // ---------- Employment Details ----------
        if (model.getEmployeeEmploymentDetailsDTO() != null) {
            employeeEmploymentDetailsService.updateEmploymentDetails(employee.getEmployeeId(), model.getEmployeeEmploymentDetailsDTO());
        }

        // ---------- Insurance Details ----------
        if (model.getEmployeeInsuranceDetailsDTO() != null) {
            employeeInsuranceDetailsService.updateInsuranceDetails(employee.getEmployeeId(), model.getEmployeeInsuranceDetailsDTO());
        }

        // ---------- Equipment Details ----------
        if (model.getEmployeeEquipmentDTO() != null) {
            employeeEquipmentService.updateEquipment(model.getEmployeeEquipmentDTO(), employee.getEmployeeId());
        }

        // ---------- Statutory Details ----------
        if (model.getEmployeeStatutoryDetailsDTO() != null) {
            employeeStatutoryDetailsService.updateStatutoryDetails(employee.getEmployeeId(), model.getEmployeeStatutoryDetailsDTO());
        }

        // ---------- Timestamp ----------
        employee.setUpdatedAt(java.time.LocalDateTime.now());
    }

    // -------------------- Utility --------------------

    /**
     * Update a field only if the value is not null
     */
    private <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    // -------------------- Credentials DTO --------------------
    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class Credentials {
        private final String companyId;
        private final String rawPassword;
        private final String encryptedPassword;
    }

}
