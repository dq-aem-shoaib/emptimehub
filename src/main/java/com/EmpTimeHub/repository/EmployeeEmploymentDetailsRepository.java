package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EmployeeEmploymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeEmploymentDetailsRepository extends JpaRepository<EmployeeEmploymentDetails , UUID> {
    Optional<EmployeeEmploymentDetails> findByEmployee_EmployeeId(UUID employeeId);
}
