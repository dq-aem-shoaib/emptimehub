package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EmployeeAdditionalDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeAdditionalDetailsRepository extends JpaRepository<EmployeeAdditionalDetails, UUID> {
    Optional<EmployeeAdditionalDetails> findByEmployee_EmployeeId(UUID employeeId);
}
