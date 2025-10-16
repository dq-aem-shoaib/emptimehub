package com.EmpTimeHub.service;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.DateRangeRequestDTO;
import com.EmpTimeHub.dto.LeaveRequestDTO;
import com.EmpTimeHub.dto.LeaveResponseDTO;
import com.EmpTimeHub.dto.WorkdayResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.UUID;

/**
 * Service interface for managing employee leave requests.
 */
public interface EmployeeLeaveService {

    /**
     * Applies a new leave request for the authenticated employee.
     *
     * @param request DTO containing leave details.
     * @param email   Email of the employee applying for leave.
     * @return LeaveResponseDTO containing the saved leave details.
     */
    LeaveResponseDTO applyLeave(LeaveRequestDTO request, String email);

    /**
     * Retrieves a paginated list of leaves with optional filters such as employeeId, month, type, and status.
     *
     * @param employeeId Optional employee UUID to filter leaves (ADMIN only).
     * @param month      Optional month filter in format "yyyy-MM".
     * @param type       Optional leave type filter.
     * @param status     Optional leave status filter.
     * @param page       Page number for pagination.
     * @param size       Page size for pagination.
     * @param sort       Sorting criteria in the format "field,direction".
     * @param user       Authenticated user details.
     * @return Page of LeaveResponseDTO containing leave details.
     */
    Page<LeaveResponseDTO> getLeaves(UUID employeeId, String month, String type, String status,
                                     int page, int size, String sort, UserDetails user);

    /**
     * Fetches a single leave by its ID for the authenticated employee.
     *
     * @param leaveId UUID of the leave to fetch.
     * @param email   Email of the employee requesting the leave.
     * @return LeaveResponseDTO containing the leave details.
     */
    LeaveResponseDTO getLeaveById(UUID leaveId, String email);

    /**
     * Updates an existing leave request for the authenticated employee.
     *
     * @param request DTO containing updated leave details.
     * @param email   Email of the employee updating the leave.
     * @return LeaveResponseDTO containing the updated leave details.
     */
    LeaveResponseDTO updateLeave(LeaveRequestDTO request, String email);

    /**
     * Deletes a leave request for the authenticated employee by its ID.
     *
     * @param leaveId UUID of the leave to delete.
     * @param email   Email of the employee requesting deletion.
     */
    void deleteLeave(UUID leaveId, String email);

    /**
     * Updates the status of a leave request (APPROVED/REJECTED) by an admin
     * and sends an email notification to the employee.
     *
     * @param leaveId      ID of the leave to update
     * @param status       new leave status
     * @param managerComment optional admin comment
     * @param managerEmail   admin's email performing the update
     * @return updated leave details as {@link LeaveResponseDTO}
     */
    LeaveResponseDTO updateLeaveStatus(UUID leaveId, EnumConstants.LeaveStatus status, String managerComment, String managerEmail);

    /**
     * Calculate the number of working days between the given from and to dates,
     * excluding weekends and company holidays.
     *
     * @param request the date range request containing fromDate and toDate
     * @return WorkdayResponseDTO containing the total working days and any relevant details
     */
    WorkdayResponseDTO calculateWorkingDays(DateRangeRequestDTO request);

    /**
     * Get all company holidays as a map where the key is the date (yyyy-MM-dd)
     * and the value is the holiday name.
     *
     * @return a map of holiday dates to holiday names
     */
    Map<String, String> getAllHolidaysMap();
}
