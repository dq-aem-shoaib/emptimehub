package com.EmpTimeHub.specification;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.constants.EnumConstants.LeaveCategory;
import com.EmpTimeHub.entity.EmployeeLeave;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
     * Builds a JPA Specification to filter EmployeeLeave entities by their financial type.
     *
     * <p>Financial type refers to whether the leave is PAID or UNPAID.</p>
     *
     * @param financialType the financial type to filter by (e.g., "PAID" or "UNPAID")
     * @return a Specification<EmployeeLeave> that filters by the given financial type
     */
    public static Specification<EmployeeLeave> byFinancialType(String financialType) {
        return (root, query, cb) -> {
            try {
                if (financialType == null || financialType.isBlank()) {
                    log.warn("byFinancialType called with null or blank financialType, ignoring filter");
                    return cb.conjunction(); // no filtering if input invalid
                }
                log.debug("Applying financialType filter: {}", financialType);
                return cb.equal(root.get("financialType"), financialType);
            } catch (Exception e) {
                log.error("Error applying financialType filter for value '{}'", financialType, e);
                return cb.conjunction(); // fallback to no filtering
            }
        };
    }

    /**
     * Builds a JPA Specification to filter EmployeeLeave entities by their leave category.
     *
     * <p>Leave category refers to the type of leave selected by the employee, e.g., SICK, CASUAL, PLANNED, UNPLANNED.</p>
     *
     * @param leaveCategory the leave category to filter by (e.g., "SICK", "CASUAL")
     * @return a Specification<EmployeeLeave> that filters by the given leave category
     */
    public static Specification<EmployeeLeave> byLeaveCategory(String leaveCategory) {
        return (root, query, cb) -> {
            try {
                if (leaveCategory == null || leaveCategory.isBlank()) {
                    log.warn("byLeaveCategory called with null or blank leaveCategory, ignoring filter");
                    return cb.conjunction(); // no filtering if input invalid
                }
                log.debug("Applying leaveCategory filter: {}", leaveCategory);
                return cb.equal(root.get("leaveCategory"), leaveCategory);
            } catch (Exception e) {
                log.error("Error applying leaveCategory filter for value '{}'", leaveCategory, e);
                return cb.conjunction(); // fallback to no filtering
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

    /**
     * Filters leaves that are approved and in the future (including today).
     * <p>
     * SQL equivalent: <br>
     * <code>WHERE status = 'APPROVED' AND from_date >= CURRENT_DATE</code>
     *
     * @return Specification<EmployeeLeave> for future approved leaves
     */
    public static Specification<EmployeeLeave> futureApprovedLeaves() {
        LocalDate today = LocalDate.now();
        log.debug("Applying futureApprovedLeaves specification with today={}", today);

        return (root, query, cb) -> cb.and(
                cb.equal(root.get("status"), EnumConstants.LeaveStatus.APPROVED),
                cb.greaterThanOrEqualTo(root.get("fromDate"), today)
        );
    }

    /**
     * Filters leaves that include a specific date (to check if employee is on leave on that day).
     * <p>
     * This will match any leave where:
     * <ul>
     *     <li>fromDate <= date <= toDate</li>
     *     <li>status = APPROVED</li>
     * </ul>
     * <p>
     * SQL equivalent: <br>
     * <code>WHERE from_date <= :date AND to_date >= :date AND status = 'APPROVED'</code>
     *
     * @param date LocalDate to check if employee is on leave
     * @return Specification<EmployeeLeave> for leaves covering the given date
     */
    public static Specification<EmployeeLeave> leavesOnDate(LocalDate date) {
        log.debug("Applying leavesOnDate specification for date={}", date);

        return (root, query, cb) -> cb.and(
                cb.lessThanOrEqualTo(root.get("fromDate"), date),
                cb.greaterThanOrEqualTo(root.get("toDate"), date),
                cb.equal(root.get("status"), EnumConstants.LeaveStatus.APPROVED)
        );
    }
}
