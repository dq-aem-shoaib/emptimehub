package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EmployeeStatutoryDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeStatutoryDetailsRepository extends JpaRepository<EmployeeStatutoryDetails, UUID> {
    Optional<EmployeeStatutoryDetails> findByEmployee_EmployeeId(UUID employeeId);
}
