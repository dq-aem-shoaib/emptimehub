package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.repository.*;
import com.EmpTimeHub.service.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    @Transactional
    @Override
    public Employee addEmployee(EmployeeModel employeeModel) {

        //Generate username & password
        String username = employeeModel.getFirstName()
                .toLowerCase()
                .replaceAll("\\s+", "")
                + employeeModel.getAadharNumber().substring(employeeModel.getAadharNumber().length() - 4);

        String rawPassword = "Emp@" + employeeModel.getAadharNumber().
                substring(employeeModel.getAadharNumber().length() - 4);
        String encryptedPassword = passwordEncoder.encode(rawPassword);

        log.info("Employee added with username: {} and Password: {}",username,rawPassword);

        //  Save User
        User user = User.builder()
                .userName(username)
                .companyEmail(employeeModel.getCompanyEmail())
                .password(encryptedPassword)
                .role(EnumConstants.Role.EMPLOYEE)
                .build();
        User savedUser = userRepository.save(user);

        // Fetch Client
        Client client = null;
        if (employeeModel.getClientId() != null) {
            client = clientRepository.findById(employeeModel.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
        }

        //  Build Address entity (not yet saved)
        Address address = Address.builder()
                .houseNo(employeeModel.getHouseNo())
                .streetName(employeeModel.getStreetName())
                .city(employeeModel.getCity())
                .state(employeeModel.getState())
                .country(employeeModel.getCountry())
                .pincode(employeeModel.getPinCode())
                .build();

        // Build BankDetails
        BankDetails bankDetails = BankDetails.builder()
                .accountNumber(employeeModel.getAccountNumber())
                .bankName(employeeModel.getBankName())
                .ifscCode(employeeModel.getIfscCode())
                .branchName(employeeModel.getBranchName())
                .accountHolderName(employeeModel.getAccountHolderName())
                .build();

        //  Build Employee entity
        Employee employee = Employee.builder()
                .user(savedUser)
                .client(client)
                .address(address)      // Cascaded save
                .bankDetails(bankDetails)
                .firstName(employeeModel.getFirstName())
                .lastName(employeeModel.getLastName())
                .personalEmail(employeeModel.getPersonalEmail())
                .companyEmail(employeeModel.getCompanyEmail())
                .contactNumber(employeeModel.getContactNumber())
                .currency(employeeModel.getCurrency())
                .dateOfBirth(employeeModel.getDateOfBirth())
                .dateOfJoining(employeeModel.getDateOfJoining())
                .designation(employeeModel.getDesignation())
                .rateCard(employeeModel.getRateCard())
                .panNumber(employeeModel.getPanNumber())
                .availableLeaves(12)
                .aadharNumber(employeeModel.getAadharNumber())
                .panCardUrl(employeeModel.getPanCardUrl())
                .aadharCardUrl(employeeModel.getAadharCardUrl())
                .bankPassbookUrl(employeeModel.getBankPassbookUrl())
                .tenthCftUrl(employeeModel.getTenthCftUrl())
                .interCftUrl(employeeModel.getInterCftUrl())
                .degreeCftUrl(employeeModel.getDegreeCftUrl())
                .postGraduationCftUrl(employeeModel.getPostGraduationCftUrl())
                .status("ACTIVE")
                .build();

        //  Save Employee (address will be saved automatically because of cascade)
        return employeeRepository.save(employee);
    }


    @Override
    public Employee getEmployeeById(UUID empId){
        return  employeeRepository.findById(empId).orElseThrow(() -> new RuntimeException("Employee not found"));

    }

    @Override
    public List<EmployeeDTO> getAllEmployee() {
        List<Employee> employeeList = employeeRepository.findAll();

        return employeeList.stream().map(employee -> {
            EmployeeDTO dto = new EmployeeDTO();
            BeanUtils.copyProperties(employee, dto);

            // Handle fields that BeanUtils cannot copy or lazy-loaded proxies
            if (employee.getClient() != null) {
                dto.setClientId(employee.getClient().getClientId());
                dto.setClientName(employee.getClient().getCompanyName());
            }

            return dto;
        }).toList();
    }

    @Override
    public void updateEmployeeById(UUID empId, EmployeeModel employeeModel) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not Found"));

        // ---------- Basic Info ----------
        if (employeeModel.getFirstName() != null) employee.setFirstName(employeeModel.getFirstName());
        if (employeeModel.getLastName() != null) employee.setLastName(employeeModel.getLastName());
        if (employeeModel.getPersonalEmail() != null) employee.setPersonalEmail(employeeModel.getPersonalEmail());
        if (employeeModel.getCompanyEmail() != null) employee.setCompanyEmail(employeeModel.getCompanyEmail());
        if (employeeModel.getContactNumber() != null) employee.setContactNumber(employeeModel.getContactNumber());

        // ---------- Associations ----------
        UUID clientId = employeeModel.getClientId();
        if (clientId != null) {
            Client client = clientRepository.findById(clientId).orElseThrow(() -> new RuntimeException("client not found"));
            employee.setClient(client);
        }

        // ---------- Job Info ----------
        if (employeeModel.getDesignation() != null) employee.setDesignation(employeeModel.getDesignation());
        if (employeeModel.getDateOfBirth() != null) employee.setDateOfBirth(employeeModel.getDateOfBirth());
        if (employeeModel.getDateOfJoining() != null) employee.setDateOfJoining(employeeModel.getDateOfJoining());
        if (employeeModel.getCurrency() != null) employee.setCurrency(employeeModel.getCurrency());
        if (employeeModel.getRateCard() != null) employee.setRateCard(employeeModel.getRateCard());

        // ---------- Identification ----------
        if (employeeModel.getPanNumber() != null) employee.setPanNumber(employeeModel.getPanNumber());
        if (employeeModel.getAadharNumber() != null) employee.setAadharNumber(employeeModel.getAadharNumber());

        // ---------- Bank Details ----------
        BankDetails bankDetails = bankDetailsRepository.findById(employee.getBankDetails().getBankAccountId()).
                orElseThrow(() -> new RuntimeException("bank details  not found"));

        if (employeeModel.getAccountNumber() != null) bankDetails.setAccountNumber(employeeModel.getAccountNumber());
        if (employeeModel.getAccountHolderName() != null) bankDetails.setAccountHolderName(employeeModel.getAccountHolderName());
        if (employeeModel.getBankName() != null) bankDetails.setBankName(employeeModel.getBankName());
        if (employeeModel.getIfscCode() != null) bankDetails.setIfscCode(employeeModel.getIfscCode());
        if (employeeModel.getBranchName() != null) bankDetails.setBranchName(employeeModel.getBranchName());

        // ---------- Address ----------
        Address address = addressRepository.findById(employee.getAddress().getAddressId()).
                orElseThrow(() -> new RuntimeException("address not found"));
        if (employeeModel.getHouseNo() != null) address.setHouseNo(employeeModel.getHouseNo());
        if (employeeModel.getStreetName() != null) address.setStreetName(employeeModel.getStreetName());
        if (employeeModel.getCity() != null) address.setCity(employeeModel.getCity());
        if (employeeModel.getState() != null) address.setState(employeeModel.getState());
        if (employeeModel.getPinCode() != null) address.setPincode(employeeModel.getPinCode());
        if (employeeModel.getCountry() != null) address.setCountry(employeeModel.getCountry());
        addressRepository.save(address);



        // ---------- Document URLs ----------
        if (employeeModel.getPanCardUrl() != null) employee.setPanCardUrl(employeeModel.getPanCardUrl());
        if (employeeModel.getAadharCardUrl() != null) employee.setAadharCardUrl(employeeModel.getAadharCardUrl());
        if (employeeModel.getBankPassbookUrl() != null) employee.setBankPassbookUrl(employeeModel.getBankPassbookUrl());
        if (employeeModel.getTenthCftUrl() != null) employee.setTenthCftUrl(employeeModel.getTenthCftUrl());
        if (employeeModel.getInterCftUrl() != null) employee.setInterCftUrl(employeeModel.getInterCftUrl());
        if (employeeModel.getDegreeCftUrl() != null) employee.setDegreeCftUrl(employeeModel.getDegreeCftUrl());
        if (employeeModel.getPostGraduationCftUrl() != null) employee.setPostGraduationCftUrl(employeeModel.getPostGraduationCftUrl());

        // Update timestamp
        employee.setUpdatedAt(LocalDateTime.now());


        employeeRepository.save(employee);
    }

    @Override
    public void removeEmployeeById(UUID empId){
        Employee employee = employeeRepository.findById(empId).get();
        if(employee.getStatus().equals("ACTIVE")){
            employee.setStatus("INACTIVE");
            employeeRepository.save(employee);
        }
    }

    @Override
    public void unassignEmployeeFromClient(UUID empId){
        Employee employee = employeeRepository.findById(empId).
                orElseThrow(() -> new RuntimeException("Employee not Found"));
        employee.setClient(null);
        employeeRepository.save(employee);
    }

}
