package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.LeaveRequestDTO;
import com.EmpTimeHub.dto.LeaveResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

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
}
