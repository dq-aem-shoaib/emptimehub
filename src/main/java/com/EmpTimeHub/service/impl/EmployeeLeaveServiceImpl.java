package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.DateRangeRequestDTO;
import com.EmpTimeHub.dto.LeaveRequestDTO;
import com.EmpTimeHub.dto.LeaveResponseDTO;
import com.EmpTimeHub.dto.WorkdayResponseDTO;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
     * to the manager if specified.
     *
     * @param request DTO containing leave details such as type, fromDate, toDate, subject, context, and approvalName.
     * @param email   Email of the authenticated user applying for leave.
     * @return LeaveResponseDTO containing the saved leave details.
     * @throws EntityNotFoundException if the user, employee, or manager is not found.
     */
    @Override
    public LeaveResponseDTO applyLeave(LeaveRequestDTO request, String email) {
        log.info("Applying leave for user with email '{}', from {} to {}, type: {}",
                email, request.getFromDate(), request.getToDate(), request.getType());

        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Employee not found"));

        String employeeMail = employee.getCompanyEmail();
        log.debug("Employee found: {} {}", employee.getFirstName(), employee.getLastName());

        Employee manager = employeeRepository.findByIdWithManager(employee.getEmployeeId())
                .map(emp -> {
                    if (emp.getReportingManager() == null) {
                        throw new UserNotFoundException("Manager not found for employee: " + emp.getEmployeeId());
                    }
                    return emp.getReportingManager();
                })
                .orElseThrow(() -> new UserNotFoundException("Employee not found with ID: " + employee.getEmployeeId()));

        LocalDate fromDate = request.getFromDate();
        LocalDate endDate = request.getToDate();

        DateRangeRequestDTO rangeRequestDTO=new DateRangeRequestDTO(fromDate,endDate);
        WorkdayResponseDTO workdayResponseDTO = calculateWorkingDays(rangeRequestDTO);

        int workingDays = workdayResponseDTO.getWorkingDays();
        int totalHolidays=workdayResponseDTO.getTotalHolidays();

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

        // Send email to manager if available
        if (manager != null ) {
            String managerCompanyMail = manager.getCompanyEmail();
            String managerName = manager.getFirstName();
            String mailSubject = request.getSubject();
            String mailBody = String.format("""
        Dear %s,
        
        I would like to request leave for the following period:
        
        Leave Period: %s to %s
        Type: %s
        Working Days: %d
        Holidays: %d
        Reason/Context: %s

        Kindly review and approve/reject my leave request.

        Regards,
        %s
        """,
                    managerName,
                    request.getFromDate(),
                    request.getToDate(),
                    request.getType(),
                    workingDays,
                    totalHolidays,
                    request.getContext(),
                    employee.getFirstName()
            );

            mailService.sendMail(employeeMail, managerCompanyMail, mailSubject, mailBody);
            log.info("Leave approval email sent from '{}' to '{}'", employeeMail, managerCompanyMail);
        }

        return mapToResponse(savedLeave);
    }


    /**
     * Fetches a paginated list of employee leaves based on role-based access and dynamic filters.
     * <p>
     * Access rules:
     * <ul>
     *     <li>EMPLOYEE: Can view only their own leaves.</li>
     *     <li>MANAGER: Can view leaves of direct reports only.</li>
     *     <li>ADMIN: Can view all leaves, optional filtering by employeeId.</li>
     * </ul>
     * <p>
     * Additional filters (`month`, `type`, `status`) are applied on top of role-based filtering.
     * Sorting and pagination are also supported.
     *
     * @param employeeId Optional UUID of an employee to filter by.
     * @param month      Optional month filter in "yyyy-MM" format.
     * @param type       Optional leave type filter (PAID, UNPAID, etc.).
     * @param status     Optional leave status filter (PENDING, APPROVED, REJECTED, etc.).
     * @param page       Page number for pagination (0-based).
     * @param size       Page size for pagination.
     * @param sort       Sort criteria in "field,direction" format (default "fromDate,desc").
     * @param user       Currently authenticated user details.
     * @return Paginated list of LeaveResponseDTO wrapped in Page.
     * @throws UserNotFoundException   if the user or employee/manager record is not found.
     * @throws AccessDeniedException   if an employee/manager tries to access unauthorized leave records.
     */
    @Override
    public Page<LeaveResponseDTO> getLeaves(UUID employeeId, String month, String type, String status,
                                            int page, int size, String sort, UserDetails user) {

        log.info("===== Fetching leave summary =====");
        log.info("Requested by user: '{}', Filters -> employeeId: {}, month: {}, type: {}, status: {}, page: {}, size: {}, sort: {}",
                user.getUsername(), employeeId, month, type, status, page, size, sort);

        // Fetch current user from database
        User currentUser = userRepository.findByCompanyEmail(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + user.getUsername()));

        Specification<EmployeeLeave> spec = null; // Initialize Specification for dynamic query building

        // ===========================
        // Role-based access control
        // ===========================

        if (currentUser.getRole() == EnumConstants.Role.EMPLOYEE) {
            // Employee can view only their own leaves
            Employee employee = employeeRepository.findByUser_UserId(currentUser.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Employee record not found"));

            // Validate employeeId filter to prevent access to other employees' leaves
            if (employeeId != null && !employee.getEmployeeId().equals(employeeId)) {
                log.warn("Employee '{}' attempted to access leaves of employeeId={}", user.getUsername(), employeeId);
                throw new AccessDeniedException("You don't have access to view other employees' leaves");
            }

            spec = LeaveSpecifications.byEmployee(employee.getEmployeeId());
            log.debug("Applied employee filter for employeeId={}", employee.getEmployeeId());

        } else if (currentUser.getRole() == EnumConstants.Role.MANAGER) {
            // Manager can view leaves of employees reporting to them
            Employee manager = employeeRepository.findByUser_UserId(currentUser.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("Manager record not found"));

            if (employeeId != null) {
                // Check if the requested employee belongs to this manager
                Employee employee = employeeRepository.findByIdWithManager(employeeId)
                        .orElseThrow(() -> new UserNotFoundException("Employee not found"));

                Employee assignedManager = employee.getReportingManager();
                if (!manager.getEmployeeId().equals(assignedManager.getEmployeeId())) {
                    log.warn("Manager '{}' attempted to access leaves of non-report employeeId={}", user.getUsername(), employeeId);
                    throw new AccessDeniedException("You don't have access to view this employee's leaves");
                }
                spec = LeaveSpecifications.byEmployee(employeeId);
                log.debug("Applied employee filter for manager's direct report employeeId={}", employeeId);
            } else {
                // Fetch all direct reports
                spec = LeaveSpecifications.byManager(manager.getEmployeeId());
                log.debug("Applied manager filter for all direct reports of managerId={}", manager.getEmployeeId());
            }

        } else if (currentUser.getRole() == EnumConstants.Role.ADMIN) {
            // Admin can filter by specific employee or view all
            if (employeeId != null) {
                spec = LeaveSpecifications.byEmployee(employeeId);
                log.debug("Admin applied employee filter for employeeId={}", employeeId);
            } else {
                log.debug("Admin fetching leaves without employee filter");
            }
        }

        // ===========================
        // Apply common filters
        // ===========================

        if (month != null && !month.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byMonth(month)
                    : spec.and(LeaveSpecifications.byMonth(month));
            log.debug("Applied month filter: {}", month);
        }

        if (type != null && !type.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byType(type)
                    : spec.and(LeaveSpecifications.byType(type));
            log.debug("Applied leave type filter: {}", type);
        }

        if (status != null && !status.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byStatus(status)
                    : spec.and(LeaveSpecifications.byStatus(status));
            log.debug("Applied leave status filter: {}", status);
        }

        // ===========================
        // Sorting
        // ===========================
        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt"); // Default sort
        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0];
            Sort.Direction direction = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc"))
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sortObj = Sort.by(direction, sortField);
            log.debug("Applied sorting: field={}, direction={}", sortField, direction);
        }

        // ===========================
        // Pagination
        // ===========================
        Pageable pageable = PageRequest.of(page, size, sortObj);
        log.debug("Pagination applied: page={}, size={}", page, size);

        // ===========================
        // Execute query
        // ===========================
        Page<EmployeeLeave> leavePage = (spec != null)
                ? leaveRepository.findAll(spec, pageable)
                : leaveRepository.findAll(pageable);

        log.info("Fetched {} leave records for user '{}'", leavePage.getTotalElements(), user.getUsername());
        log.info("===== Leave summary fetch complete =====");

        // Map entities to DTO
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
     * based on the new dates and optionally sends an email to the manager if specified.
     *
     * @param request DTO containing updated leave details including leaveId, type, dates, subject, context, and approvalName.
     * @param email   Email of the authenticated employee.
     * @return LeaveResponseDTO containing the updated leave details.
     * @throws EntityNotFoundException if the user, employee, leave, or manager is not found.
     * @throws AccessDeniedException   if the leave does not belong to the employee.
     * @throws IllegalStateException   if the leave is not in PENDING status.
     */
    @Override
    public LeaveResponseDTO updateLeave(LeaveRequestDTO request, String email) {
        log.info("Updating leave with leaveId={} for user '{}'", request.getLeaveId(), email);

        // Validate user and employee
        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found for user: " + user.getUserId()));

        UUID leaveId = request.getLeaveId();
        EmployeeLeave existingLeave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found with ID: " + leaveId));

        // Authorization check
        if (!existingLeave.getEmployee().getEmployeeId().equals(employee.getEmployeeId())) {
            log.warn("Unauthorized attempt: '{}' tried to update leaveId={} not belonging to them", email, leaveId);
            throw new AccessDeniedException("You are not authorized to update this leave");
        }

        // Only pending leaves can be updated
        if (existingLeave.getStatus() != EnumConstants.LeaveStatus.PENDING) {
            log.warn("LeaveId={} update rejected — current status={}", leaveId, existingLeave.getStatus());
            throw new IllegalStateException("Only pending leaves can be updated");
        }

        // Apply non-null updates
        if (request.getType() != null) {
            existingLeave.setType(request.getType());
        }
        if (request.getSubject() != null && !request.getSubject().isBlank()) {
            existingLeave.setSubject(request.getSubject());
        }
        if (request.getContext() != null && !request.getContext().isBlank()) {
            existingLeave.setContext(request.getContext());
        }

        // Recalculate date-related fields if dates changed
        LocalDate fromDate = request.getFromDate() != null ? request.getFromDate() : existingLeave.getFromDate();
        LocalDate toDate = request.getToDate() != null ? request.getToDate() : existingLeave.getToDate();

        if (!fromDate.equals(existingLeave.getFromDate()) || !toDate.equals(existingLeave.getToDate())) {
            DateRangeRequestDTO rangeRequestDTO = new DateRangeRequestDTO(fromDate, toDate);
            WorkdayResponseDTO workdayResponseDTO = calculateWorkingDays(rangeRequestDTO);

            int workingDays = workdayResponseDTO.getWorkingDays();
            int totalHolidays = workdayResponseDTO.getTotalHolidays();
            existingLeave.setFromDate(fromDate);
            existingLeave.setToDate(toDate);
            existingLeave.setWorkingDays(workingDays);
            existingLeave.setHolidays(totalHolidays);

            log.info("Recalculated leave period for leaveId={}: workingDays={}, holidays={}", leaveId, workingDays, totalHolidays);
        }

        // Send email to manager if available
        Employee manager = employeeRepository.findByIdWithManager(employee.getEmployeeId())
                .map(emp -> {
                    if (emp.getReportingManager() == null) {
                        throw new UserNotFoundException("Manager not found for employee: " + emp.getEmployeeId());
                    }
                    return emp.getReportingManager();
                })
                .orElseThrow(() -> new UserNotFoundException("Employee not found with ID: " + employee.getEmployeeId()));


        if (manager != null) {
            String managerCompanyMail = manager.getCompanyEmail();
            String managerName = manager.getFirstName();
            String employeeMail = employee.getCompanyEmail();
            String mailSubject = "Updated Leave Request: " + existingLeave.getSubject();
            String mailBody = String.format("""
            Dear %s,
            
            The following leave request has been updated by %s:
            
            Leave Period: %s to %s
            Type: %s
            Working Days: %d
            Holidays: %d
            Reason/Context: %s
            
            Kindly review and approve/reject the leave.
            
            Regards,
            %s
            """,
                    managerName,
                    employee.getFirstName(),
                    existingLeave.getFromDate(),
                    existingLeave.getToDate(),
                    existingLeave.getType(),
                    existingLeave.getWorkingDays(),
                    existingLeave.getHolidays(),
                    existingLeave.getContext(),
                    employee.getFirstName()
            );

            mailService.sendMail(employeeMail, managerCompanyMail, mailSubject, mailBody);
            log.info("Leave update email sent from '{}' to '{}'", employeeMail, managerCompanyMail);
        }

        // Update timestamp and save
        existingLeave.setUpdatedAt(LocalDateTime.now());
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
     * Updates the status of a leave request (APPROVED or REJECTED) by an admin
     * and sends an email notification to the employee.
     *
     * @param leaveId      UUID of the leave to update
     * @param status       new leave status (APPROVED or REJECTED)
     * @param managerComment optional comment from the admin
     * @param managerEmail   email of the admin performing the update
     * @return updated leave details as {@link LeaveResponseDTO}
     * @throws EntityNotFoundException if the admin user, admin entity, or leave entity is not found
     */
    @Override
    public LeaveResponseDTO updateLeaveStatus(UUID leaveId, EnumConstants.LeaveStatus status, String managerComment, String managerEmail) {
        log.info("Manager '{}' updating leaveId={} with status={}", managerEmail, leaveId, status);

        // Fetch logged-in manager
        Employee manager = employeeRepository.getEmployeeByEmail(managerEmail);
        if (manager == null) {
            throw new EntityNotFoundException("Manager not found with email: " + managerEmail);
        }

        // Fetch leave with employee and reporting manager
        EmployeeLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found with id: " + leaveId));


        // Fetch employee along with reporting manager in one query
        Employee employee = employeeRepository.findByIdWithManager(leave.getEmployee().getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        int availableLeaves = employee.getAvailableLeaves() != null ? employee.getAvailableLeaves() : 0;
        int workingDays = leave.getWorkingDays() != null ? leave.getWorkingDays() : 0;

        // Validate working days
        if (workingDays <= 0) {
            throw new IllegalArgumentException("Working days must be greater than zero");
        }

        // Deduct only if leave type is not UNPAID
        if (leave.getType() != null && leave.getType() != EnumConstants.LeaveType.UNPAID) {
            if (workingDays > availableLeaves) {
                throw new IllegalArgumentException("Insufficient available leaves");
            }

            employee.setAvailableLeaves(availableLeaves - workingDays);

            // Persist updated employee safely
            employeeRepository.save(employee);
        }


        // Validate that the logged-in manager is the reporting manager
        if (employee.getReportingManager() == null || !employee.getReportingManager().getEmployeeId().equals(manager.getEmployeeId())) {
            log.warn("Unauthorized attempt: '{}' tried to update leaveId={} not belonging to their team", managerEmail, leaveId);
            throw new AccessDeniedException("You are not authorized to update this leave");
        }

        // Update leave details
        leave.setStatus(status != null ? status : leave.getStatus());
        leave.setManagerComment(managerComment != null ? managerComment : leave.getManagerComment());
        leave.setReportingManager(manager); // store approving manager
        leave.setUpdatedAt(LocalDateTime.now());

        EmployeeLeave updatedLeave = leaveRepository.save(leave);
        log.info("Leave status updated successfully: {}", updatedLeave.getStatus());

        // Send notification
        sendLeaveStatusNotification(updatedLeave,employee);

        return mapToResponse(updatedLeave);
    }


    /**
     * Sends an email notification to the employee regarding the leave status update.
     * Includes leave period, type, working days, holidays, and admin comment.
     *
     * @param leave the {@link EmployeeLeave} entity containing leave and employee details
     */
    private void sendLeaveStatusNotification(EmployeeLeave leave,Employee employee) {
        if (leave == null || leave.getEmployee() == null) {
            log.warn("Cannot send email: leave or employee is null for leaveId={}", leave != null ? leave.getLeaveId() : "null");
            return;
        }

        try {

            Employee manager = employee.getReportingManager(); // reporting manager
            String managerName = (manager != null )
                    ? manager.getFirstName()
                    : "Manager";
            String managerEmail = (manager != null)
                    ? manager.getCompanyEmail()
                    : null;

            String employeeEmail = employee.getCompanyEmail();
            String employeeName = employee.getFirstName();

            String subject = "Your Leave Request ("
                    + (leave.getType() != null ? leave.getType() : "") + ") has been "
                    + (leave.getStatus() != null ? leave.getStatus() : "UPDATED");

            String status = leave.getStatus() != null ? leave.getStatus().name() : "UPDATED";
            String managerComment = leave.getManagerComment() != null ? leave.getManagerComment() : "No comments";

            String body;

            if ("APPROVED".equals(status)) {
                body = String.format("""
            Dear %s,

            Your leave request has been %s.

            Leave Period: %s to %s
            Type: %s
            Working Days: %d
            Holidays: %d
            Manager Comment: %s

            Regards,
            %s
            """,
                        employeeName,
                        status,
                        leave.getFromDate(),
                        leave.getToDate(),
                        leave.getType() != null ? leave.getType() : "N/A",
                        leave.getWorkingDays() != null ? leave.getWorkingDays() : 0,
                        leave.getHolidays() != null ? leave.getHolidays() : 0,
                        managerComment,
                        managerName
                );
            } else  {
                body = String.format("""
            Dear %s,

            Your leave request has been %s.

            Manager Comment: %s

            Regards,
            %s
            """,
                        employeeName,
                        status,
                        managerComment,
                        managerName
                );
            }

            // Send email from manager if email exists, else fallback to employee email
            String senderEmail = managerEmail != null ? managerEmail : employeeEmail;
            mailService.sendMail(senderEmail, employeeEmail, subject, body);

            log.info("Leave status notification email sent to employee '{}'", employeeEmail);

        } catch (Exception e) {
            log.error("Failed to send leave status email for leaveId={}: {}", leave.getLeaveId(), e.getMessage(), e);
        }
    }



    /**
     * Calculates the number of working days between two dates.
     * <p>
     * Working days are calculated as total days minus weekends (Saturday & Sunday)
     * and company holidays fetched from the repository.
     *
     * @param request the date range request containing fromDate and toDate
     * @return WorkdayResponseDTO containing total days, weekends, holidays, and working days
     * @throws IllegalArgumentException if fromDate or toDate is null or fromDate is after toDate
     */
    @Override
    public  WorkdayResponseDTO calculateWorkingDays(DateRangeRequestDTO request) {
        LocalDate fromDate = request.getFromDate();
        LocalDate endDate = request.getToDate();

        if (fromDate == null || endDate == null || fromDate.isAfter(endDate)) {
            log.error("Invalid date range provided: fromDate={}, toDate={}", fromDate, endDate);
            throw new IllegalArgumentException("Invalid date range provided.");
        }

        // Fetch holidays in the given date range
        List<Holiday> holidaysList = holidayRepository.findByHolidayDateBetween(fromDate, endDate);
        int holidays = holidaysList.size();
        log.debug("Number of holidays between {} and {}: {}", fromDate, endDate, holidays);

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

        // Calculate totals
        int totalDays = (int) ChronoUnit.DAYS.between(fromDate, endDate) + 1;
        int totalHolidays = holidays + weekendCount;
        int workingDays = totalDays - totalHolidays;

        log.info("Total days: {}, Holidays: {}, Weekends: {}, Working days: {}",
                totalDays, holidays, weekendCount, workingDays);

        return WorkdayResponseDTO.builder()
                .totalDays(totalDays)
                .holidays(holidays)
                .weekends(weekendCount)
                .totalHolidays(totalHolidays)
                .workingDays(workingDays)
                .build();
    }


    /**
     * Fetches all company holidays and returns them as a sorted map.
     * <p>
     * The map key is the holiday date in "yyyy-MM-dd" format,
     * and the value is the holiday name.
     * The holidays are sorted by date in ascending order.
     *
     * @return a LinkedHashMap of holiday dates to holiday names
     */
    @Override
    public Map<String, String> getAllHolidaysMap() {
        log.info("Fetching all company holidays from the database");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, String> holidaysMap = holidayRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Holiday::getHolidayDate))
                .collect(Collectors.toMap(
                        h -> h.getHolidayDate().format(formatter),
                        Holiday::getName,
                        (existing, replacement) -> existing, // handle duplicates
                        LinkedHashMap::new // preserve insertion order
                ));

        log.info("Fetched {} holidays", holidaysMap.size());

        return holidaysMap;
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
                        leave.getReportingManager() != null ? leave.getReportingManager().getFirstName() : null
                )
                .employeeName(
                        leave.getEmployee() != null
                                ? leave.getEmployee().getFirstName()
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
                .managerComment(leave.getManagerComment())
                .build();
    }

}
