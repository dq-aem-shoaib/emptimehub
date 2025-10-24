package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.EmployeeSalaryDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.EmployeeSalary;

public interface EmployeeSalaryService {
    EmployeeSalary saveSalaryDetails(EmployeeSalaryDTO dto, Employee employee);

    EmployeeSalaryDTO getSalaryDetailsForEmployee(Employee employee);

    EmployeeSalary updateSalaryDetails(EmployeeSalaryDTO dto, Employee employee);
}
