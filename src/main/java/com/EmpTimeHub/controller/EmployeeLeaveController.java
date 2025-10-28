package com.EmpTimeHub.controller;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.*;
import com.EmpTimeHub.service.EmployeeLeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.EmpTimeHub.constants.EndpointConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmployeeLeaveController {

    private final EmployeeLeaveService leaveService;

    /**
     * Allows an employee to apply for leave.
     * <p>
     * Accessible only by users with role 'EMPLOYEE'.
     *
     * @param request      DTO containing leave details such as type, fromDate, toDate, and reason.
     * @param userDetails  Authenticated user's details injected by Spring Security.
     * @return ResponseEntity containing WebResponseDTO with leave details, success flag, and message.
     */

    @Operation(
            summary = "Apply for Leave",
            description = "Submit a new leave request with optional attachment (e.g., medical certificate or document).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = LeaveRequestDTO.class)
                    )
            )
    )
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping(EMPLOYEE_LEAVE_REQUEST)
    public ResponseEntity<WebResponseDTO<LeaveResponseDTO>> applyLeave(
            @ModelAttribute LeaveRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Employee '{}' is applying for leave from {} to {} of type {}",
                userDetails.getUsername(),
                request.getFromDate(),
                request.getToDate(),
                request.getCategoryType());

        LeaveResponseDTO leaveResponseDTO = leaveService.applyLeave(request, userDetails.getUsername());

        log.info("Leave applied successfully for employee '{}', leaveId: {}",
                userDetails.getUsername(),
                leaveResponseDTO.getLeaveId());

        WebResponseDTO<LeaveResponseDTO> response = WebResponseDTO.<LeaveResponseDTO>builder()
                .flag(true)
                .message("Apply Leave Successfully")
                .status(200)
                .response(leaveResponseDTO)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches leave summaries with optional filters.
     * <p>
     * Accessible by users with roles 'ADMIN' or 'EMPLOYEE'.
     *
     * @param employeeId Optional UUID of the employee to filter leaves (ADMIN only).
     * @param month      Optional month (format: yyyy-MM) to filter leaves.
     * @param financialType       Optional leave type filter (e.g., PAID, UNPAID).
     * @param leaveCategory       Optional leave type filter (e.g., SICK, CASUAL).
     * @param status     Optional leave status filter (e.g., APPROVED, PENDING).
     * @param page       Page number for pagination (default 0).
     * @param size       Page size for pagination (default 10).
     * @param sort       Sorting criteria in the format "field,direction" (default "fromDate,desc").
     * @param user       Authenticated user details injected by Spring Security.
     * @return ResponseEntity containing WebResponseDTO with paginated leave summaries.
     */
    @GetMapping(LEAVE_SUMMARY)
    @PreAuthorize("hasRole('ADMIN') OR hasRole('EMPLOYEE') OR hasRole('MANAGER')")
    public ResponseEntity<WebResponseDTO<Page<LeaveResponseDTO>>> getLeaves(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) EnumConstants.FinancialType financialType,
            @RequestParam(required = false) EnumConstants.LeaveCategory leaveCategory,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean futureApproved,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fromDate,desc") String sort,
            @AuthenticationPrincipal UserDetails user) {

        log.info("User '{}' requested leave summary with filters: employeeId={}, month={}, financialType={},leaveCategoryType={}, status={}, page={}, size={}, sort={}",
                user.getUsername(), employeeId, month, financialType,leaveCategory, status, page, size, sort);

        String financialTypeStr = (financialType != null) ? financialType.name() : null;
        String leaveCategoryStr = (leaveCategory != null) ? leaveCategory.name() : null;

        Page<LeaveResponseDTO> leaves = leaveService.getLeaves(employeeId, month, financialTypeStr,leaveCategoryStr, status, page, size, sort, user,futureApproved,date);

        log.info("Fetched {} leave records for user '{}'", leaves.getTotalElements(), user.getUsername());

        WebResponseDTO<Page<LeaveResponseDTO>> response = WebResponseDTO.<Page<LeaveResponseDTO>>builder()
                .flag(true)
                .message("Leaves fetched successfully")
                .status(200)
                .response(leaves)
                .build();

        return ResponseEntity.ok(response);
    }


    /**
     * Fetches a single leave by its ID for the authenticated employee.
     * <p>
     * Accessible only by users with role 'EMPLOYEE'.
     *
     * @param leaveId     UUID of the leave to fetch.
     * @param userDetails Authenticated user details injected by Spring Security.
     * @return ResponseEntity containing WebResponseDTO with the leave details, success flag, and message.
     */
    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('MANAGER') OR hasRole('ADMIN')")
    @GetMapping(EMPLOYEE_LEAVE_BY_ID)
    public ResponseEntity<WebResponseDTO<LeaveResponseDTO>> getLeaveById(
            @PathVariable UUID leaveId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Employee '{}' requested leave details for leaveId={}", userDetails.getUsername(), leaveId);

        LeaveResponseDTO leaveResponse = leaveService.getLeaveById(leaveId, userDetails.getUsername());

        log.info("Leave fetched successfully for leaveId={}, employee={}", leaveId, userDetails.getUsername());

        WebResponseDTO<LeaveResponseDTO> response = WebResponseDTO.<LeaveResponseDTO>builder()
                .flag(true)
                .message("Leave fetched successfully")
                .status(200)
                .response(leaveResponse)
                .build();

        return ResponseEntity.ok(response);
    }


    /**
     * Updates an existing leave request for the authenticated employee.
     * <p>
     * Accessible only by users with role 'EMPLOYEE'.
     *
     * @param request     DTO containing updated leave details.
     * @param userDetails Authenticated user details injected by Spring Security.
     * @return ResponseEntity containing WebResponseDTO with the updated leave details, success flag, and message.
     */
    @Operation(
            summary = "Update  Leave",
            description = "Update a new leave request with optional attachment (e.g., medical certificate or document).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = LeaveRequestDTO.class)
                    )
            )
    )
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping(EMPLOYEE_LEAVE_UPDATE)
    public ResponseEntity<WebResponseDTO<LeaveResponseDTO>> updateLeave(
            @ModelAttribute LeaveRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Employee '{}' is updating leave with leaveId={}", userDetails.getUsername(), request.getLeaveId());

        LeaveResponseDTO updatedLeave = leaveService.updateLeave(request, userDetails.getUsername());

        log.info("Leave updated successfully for leaveId={}, employee={}", request.getLeaveId(), userDetails.getUsername());

        WebResponseDTO<LeaveResponseDTO> response = WebResponseDTO.<LeaveResponseDTO>builder()
                .flag(true)
                .message("Leave updated successfully")
                .status(200)
                .response(updatedLeave)
                .build();

        return ResponseEntity.ok(response);
    }


    /**
     * Withdraws a leave request for the authenticated employee by its ID.
     * <p>
     * Accessible only by users with role 'EMPLOYEE'.
     * Employees can withdraw only pending or today/future-dated leaves.
     *
     * @param id          UUID of the leave to withdraw.
     * @param userDetails Authenticated user details injected by Spring Security.
     * @return ResponseEntity containing WebResponseDTO with a success message.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping(EMPLOYEE_LEAVE_WITHDRAW)
    public ResponseEntity<WebResponseDTO<String>> withdrawLeave(
            @RequestParam UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Employee '{}' requested withdrawal of leave with leaveId={}", userDetails.getUsername(), id);

        leaveService.withdrawLeave(id, userDetails.getUsername());

        log.info("Leave withdrawn successfully for leaveId={}, employee={}", id, userDetails.getUsername());

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Leave withdrawn successfully")
                .status(200)
                .response("Leave ID " + id + " withdrawn successfully")
                .build();

        return ResponseEntity.ok(response);
    }


    /**
     * Endpoint to update the status of an employee leave request (APPROVED or REJECTED) by an admin.
     * <p>
     * Only users with the role ADMIN can access this endpoint.
     * The admin can optionally provide a comment when updating the leave status.
     *
     * @param leaveId     UUID of the leave to update (from the URL path)
     * @param status      new leave status (APPROVED or REJECTED)
     * @param comment     optional comment provided by the admin
     * @param userDetails details of the authenticated admin performing the update
     * @return {@link WebResponseDTO} containing the updated leave details in a {@link LeaveResponseDTO}
     */
    @PutMapping(EMPLOYEE_LEAVE_STATUS_UPDATE)
    @PreAuthorize("hasAnyRole('MANAGER') OR hasAnyRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<LeaveResponseDTO>> updateLeaveStatus(
            @PathVariable UUID leaveId,
            @RequestParam EnumConstants.LeaveStatus status,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal UserDetails userDetails) {

        LeaveResponseDTO response = leaveService.updateLeaveStatus(leaveId, status, comment, userDetails.getUsername());
        return ResponseEntity.ok(WebResponseDTO.<LeaveResponseDTO>builder()
                .flag(true)
                .status(200)
                .message("Leave " + status.name().toLowerCase() + " successfully")
                .response(response)
                .build());
    }

    /**
     * Calculate the number of working days for a given date range.
     *
     * @param request the date range request containing fromDate and toDate
     * @return ResponseEntity containing a WebResponseDTO with the calculated working days
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping(EMPLOYEE_WORKDAYS)
    public ResponseEntity<WebResponseDTO<WorkdayResponseDTO>> calculateWorkingDays(
            @RequestBody DateRangeRequestDTO request) {

        log.info("Received request to calculate working days from {} to {}",
                request.getFromDate(), request.getToDate());

        WorkdayResponseDTO response = leaveService.calculateWorkingDays(request);

        log.info("Calculated working days: {}", response.getLeaveDuration());

        return ResponseEntity.ok(WebResponseDTO.<WorkdayResponseDTO>builder()
                .flag(true)
                .status(200)
                .message("Working days calculated successfully")
                .response(response)
                .build());
    }


    /**
     * Checks CASUAL leave availability for an employee.
     * Automatically marks leave as UNPAID if balance is insufficient.
     * Frontend can adjust leave duration only, not type.
     * Accessible to users with EMPLOYEE role.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping(EMPLOYEE_LEAVE_AVAILABILTY)
    public ResponseEntity<WebResponseDTO<LeaveAvailabilityDTO>> checkLeaveAvailability(
            @RequestParam UUID employeeId,
            @RequestParam Double leaveDuration) {

        WebResponseDTO<LeaveAvailabilityDTO> response =  leaveService.checkLeaveAvailability(employeeId, leaveDuration);
        return ResponseEntity.status(response.getStatus() != null ? response.getStatus() : 200)
                .body(response);
    }

    /**
     * Get all pending leaves for the logged-in manager.
     *
     * @param loggedInUser Currently authenticated user (must be a manager)
     * @return WebResponseDTO containing list of pending leaves for manager's employees
     */
    @GetMapping(EMPLOYEE_PENDING_LEAVES)
    @PreAuthorize("hasRole('MANAGER') OR hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<List<PendingLeavesResponseDTO>>> getPendingLeaves(
            @AuthenticationPrincipal UserDetails loggedInUser
    ) {
        log.info("Fetching pending leaves for manager email={}", loggedInUser.getUsername());

        List<PendingLeavesResponseDTO> pendingLeaves = leaveService.getPendingLeavesForManagerAndAdmin(loggedInUser.getUsername());

        log.info("Found {} pending leave(s) for manager email={}", pendingLeaves.size(), loggedInUser.getUsername());

        WebResponseDTO<List<PendingLeavesResponseDTO>> response = WebResponseDTO.<List<PendingLeavesResponseDTO>>builder()
                .flag(true)
                .status(200)
                .message("Pending leaves fetched successfully")
                .response(pendingLeaves)
                .build();

        log.info("Returning pending leaves response for manager email={}", loggedInUser.getUsername());
        return ResponseEntity.ok(response);
    }


    /**
     * Fetches approved leaves for the current year based on user role (Employee, Manager, Admin).
     * Managers/Admins can provide employeeId to view other employees’ leaves.
     * Excludes weekends and holidays from the result.
     * @return list of approved leave days wrapped in WebResponseDTO.
     */
    @GetMapping(EMPLOYEE_APPROVED_LEAVES)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<List<EmployeeLeaveDayDTO>>> getApprovedLeavesForCurrentYear(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) LocalDate currentYear) {

        String companyMail = userDetails.getUsername();
        log.info("Request to fetch approved leaves for year: {}, by user: {}", currentYear, companyMail);

        List<EmployeeLeaveDayDTO> leaveDays = leaveService.getApprovedLeavesForCurrentYear(
                companyMail, employeeId, currentYear
        );

        WebResponseDTO<List<EmployeeLeaveDayDTO>> response = WebResponseDTO.<List<EmployeeLeaveDayDTO>>builder()
                .flag(true)
                .status(200)
                .message("Approved leaves fetched successfully")
                .response(leaveDays)
                .build();

        return ResponseEntity.ok(response);
    }

}
