package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EmployeeInsuranceDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeInsuranceDetailsRepository extends JpaRepository<EmployeeInsuranceDetails, UUID> {
    Optional<EmployeeInsuranceDetails> findByEmployee_EmployeeId(UUID employeeId);
}
