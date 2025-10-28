package com.EmpTimeHub.service;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.model.EmployeeModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    @Transactional
        // <- This ensures atomicity
    EmployeeDTO addEmployee(EmployeeModel employeeModel);

    EmployeeDTO getEmployeeById(UUID empId);
    List<EmployeeDTO> getAllEmployee();
    void updateEmployeeById(UUID empId, EmployeeModel employeeModel);


    void removeEmployeeById(UUID empId);

    void unassignEmployeeFromClient(UUID empId);

    List<EmployeeDTO> findByDesignation(EnumConstants.Designation designation);
    /**
     * Fetches all employees reporting to the specified manager.
     *
     * @param managerUserId the UUID of the manager's user account
     * @return list of {@link EmployeeDTO} representing employees under that manager
     */
    List<EmployeeDTO> getEmployeesUnderManager(UUID managerUserId);
}
