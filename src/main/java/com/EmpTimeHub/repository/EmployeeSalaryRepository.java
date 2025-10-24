package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EmployeeSalary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, UUID> {

    Optional<EmployeeSalary> findByEmployeeEmployeeId(UUID employeeId);

}
