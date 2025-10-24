package com.EmpTimeHub.repository;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, UUID>,
        JpaSpecificationExecutor<EmployeeLeave> {

    /**
     * Retrieves all non-withdrawn leaves with the given status for a specific manager.
     * The associated Employee is eagerly fetched, and results are ordered by creation date descending.
     *
     * @param managerId the manager's employee ID
     * @param status the leave status to filter by (e.g., PENDING)
     * @return list of EmployeeLeave matching the criteria
     */

    @Query("SELECT el FROM EmployeeLeave el " +
            "WHERE el.reportingManager.employeeId = :managerId " +
            "AND el.status = :status " +
            "AND el.withdrawn = false " +
            "ORDER BY el.createdAt DESC")
    List<EmployeeLeave> findPendingLeavesByManager(
            @Param("managerId") UUID managerId,
            @Param("status") EnumConstants.LeaveStatus status
    );

    /**
     * Retrieves approved leaves for an employee within a given date range.
     *
     * @param employee the employee whose leaves to fetch
     * @param status the leave status to filter by
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of EmployeeLeave matching criteria
     */
    List<EmployeeLeave> findByEmployeeAndStatusAndFromDateBetween(
            Employee employee,
            EnumConstants.LeaveStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Finds all leave records matching the given status.
     * Used mainly by admin to fetch all pending, approved, or rejected leaves.
     *
     * @param status the leave status to filter by
     * @return list of leaves with the specified status
     */
    List<EmployeeLeave> findByStatus(EnumConstants.LeaveStatus status);

}

