package com.EmpTimeHub.specification;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.constants.EnumConstants.LeaveType;
import com.EmpTimeHub.entity.EmployeeLeave;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Specification builder for dynamic filtering of EmployeeLeave entities.
 * <p>
 * Supports filtering by employee, month, leave type, and leave status.
 * Used with Spring Data JPA repositories to build dynamic queries.
 */
public class LeaveSpecifications {

    /**
     * Filters leaves by a specific employee.
     *
     * @param employeeId UUID of the employee.
     * @return Specification<EmployeeLeave> for filtering by employee.
     */
    public static Specification<EmployeeLeave> byEmployee(UUID employeeId) {
        return (root, query, cb) ->
                cb.equal(root.get("employee").get("employeeId"), employeeId);
    }


    public static Specification<EmployeeLeave> byManager(UUID managerId) {
        return (root, query, cb) -> cb.equal(
                root.get("employee").get("reportingManager").get("employeeId"),
                managerId
        );
    }

    /**
     * Filters leaves by a specific month.
     *
     * @param yearMonthStr Year and month in "yyyy-MM" format.
     * @return Specification<EmployeeLeave> for filtering leaves within the month.
     * Returns an empty (conjunction) filter if input is null/blank,
     * and disjunction if parsing fails.
     */
    public static Specification<EmployeeLeave> byMonth(String yearMonthStr) {
        return (root, query, cb) -> {
            if (yearMonthStr == null || yearMonthStr.isBlank()) return cb.conjunction();
            try {
                YearMonth ym = YearMonth.parse(yearMonthStr);
                LocalDate start = ym.atDay(1);
                LocalDate end = ym.atEndOfMonth();
                return cb.between(root.get("fromDate"), start, end);
            } catch (Exception e) {
                // Consider logging the parse error in real implementation
                return cb.disjunction();
            }
        };
    }

    /**
     * Filters leaves by leave type (PAID, UNPAID, etc.).
     *
     * @param type String representing leave type.
     * @return Specification<EmployeeLeave> for filtering by type.
     * Returns conjunction if input is null/empty, disjunction if invalid.
     */
    public static Specification<EmployeeLeave> byType(String type) {
        return (root, query, cb) -> {
            if (type == null || type.isEmpty()) return cb.conjunction();
            try {
                LeaveType leaveType = LeaveType.valueOf(type.toUpperCase());
                return cb.equal(root.get("type"), leaveType);
            } catch (IllegalArgumentException e) {
                // Consider logging invalid leave type
                return cb.disjunction();
            }
        };
    }

    /**
     * Filters leaves by leave status (PENDING, APPROVED, REJECTED, etc.).
     *
     * @param status String representing leave status.
     * @return Specification<EmployeeLeave> for filtering by status.
     * Returns conjunction if input is null/blank, disjunction if invalid.
     */
    public static Specification<EmployeeLeave> byStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return cb.conjunction();
            try {
                EnumConstants.LeaveStatus leaveStatus = EnumConstants.LeaveStatus.valueOf(status.toUpperCase());
                return cb.equal(root.get("status"), leaveStatus);
            } catch (IllegalArgumentException e) {
                // Consider logging invalid leave status
                return cb.disjunction();
            }
        };
    }
}
