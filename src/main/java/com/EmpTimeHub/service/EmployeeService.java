package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.ClientDTO;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.model.EmployeeModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    @Transactional
        // <- This ensures atomicity
    Employee addEmployee(EmployeeModel employeeModel);

    Employee getEmployeeById(UUID empId);
    List<EmployeeDTO> getAllEmployee();
    void updateEmployeeById(UUID empId, EmployeeModel employeeModel);


    void removeEmployeeById(UUID empId);

    void unassignEmployeeFromClient(UUID empId);
}
