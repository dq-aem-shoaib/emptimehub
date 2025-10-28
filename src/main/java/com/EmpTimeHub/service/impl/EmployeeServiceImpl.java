package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.mapper.EmployeeMapper;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.service.*;
import com.EmpTimeHub.service.helper.EmployeeServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    // -------------------- Repositories & Helpers --------------------
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeServiceHelper serviceHelper;

    private final EmployeeSalaryService employeeSalaryService;
    private final EmployeeEmploymentDetailsService employeeEmploymentDetailsService;
    private final EmployeeInsuranceDetailsService employeeInsuranceDetailsService;
    private final EmployeeEquipmentService employeeEquipmentService;
    private final EmployeeStatutoryDetailsService employeeStatutoryDetailsService;

    // -------------------- Add Employee --------------------
    /**
     * Adds a new employee along with all related entities like salary, documents, addresses, etc.
     *
     * @param employeeModel the employee input model
     * @return the saved EmployeeDTO
     */
    @Transactional
    @Override
    public EmployeeDTO addEmployee(EmployeeModel employeeModel) {

        // 1. Generate credentials
        EmployeeServiceHelper.Credentials creds = serviceHelper.generateCredentials();

        // 2. Save User entity
        User savedUser = serviceHelper.saveUser(
                employeeModel.getDesignation(),
                employeeModel.getCompanyEmail(),
                creds.getCompanyId(),
                creds.getEncryptedPassword()
        );

        // 3. Fetch related entities (Client and Reporting Manager)
        Client client = serviceHelper.getClient(employeeModel.getClientId());
        Employee reportingManager = serviceHelper.getReportingManager(employeeModel.getReportingManagerId());

        // 4. Build BankDetails entity
        BankDetails bankDetails = serviceHelper.buildBankDetails(employeeModel);

        // 5. Map EmployeeModel to Employee entity
        Employee employee = employeeMapper.toEntity(
                employeeModel, savedUser, client, reportingManager, bankDetails, creds.getCompanyId()
        );

        Employee savedEmployee = employeeRepository.save(employee);

        // 6. Save related entities
        saveRelatedEntities(savedEmployee, employeeModel);

        // 7. Convert to DTO and return
        return serviceHelper.toEmployeeDTO(savedEmployee);
    }

    /**
     * Saves all related entities of an employee like addresses, documents, salary, and additional details.
     *
     * @param savedEmployee the saved Employee entity
     * @param employeeModel the Employee input model
     */
    private void saveRelatedEntities(Employee savedEmployee, EmployeeModel employeeModel) {

        // Save addresses
        serviceHelper.saveAddresses(savedEmployee.getEmployeeId(), employeeModel.getAddresses());

        // Save documents
        serviceHelper.processDocument(employeeModel.getDocuments(), savedEmployee.getEmployeeId());

        // Save salary details
        employeeSalaryService.saveSalaryDetails(employeeModel.getEmployeeSalaryDTO(), savedEmployee);

        // Save optional details
        if (employeeModel.getEmployeeAdditionalDetailsDTO() != null) {
            serviceHelper.saveAdditionalDetails(employeeModel.getEmployeeAdditionalDetailsDTO(),
                    savedEmployee.getEmployeeId());
        }

        if (employeeModel.getEmployeeEmploymentDetailsDTO() != null) {
            employeeEmploymentDetailsService.saveEmploymentDetails(
                    employeeModel.getEmployeeEmploymentDetailsDTO(),
                    savedEmployee.getEmployeeId()
            );
        }

        if (employeeModel.getEmployeeInsuranceDetailsDTO() != null) {
            employeeInsuranceDetailsService.saveInsuranceDetails(
                    employeeModel.getEmployeeInsuranceDetailsDTO(),
                    savedEmployee.getEmployeeId()
            );
        }

        if (employeeModel.getEmployeeEquipmentDTO() != null) {
            employeeEquipmentService.saveEquipment(
                    employeeModel.getEmployeeEquipmentDTO(),
                    savedEmployee.getEmployeeId()
            );
        }

        if (employeeModel.getEmployeeStatutoryDetailsDTO() != null) {
            employeeStatutoryDetailsService.saveStatutoryDetails(
                    employeeModel.getEmployeeStatutoryDetailsDTO(),
                    savedEmployee.getEmployeeId()
            );
        }
    }

    // -------------------- Get Employee --------------------
    /**
     * Fetches an employee by ID.
     *
     * @param empId employee UUID
     * @return EmployeeDTO
     */
    @Override
    public EmployeeDTO getEmployeeById(UUID empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return serviceHelper.toEmployeeDTO(employee);
    }

    /**
     * Fetches all employees.
     *
     * @return list of EmployeeDTO
     */
    @Override
    public List<EmployeeDTO> getAllEmployee() {
        return employeeRepository.findAll()
                .stream()
                .map(serviceHelper::toEmployeeDTO)
                .toList();
    }

    // -------------------- Update Employee --------------------
    /**
     * Updates an employee by ID using values from EmployeeModel.
     *
     * @param empId         employee UUID
     * @param employeeModel input model
     */
    @Override
    public void updateEmployeeById(UUID empId, EmployeeModel employeeModel) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Delegate update logic to helper
        serviceHelper.updateEmployeeFromModel(employee, employeeModel);
        employeeRepository.save(employee);
    }

    // -------------------- Soft Delete Employee --------------------
    /**
     * Marks an employee as INACTIVE (soft delete).
     *
     * @param empId employee UUID
     */
    @Override
    public void removeEmployeeById(UUID empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if ("ACTIVE".equals(employee.getStatus())) {
            employee.setStatus("INACTIVE");
            employeeRepository.save(employee);
        }
    }

    // -------------------- Unassign Employee from Client --------------------
    /**
     * Unassigns an employee from their client.
     *
     * @param empId employee UUID
     */
    @Override
    public void unassignEmployeeFromClient(UUID empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setClient(null);
        employeeRepository.save(employee);
    }

    // -------------------- Find Employees by Designation --------------------
    /**
     * Finds employees by designation.
     *
     * @param designation EnumConstants.Designation
     * @return list of EmployeeDTO
     */
    @Override
    public List<EmployeeDTO> findByDesignation(EnumConstants.Designation designation) {
        List<Employee> employees = employeeRepository.findByDesignation(designation);

        return employees.stream()
                .map(employee -> {
                    EmployeeDTO dto = new EmployeeDTO();
                    BeanUtils.copyProperties(employee, dto);

                    // Handle fields not copied by BeanUtils
                    if (employee.getClient() != null) {
                        dto.setClientId(employee.getClient().getClientId());
                        dto.setClientName(employee.getClient().getCompanyName());
                    }

                    return dto;
                })
                .toList();
    }

    @Override
    public List<EmployeeDTO> getEmployeesUnderManager(UUID managerUserId) {
        log.info("Fetching employees under manager userId: {}", managerUserId);

        //  Identify the manager as an Employee
        Employee manager = employeeRepository.findByUser_UserId(managerUserId)
                .orElseThrow(() -> {
                    log.error("Manager not found for userId: {}", managerUserId);
                    return new RuntimeException("Manager not found for user ID: " + managerUserId);
                });

        log.debug("Manager found: {} {} (EmployeeId: {})", manager.getFirstName(), manager.getLastName(), manager.getEmployeeId());

        // Fetch all employees reporting to this manager
        List<Employee> employees = employeeRepository.findByReportingManager_EmployeeId(manager.getEmployeeId());
        log.info("Found {} employees under manager {}", employees.size(), manager.getEmployeeId());

        // Convert to DTOs
        List<EmployeeDTO> employeeDTOs = employees.stream()
                .map(serviceHelper::toEmployeeDTO)
                .collect(Collectors.toList());

        log.info("Converted {} employees to DTOs successfully", employeeDTOs.size());

        return employeeDTOs;
    }
}
