package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.LeaveRequestDTO;
import com.EmpTimeHub.dto.LeaveResponseDTO;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.service.EmployeeLeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping(EMPLOYEE_LEAVE_REQUEST)
    public ResponseEntity<WebResponseDTO<LeaveResponseDTO>> applyLeave(
            @RequestBody LeaveRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Employee '{}' is applying for leave from {} to {} of type {}",
                userDetails.getUsername(),
                request.getFromDate(),
                request.getToDate(),
                request.getType());

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
     * @param type       Optional leave type filter (e.g., PAID, UNPAID).
     * @param status     Optional leave status filter (e.g., APPROVED, PENDING).
     * @param page       Page number for pagination (default 0).
     * @param size       Page size for pagination (default 10).
     * @param sort       Sorting criteria in the format "field,direction" (default "fromDate,desc").
     * @param user       Authenticated user details injected by Spring Security.
     * @return ResponseEntity containing WebResponseDTO with paginated leave summaries.
     */
    @GetMapping(LEAVE_SUMMARY)
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<Page<LeaveResponseDTO>>> getLeaves(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fromDate,desc") String sort,
            @AuthenticationPrincipal UserDetails user) {

        log.info("User '{}' requested leave summary with filters: employeeId={}, month={}, type={}, status={}, page={}, size={}, sort={}",
                user.getUsername(), employeeId, month, type, status, page, size, sort);

        Page<LeaveResponseDTO> leaves = leaveService.getLeaves(employeeId, month, type, status, page, size, sort, user);

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
    @PreAuthorize("hasRole('EMPLOYEE')")
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
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping(EMPLOYEE_LEAVE_UPDATE)
    public ResponseEntity<WebResponseDTO<LeaveResponseDTO>> updateLeave(
            @RequestBody LeaveRequestDTO request,
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
     * Deletes a leave request for the authenticated employee by its ID.
     * <p>
     * Accessible only by users with role 'EMPLOYEE'.
     *
     * @param id          UUID of the leave to delete.
     * @param userDetails Authenticated user details injected by Spring Security.
     * @return ResponseEntity containing WebResponseDTO with a success message.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping(EMPLOYEE_LEAVE_DELETE)
    public ResponseEntity<WebResponseDTO<String>> deleteLeave(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Employee '{}' requested deletion of leave with leaveId={}", userDetails.getUsername(), id);

        leaveService.deleteLeave(id, userDetails.getUsername());

        log.info("Leave deleted successfully for leaveId={}, employee={}", id, userDetails.getUsername());

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Leave deleted successfully")
                .status(200)
                .response("Leave ID " + id + " deleted")
                .build();

        return ResponseEntity.ok(response);
    }

}
