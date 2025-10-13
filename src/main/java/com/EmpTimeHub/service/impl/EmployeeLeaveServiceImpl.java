package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.LeaveRequestDTO;
import com.EmpTimeHub.dto.LeaveResponseDTO;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.exceptions.customExceptions.UserNotFoundException;
import com.EmpTimeHub.repository.*;
import com.EmpTimeHub.service.EmployeeLeaveService;
import com.EmpTimeHub.service.MailService;
import com.EmpTimeHub.specification.LeaveSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

    private final EmployeeLeaveRepository leaveRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;
    private final MailService mailService;
    private final HolidayRepository holidayRepository;

    /**
     * Applies for leave on behalf of the authenticated employee.
     * <p>
     * Calculates total holidays and working days between the leave dates,
     * saves the leave with PENDING status, and optionally sends an email
     * to the approver if specified.
     *
     * @param request DTO containing leave details such as type, fromDate, toDate, subject, context, and approvalName.
     * @param email   Email of the authenticated user applying for leave.
     * @return LeaveResponseDTO containing the saved leave details.
     * @throws EntityNotFoundException if the user, employee, or approver is not found.
     */
    @Override
    public LeaveResponseDTO applyLeave(LeaveRequestDTO request, String email) {
        log.info("Applying leave for user with email '{}', from {} to {}, type: {}",
                email, request.getFromDate(), request.getToDate(), request.getType());

        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        String employeeMail = employee.getCompanyEmail();
        log.debug("Employee found: {} {}", employee.getFirstName(), employee.getLastName());

        Admin approver = null;
        if (request.getApprovalName() != null && !request.getApprovalName().isBlank()) {
            approver = adminRepository.findByFullName(request.getApprovalName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Approver not found with name: " + request.getApprovalName()));
            log.debug("Approver found: {}", approver.getFullName());
        }

        LocalDate fromDate = request.getFromDate();
        LocalDate endDate = request.getToDate();

        // Count holidays
        List<Holiday> festHolidays = holidayRepository.findByHolidayDateBetween(fromDate, endDate);
        int holidays = festHolidays.size();
        log.debug("Number of festival holidays between {} and {}: {}", fromDate, endDate, holidays);

        // Count weekends
        int weekendCount = 0;
        LocalDate date = fromDate;
        while (!date.isAfter(endDate)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekendCount++;
            }
            date = date.plusDays(1);
        }
        log.debug("Number of weekend days between {} and {}: {}", fromDate, endDate, weekendCount);

        int totalHolidays = holidays + weekendCount;
        int totalDays = (int) ChronoUnit.DAYS.between(fromDate, endDate) + 1;
        int workingDays = totalDays - totalHolidays;

        // Build and save leave entity
        EmployeeLeave leave = EmployeeLeave.builder()
                .employee(employee)
                .type(request.getType())
                .fromDate(fromDate)
                .toDate(endDate)
                .subject(request.getSubject())
                .context(request.getContext())
                .status(EnumConstants.LeaveStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .holidays(totalHolidays)
                .workingDays(workingDays)
                .build();

        EmployeeLeave savedLeave = leaveRepository.save(leave);
        log.info("Leave saved successfully with leaveId={}", savedLeave.getLeaveId());

        // Send email to approver if available
        if (approver != null && approver.getUser() != null && approver.getUser().getCompanyEmail() != null) {
            String approverEmail = approver.getUser().getCompanyEmail();
            String mailSubject = request.getSubject();
            String mailBody = request.getContext() + "\n\nBest regards,\n" + employee.getFirstName();

            mailService.sendMail(employeeMail, approverEmail, mailSubject, mailBody, employee.getFirstName());
            log.info("Leave approval email sent from '{}' to '{}'", employeeMail, approverEmail);
        }

        return mapToResponse(savedLeave);
    }


    /**
     * Retrieves a paginated list of leaves with optional filters such as employeeId, month, type, and status.
     * <p>
     * Employees can only view their own leaves, while admins can view all leaves and apply filters.
     *
     * @param employeeId Optional UUID to filter by a specific employee (ADMIN only).
     * @param month      Optional month filter in format "yyyy-MM".
     * @param type       Optional leave type filter (e.g., PAID, UNPAID).
     * @param status     Optional leave status filter (e.g., APPROVED, PENDING).
     * @param page       Page number for pagination (default 0).
     * @param size       Page size for pagination (default 10).
     * @param sort       Sorting criteria in the format "field,direction" (default "createdAt,desc").
     * @param user       Authenticated user details injected by Spring Security.
     * @return Page of LeaveResponseDTO containing leave details.
     * @throws UserNotFoundException if the user or employee is not found.
     * @throws AccessDeniedException if an employee tries to access another employee's leaves.
     */
    @Override
    public Page<LeaveResponseDTO> getLeaves(UUID employeeId, String month, String type, String status,
                                            int page, int size, String sort, UserDetails user) {

        log.info("Fetching leaves for user '{}' with filters: employeeId={}, month={}, type={}, status={}, page={}, size={}, sort={}",
                user.getUsername(), employeeId, month, type, status, page, size, sort);

        User currentUser = userRepository.findByCompanyEmail(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Specification<EmployeeLeave> spec = null;

        if (currentUser.getRole() == EnumConstants.Role.EMPLOYEE) {
            Employee employee = employeeRepository.findByUser_UserId(currentUser.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Employee not found"));

            if (employeeId != null && !employee.getEmployeeId().equals(employeeId)) {
                log.warn("Employee '{}' attempted to access leaves of employeeId={}", user.getUsername(), employeeId);
                throw new AccessDeniedException("You don't have access to view other employees' leaves");
            }

            spec = LeaveSpecifications.byEmployee(employee.getEmployeeId());
            log.debug("Employee '{}' leave filter applied", employee.getEmployeeId());
        } else if (currentUser.getRole() == EnumConstants.Role.ADMIN) {
            if (employeeId != null) {
                spec = (spec == null) ? LeaveSpecifications.byEmployee(employeeId)
                        : spec.and(LeaveSpecifications.byEmployee(employeeId));
                log.debug("Admin '{}' applied employeeId filter: {}", currentUser.getCompanyEmail(), employeeId);
            }
        }

        if (month != null && !month.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byMonth(month)
                    : spec.and(LeaveSpecifications.byMonth(month));
            log.debug("Month filter applied: {}", month);
        }

        if (type != null && !type.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byType(type)
                    : spec.and(LeaveSpecifications.byType(type));
            log.debug("Type filter applied: {}", type);
        }

        if (status != null && !status.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byStatus(status)
                    : spec.and(LeaveSpecifications.byStatus(status));
            log.debug("Status filter applied: {}", status);
        }

        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0];
            Sort.Direction direction = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc"))
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sortObj = Sort.by(direction, sortField);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<EmployeeLeave> leavePage = (spec != null)
                ? leaveRepository.findAll(spec, pageable)
                : leaveRepository.findAll(pageable);

        log.info("Fetched {} leave records for user '{}'", leavePage.getTotalElements(), user.getUsername());

        return leavePage.map(this::mapToResponse);
    }

    /**
     * Retrieves a single leave by its ID for the authenticated employee.
     * <p>
     * Employees can only fetch their own leaves. Throws an exception if the leave
     * does not belong to the authenticated employee.
     *
     * @param leaveId UUID of the leave to fetch.
     * @param email   Email of the authenticated employee.
     * @return LeaveResponseDTO containing the leave details.
     * @throws EntityNotFoundException if the user, employee, or leave is not found.
     * @throws AccessDeniedException  if the leave does not belong to the employee.
     */
    @Override
    public LeaveResponseDTO getLeaveById(UUID leaveId, String email) {
        log.info("Fetching leave with leaveId={} for user '{}'", leaveId, email);

        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        EmployeeLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found with ID: " + leaveId));

        if (!leave.getEmployee().getEmployeeId().equals(employee.getEmployeeId())) {
            log.warn("User '{}' attempted to access leaveId={} which does not belong to them", email, leaveId);
            throw new AccessDeniedException("You are not authorized to view this leave");
        }

        log.info("Leave fetched successfully for leaveId={} and user '{}'", leaveId, email);
        return mapToResponse(leave);
    }

    /**
     * Updates an existing leave request for the authenticated employee.
     * <p>
     * Only leaves with status PENDING can be updated. Updates working days and holidays
     * based on the new dates and optionally sends an email to the approver if specified.
     *
     * @param request DTO containing updated leave details including leaveId, type, dates, subject, context, and approvalName.
     * @param email   Email of the authenticated employee.
     * @return LeaveResponseDTO containing the updated leave details.
     * @throws EntityNotFoundException if the user, employee, leave, or approver is not found.
     * @throws AccessDeniedException   if the leave does not belong to the employee.
     * @throws IllegalStateException   if the leave is not in PENDING status.
     */
    @Override
    public LeaveResponseDTO updateLeave(LeaveRequestDTO request, String email) {
        log.info("Updating leave with leaveId={} for user '{}'", request.getLeaveId(), email);

        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        UUID leaveId = request.getLeaveId();
        EmployeeLeave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found with ID: " + leaveId));

        if (!existingLeave.getEmployee().getEmployeeId().equals(employee.getEmployeeId())) {
            log.warn("User '{}' attempted to update leaveId={} which does not belong to them", email, leaveId);
            throw new AccessDeniedException("You are not authorized to update this leave");
        }

        if (existingLeave.getStatus() != EnumConstants.LeaveStatus.PENDING) {
            log.warn("LeaveId={} cannot be updated because its status is {}", leaveId, existingLeave.getStatus());
            throw new IllegalStateException("Only pending leaves can be updated");
        }

        LocalDate fromDate = request.getFromDate();
        LocalDate toDate = request.getToDate();

        // Calculate holidays and weekends
        List<Holiday> holidays = holidayRepository.findByHolidayDateBetween(fromDate, toDate);
        int holidayCount = holidays.size();

        int weekendCount = 0;
        LocalDate tempDate = fromDate;
        while (!tempDate.isAfter(toDate)) {
            DayOfWeek day = tempDate.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekendCount++;
            }
            tempDate = tempDate.plusDays(1);
        }

        int totalHolidays = holidayCount + weekendCount;
        int totalDays = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        int workingDays = totalDays - totalHolidays;

        // Update leave details
        existingLeave.setType(request.getType());
        existingLeave.setSubject(request.getSubject());
        existingLeave.setContext(request.getContext());
        existingLeave.setFromDate(fromDate);
        existingLeave.setToDate(toDate);
        existingLeave.setUpdatedAt(LocalDateTime.now());
        existingLeave.setWorkingDays(workingDays);
        existingLeave.setHolidays(totalHolidays);

        // Send email to approver if specified
        if (request.getApprovalName() != null && !request.getApprovalName().isBlank()) {
            Admin approver = adminRepository.findByFullName(request.getApprovalName())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Approver not found with name: " + request.getApprovalName()));
            if (approver.getUser() != null && approver.getUser().getCompanyEmail() != null) {
                mailService.sendMail(
                        employee.getCompanyEmail(),
                        approver.getUser().getCompanyEmail(),
                        "Updated Leave Request: " + request.getSubject(),
                        "Employee " + employee.getFirstName() + " updated their leave request.\n\n" + request.getContext(),
                        employee.getFirstName()
                );
                log.info("Approval email sent to '{}'", approver.getUser().getCompanyEmail());
            }
        }

        EmployeeLeave updatedLeave = leaveRepository.save(existingLeave);
        log.info("Leave updated successfully for leaveId={}", leaveId);

        return mapToResponse(updatedLeave);
    }


    /**
     * Deletes a leave request for the authenticated employee by its ID.
     * <p>
     * Only leaves with PENDING status can be deleted. Employees can only delete their own leaves.
     *
     * @param leaveId UUID of the leave to delete.
     * @param email   Email of the authenticated employee.
     * @throws EntityNotFoundException if the user, employee, or leave is not found.
     * @throws AccessDeniedException   if the leave does not belong to the employee.
     * @throws IllegalStateException   if the leave is not in PENDING status.
     */
    @Override
    public void deleteLeave(UUID leaveId, String email) {
        log.info("Attempting to delete leave with leaveId={} for user '{}'", leaveId, email);

        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        EmployeeLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found with ID: " + leaveId));

        if (!leave.getEmployee().getEmployeeId().equals(employee.getEmployeeId())) {
            log.warn("User '{}' attempted to delete leaveId={} which does not belong to them", email, leaveId);
            throw new AccessDeniedException("You are not authorized to delete this leave");
        }

        if (leave.getStatus() != EnumConstants.LeaveStatus.PENDING) {
            log.warn("LeaveId={} cannot be deleted because its status is {}", leaveId, leave.getStatus());
            throw new IllegalStateException("Only pending leaves can be deleted");
        }

        leaveRepository.delete(leave);
        log.info("Leave deleted successfully for leaveId={} by user '{}'", leaveId, email);
    }


    /**
     * Maps an EmployeeLeave entity to a LeaveResponseDTO.
     *
     * @param leave The EmployeeLeave entity to map.
     * @return LeaveResponseDTO containing relevant leave details for the API response.
     */
    private LeaveResponseDTO mapToResponse(EmployeeLeave leave) {
        return LeaveResponseDTO.builder()
                .leaveId(leave.getLeaveId())
                .approverName(
                        leave.getApproval() != null ? leave.getApproval().getFullName() : null
                )
                .employeeName(
                        leave.getEmployee() != null
                                ? leave.getEmployee().getUser().getUserName()
                                : null
                )
                .fromDate(leave.getFromDate())
                .toDate(leave.getToDate())
                .type(leave.getType() != null ? leave.getType().name() : null)
                .subject(leave.getSubject())
                .context(leave.getContext())
                .workingdays(leave.getWorkingDays())
                .holidays(leave.getHolidays())
                .status(leave.getStatus() != null ? leave.getStatus().name() : null)
                .adminComment(leave.getAdminComment())
                .build();
    }

}
