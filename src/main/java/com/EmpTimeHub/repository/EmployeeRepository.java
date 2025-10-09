package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,String> {
    Optional<Employee> findByUser_UserId(UUID userId);
}
