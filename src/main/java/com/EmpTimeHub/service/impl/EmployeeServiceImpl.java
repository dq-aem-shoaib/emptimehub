package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.generator.CredentialGenerator;
import com.EmpTimeHub.mapper.EmployeeMapper;
import com.EmpTimeHub.model.AddressModel;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.repository.*;
import com.EmpTimeHub.service.AddressService;
import com.EmpTimeHub.service.EmployeeService;
import com.EmpTimeHub.service.helper.EmployeeServiceHelper;
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
    private final EmployeeMapper employeeMapper;
    private final EmployeeServiceHelper serviceHelper;


    @Transactional
    @Override
    public EmployeeDTO addEmployee(EmployeeModel employeeModel) {

        //  Generate credentials
        EmployeeServiceHelper.Credentials creds = serviceHelper.generateCredentials();

        //  Save user
        User savedUser = serviceHelper.saveUser(
                employeeModel.getDesignation(),
                employeeModel.getCompanyEmail(),
                creds.getCompanyId(),
                creds.getEncryptedPassword()
        );

        //  Fetch related entities
        Client client = serviceHelper.getClient(employeeModel.getClientId());
        Employee reportingManager = serviceHelper.getReportingManager(employeeModel.getReportingManagerId());

        //  Build BankDetails
        BankDetails bankDetails = serviceHelper.buildBankDetails(employeeModel);

        //  Build and save Employee
        Employee employee = employeeMapper.toEntity(
                employeeModel, savedUser, client, reportingManager, bankDetails, creds.getCompanyId()
        );
        Employee savedEmployee = employeeRepository.save(employee);

        //  Save addresses
        serviceHelper.saveAddresses(savedEmployee.getEmployeeId(), employeeModel.getAddresses());

        //  Convert to DTO and return
        return serviceHelper.toEmployeeDTO(savedEmployee);
    }



    @Override
    public EmployeeDTO getEmployeeById(UUID empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Delegate DTO conversion to existing helper
        return serviceHelper.toEmployeeDTO(employee);
    }

    @Override
    public List<EmployeeDTO> getAllEmployee() {
        return employeeRepository.findAll()
                .stream()
                .map(serviceHelper::toEmployeeDTO)
                .toList();
    }


    @Override
    public void updateEmployeeById(UUID empId, EmployeeModel employeeModel) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        // Delegate all update logic to serviceHelper
        serviceHelper.updateEmployeeFromModel(employee, employeeModel);

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

    @Override
    public List<EmployeeDTO> findByDesignation(EnumConstants.Designation designation){
        List<Employee> employees = employeeRepository.findByDesignation(designation);

        return employees.stream().map(employee -> {
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

}
