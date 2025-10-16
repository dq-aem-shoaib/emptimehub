package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,UUID> {
    Optional<Employee> findByUser_UserId(UUID userId);
    @Query("FROM Employee e WHERE e.companyEmail = :gmail")
    Employee getEmployeeByEmail(@Param("gmail") String email);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.reportingManager WHERE e.employeeId = :id")
    Optional<Employee> findByIdWithManager(@Param("id") UUID id);

}
