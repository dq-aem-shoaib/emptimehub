package com.EmpTimeHub.repository;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,UUID> {
    Optional<Employee> findByUser_UserId(UUID userId);
    @Query("FROM Employee e WHERE e.companyEmail = :gmail")
    Employee getEmployeeByEmail(@Param("gmail") String email);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.reportingManager WHERE e.employeeId = :id")
    Optional<Employee> findByIdWithManager(@Param("id") UUID id);

    List<Employee> findByDesignation(EnumConstants.Designation designation);
    Optional<Employee> findFirstByOrderByCreatedAtDesc();

    /**
     * Finds all employees who report to the given manager.
     *
     * @param reportingManagerId UUID of the reporting manager
     * @return List of employees who report to this manager
     */
    List<Employee> findByReportingManager_EmployeeId(UUID reportingManagerId);
}
