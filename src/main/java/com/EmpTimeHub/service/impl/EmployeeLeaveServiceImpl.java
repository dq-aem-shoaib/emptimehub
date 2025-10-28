package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.config.MailTemplateConfig;
import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.*;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.exceptions.customExceptions.UserNotFoundException;
import com.EmpTimeHub.repository.*;
import com.EmpTimeHub.service.EmployeeLeaveService;
import com.EmpTimeHub.service.MailService;
import com.EmpTimeHub.service.NotificationService;
import com.EmpTimeHub.specification.LeaveSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final HolidayCalendarRepository holidayRepository;
    private final MailTemplateConfig mailTemplateConfig;

    @Autowired
    private NotificationService notificationService;

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
                email, request.getFromDate(), request.getToDate(), request.getCategoryType());

        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Employee not found"));

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

        // Calculate working days
        DateRangeRequestDTO rangeRequestDTO = new DateRangeRequestDTO(fromDate, endDate,request.getPartialDay());
        WorkdayResponseDTO workdayResponseDTO = calculateWorkingDays(rangeRequestDTO);
        int totalHolidays = workdayResponseDTO.getTotalHolidays();

        // Determine leave duration
        Double leaveDuration = request.getLeaveDuration();
        Boolean partialDay = Boolean.TRUE.equals(request.getPartialDay());
        String subject = request.getCategoryType().name() + " Leave Request";
        // 5. Optional attachment URL
        String attachmentUrl = (request.getAttachmentFile() != null && !request.getAttachmentFile().isEmpty())
                ? null
                : null;

        // Save leave request
        EmployeeLeave leave = EmployeeLeave.builder()
                .employee(employee)
                .reportingManager(manager)
                .leaveCategory(request.getCategoryType())
                .financialType(request.getFinancialType())
                .fromDate(fromDate)
                .toDate(endDate)
                .subject(subject)
                .context(request.getContext())
                .status(EnumConstants.LeaveStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .attachmentUrl(attachmentUrl)
                .partialDay(partialDay)
                .holidays(totalHolidays)
                .leaveDuration(leaveDuration)
                .withdrawn(false)
                .policyViolation(false)
                .noticePeriodViolation(false)
                .build();

        EmployeeLeave savedLeave = leaveRepository.save(leave);
        log.info("Leave saved successfully with leaveId={}", savedLeave.getLeaveId());

        notificationService.sendNotification(
                manager.getUser(),
                employee.getFirstName()+ " "+employee.getLastName() + " applied leave from " + leave.getFromDate() + " to " + leave.getToDate() +
                        " (" + leave.getLeaveDuration() + " days). Category: " + leave.getLeaveCategory(),
                leave.getLeaveId()
        );

        // Send email to manager
        if (manager != null) {
            String managerCompanyMail = manager.getCompanyEmail();
            String managerName = manager.getFirstName()+ " "+manager.getLastName();
            String employeeName = employee.getFirstName() + " " + employee.getLastName();
            String employeeId = employee.getCompanyId() != null ? employee.getCompanyId().toString() : "N/A";
            String mailSubject = "Leave Request Submission – " + employeeName;
            String managerLink = "https://portal.company.com/leave";

            String template = mailTemplateConfig.getTemplate(EnumConstants.MailTemplateType.LEAVE_SUBMITTED_MANAGER);

            String mailBody = String.format(template,
                    managerName,
                    employeeName,
                    employeeId,
                    request.getCategoryType(),
                    fromDate,
                    endDate,
                    leaveDuration,
                    totalHolidays,
                    request.getContext(),
                    managerLink
            );
            mailService.sendMail(managerCompanyMail, mailSubject, mailBody, request.getAttachmentFile());
            log.info("Leave approval email sent from '{}' to '{}'", employee.getCompanyEmail(), managerCompanyMail);
        }
        // Send confirmation email to employee
        String employeeMailSubject = "Leave Request Submitted Successfully – " + employee.getFirstName()+ " "+employee.getLastName();
        String employeeLink="https://portal.company.com/leave";;
        String template = mailTemplateConfig.getTemplate(EnumConstants.MailTemplateType.LEAVE_SUBMITTED_EMPLOYEE);

        String employeeMailBody = String.format(template,
                employee.getFirstName()+ " " + employee.getLastName(),
                employee.getCompanyId() != null ? employee.getCompanyId().toString() : "N/A",
                employee.getDesignation() != null ? employee.getDesignation() : "N/A",
                request.getCategoryType(),
                fromDate,
                endDate,
                leaveDuration,
                request.getContext(),
                manager.getFirstName() + " " + manager.getLastName(),
                employeeLink
        );
        mailService.sendMail(employee.getCompanyEmail(), employeeMailSubject, employeeMailBody, request.getAttachmentFile());
        log.info("Leave submission confirmation email sent to '{}'", employee.getCompanyEmail());
        //  Return response
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
     * @param financialType       Optional leave type filter (PAID, UNPAID, etc.).
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
    public Page<LeaveResponseDTO> getLeaves(UUID employeeId, String month, String financialType,String leaveCategory, String status,
                                            int page, int size, String sort, UserDetails user,Boolean futureApproved,         // NEW PARAM
                                            LocalDate date) {

        log.info("===== Fetching leave summary =====");
        log.info("Requested by user: '{}', Filters -> employeeId: {}, month: {}, finacialType: {},  categoryType: {}, status: {}, page: {}, size: {}, sort: {}",
                user.getUsername(), employeeId, month, financialType,leaveCategory, status, page, size, sort);

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

        if (financialType != null && !financialType.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byFinancialType(financialType)
                    : spec.and(LeaveSpecifications.byFinancialType(financialType));
        }

        if (leaveCategory != null && !leaveCategory.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byLeaveCategory(leaveCategory)
                    : spec.and(LeaveSpecifications.byLeaveCategory(leaveCategory));
        }

        if (status != null && !status.isBlank()) {
            spec = (spec == null) ? LeaveSpecifications.byStatus(status)
                    : spec.and(LeaveSpecifications.byStatus(status));
            log.debug("Applied leave status filter: {}", status);
        }

        // ===========================
        // Apply future approved leaves filter
        // ===========================
        if (Boolean.TRUE.equals(futureApproved)) {
            Specification<EmployeeLeave> futureSpec = LeaveSpecifications.futureApprovedLeaves();
            spec = (spec == null) ? futureSpec : spec.and(futureSpec);
            log.debug("Applied future approved leaves filter");
        }

        // ===========================
        // Apply date-based on-leave check
        // ===========================
        if (date != null) {
            Specification<EmployeeLeave> onDateSpec = LeaveSpecifications.leavesOnDate(date);
            spec = (spec == null) ? onDateSpec : spec.and(onDateSpec);
            log.debug("Applied on-leave check filter for date {}", date);
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
        EnumConstants.Role role = user.getRole();
        if (role == EnumConstants.Role.EMPLOYEE) {
            if (!leave.getEmployee().getEmployeeId().equals(employee.getEmployeeId())) {
                log.warn("EMPLOYEE '{}' attempted to access leaveId={} which does not belong to them",
                        email, leaveId);
                throw new AccessDeniedException("You are not authorized to view this leave");
            }
            log.info("EMPLOYEE '{}' accessed their own leaveId={}", email, leaveId);
        }

        else if (role == EnumConstants.Role.MANAGER) {
            List<Employee> teamMembers = employeeRepository.findByReportingManager_EmployeeId(employee.getEmployeeId());

            boolean isTeamMember = teamMembers.stream()
                    .anyMatch(member -> member.getEmployeeId().equals(leave.getEmployee().getEmployeeId()));

            if (!isTeamMember) {
                log.warn("MANAGER '{}' attempted to access leaveId={} which does not belong to their team",
                        email, leaveId);
                throw new AccessDeniedException("You are not authorized to view this leave");
            }

            log.info("MANAGER '{}' accessed team member's leaveId={} (EmployeeId={})",
                    email, leaveId, leave.getEmployee().getEmployeeId());
        }

        else if (role == EnumConstants.Role.ADMIN) {
            log.info("ADMIN '{}' accessed leaveId={}", email, leaveId);
        }

        else {
            log.warn("User '{}' with unknown role '{}' tried to access leaveId={}", email, role, leaveId);
            throw new AccessDeniedException("Unauthorized role access");
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
        if (existingLeave.getStatus() != EnumConstants.LeaveStatus.PENDING
                || Boolean.TRUE.equals(existingLeave.getWithdrawn())) {
            log.warn("LeaveId={} update rejected — current status={}, withdrawn={}",
                    leaveId, existingLeave.getStatus(), existingLeave.getWithdrawn());
            throw new IllegalArgumentException(
                    "Leave cannot be updated: only pending and not withdrawn leaves are allowed"
            );
        }


        // Apply non-null updates
        if (request.getCategoryType() != null) {
            existingLeave.setLeaveCategory(request.getCategoryType());
        }
        if (request.getFinancialType() != null) {
            existingLeave.setFinancialType(request.getFinancialType());
        }

        if (request.getContext() != null && !request.getContext().isBlank()) {
            existingLeave.setContext(request.getContext());
        }

        if (request.getPartialDay() != null) {
            existingLeave.setPartialDay(request.getPartialDay());
        }

        if (request.getLeaveDuration() != null) {
            existingLeave.setLeaveDuration(request.getLeaveDuration());
        }

        if (request.getAttachmentFile() != null && !request.getAttachmentFile().isEmpty()) {
            existingLeave.setAttachmentUrl(null);
        }

        // Recalculate date-related fields if dates changed
        LocalDate fromDate = request.getFromDate() != null ? request.getFromDate() : existingLeave.getFromDate();
        LocalDate toDate = request.getToDate() != null ? request.getToDate() : existingLeave.getToDate();


        if (!fromDate.equals(existingLeave.getFromDate()) || !toDate.equals(existingLeave.getToDate())) {
            DateRangeRequestDTO rangeRequestDTO = new DateRangeRequestDTO(fromDate, toDate,request.getPartialDay());
            WorkdayResponseDTO workdayResponseDTO = calculateWorkingDays(rangeRequestDTO);

           Double leaveDuration= request.getLeaveDuration();
            int totalHolidays = workdayResponseDTO.getTotalHolidays();
            existingLeave.setFromDate(fromDate);
            existingLeave.setToDate(toDate);
            existingLeave.setLeaveDuration(leaveDuration);
            existingLeave.setHolidays(totalHolidays);

            log.info("Recalculated leave period for leaveId={}: workingDays={}, holidays={}", leaveId, leaveDuration, totalHolidays);
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


        // Update timestamp and save
        existingLeave.setUpdatedAt(LocalDateTime.now());
        EmployeeLeave updatedLeave = leaveRepository.save(existingLeave);
        // Inside your method, before using them
        String employeeName = employee.getFirstName() + " " + employee.getLastName();
        String managerName = manager.getFirstName() + " " + manager.getLastName();

        if (manager != null) {
            // Prepare variables
            String managerCompanyMail = manager.getCompanyEmail();
             managerName = manager.getFirstName() + " " + manager.getLastName();
             employeeName = employee.getFirstName() + " " + employee.getLastName();
            String employeeId = employee.getCompanyId() != null ? employee.getCompanyId().toString() : "N/A";
            String managerLink = "https://portal.company.com/leave";

            // 1. Email to Manager
            String mailSubject = "Leave Request Updated – " + employeeName;
            String template = mailTemplateConfig.getTemplate(EnumConstants.MailTemplateType.LEAVE_UPDATED_MANAGER);

            String mailBody = String.format(template,
                    managerName,
                    employeeName,
                    employeeId,
                    existingLeave.getLeaveCategory() != null ? existingLeave.getLeaveCategory() : "N/A",
                    existingLeave.getFromDate(),
                    existingLeave.getToDate(),
                    existingLeave.getLeaveDuration() != null ? existingLeave.getLeaveDuration() : 0,
                    existingLeave.getHolidays() != null ? existingLeave.getHolidays() : 0,
                    existingLeave.getContext() != null ? existingLeave.getContext() : "N/A",
                    managerLink
            );


            mailService.sendMail(managerCompanyMail, mailSubject, mailBody, request.getAttachmentFile());
            log.info("Leave update email sent from '{}' to '{}'", employee.getCompanyEmail(), managerCompanyMail);
        }

      // 2. Optional: Confirmation email to Employee
        String employeeMailSubject = "Leave Request Updated Successfully – " + employeeName;
        String employeeLink = "https://portal.company.com/leave";

        String template = mailTemplateConfig.getTemplate(EnumConstants.MailTemplateType.LEAVE_UPDATED_EMPLOYEE);

        String employeeMailBody = String.format(template,
                employeeName,
                existingLeave.getLeaveCategory() != null ? existingLeave.getLeaveCategory() : "N/A",
                existingLeave.getFromDate(),
                existingLeave.getToDate(),
                existingLeave.getLeaveDuration() != null ? existingLeave.getLeaveDuration() : 0,
                existingLeave.getHolidays() != null ? existingLeave.getHolidays() : 0,
                existingLeave.getContext() != null ? existingLeave.getContext() : "N/A",
                managerName,
                employeeLink
        );

        mailService.sendMail(employee.getCompanyEmail(), employeeMailSubject, employeeMailBody, request.getAttachmentFile());
        log.info("Leave update confirmation email sent to '{}'", employee.getCompanyEmail());

        return mapToResponse(updatedLeave);
    }



    /**
     * Withdraws a leave request if it's pending or starts today/future.
     * Only the leave owner can perform this action.
     * Updates status to WITHDRAWN instead of deleting the record.
     * Throws exception if leave is approved, started, or not found.
     */
    @Override
    public void withdrawLeave(UUID leaveId, String email) {
        log.info("Attempting to withdraw leave with leaveId={} for user '{}'", leaveId, email);

        // 1️ Fetch user and employee
        User user = userRepository.findByCompanyEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        // 2️ Fetch leave
        EmployeeLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found with ID: " + leaveId));

        // 3️ Check ownership
        if (!leave.getEmployee().getEmployeeId().equals(employee.getEmployeeId())) {
            throw new AccessDeniedException("You are not authorized to withdraw this leave");
        }

        LocalDate today = LocalDate.now();

        //  Only allow withdrawal if leave is PENDING OR future-dated
        boolean isPending = leave.getStatus() == EnumConstants.LeaveStatus.PENDING;
        boolean isFuture = leave.getFromDate().isAfter(today);

        if (!(isPending || isFuture)) {
            throw new IllegalStateException("Only unapproved (PENDING) or future-dated leaves can be withdrawn");
        }

        //  Withdraw leave
        leave.setStatus(EnumConstants.LeaveStatus.WITHDRAWN);
        leave.setWithdrawn(true); // mark as withdrawn
        leave.setUpdatedAt(LocalDateTime.now());
        leaveRepository.save(leave);

        log.info("Leave withdrawn successfully for leaveId={} by user '{}'", leaveId, email);
    }



    /**
     * Updates the status of a leave request (APPROVED or REJECTED) by an admin
     * and sends an email notification to the employee.
     *
     * @param leaveId      UUID of the leave to update
     * @param status       new leave status (APPROVED or REJECTED)
     * @param approverComment optional comment from the admin
     * @param approverEmail   email of the admin performing the update
     * @return updated leave details as {@link LeaveResponseDTO}
     * @throws EntityNotFoundException if the admin user, admin entity, or leave entity is not found
     */
    @Override
    public LeaveResponseDTO updateLeaveStatus(UUID leaveId, EnumConstants.LeaveStatus status, String approverComment, String approverEmail) {
        log.info("Approver '{}' updating leaveId={} with status={}", approverEmail, leaveId, status);

        // Check if approver is Manager or Admin
        Employee approver = employeeRepository.getEmployeeByEmail(approverEmail);
        boolean isAdmin = false;
        String approverName;

        if (approver == null) {
            Admin admin = adminRepository.findByEmail(approverEmail)
                    .orElseThrow(() -> new EntityNotFoundException("Admin not found with email: " + approverEmail));
            approverName = admin.getFullName();
            isAdmin = true;
        } else {
            approverName = approver.getFirstName()+ " "+approver.getLastName();
        }

        // Fetch leave with employee and reporting manager
        EmployeeLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new EntityNotFoundException("Leave not found with id: " + leaveId));

        UUID employeeId = leave.getEmployee().getEmployeeId();
        // Fetch employee along with reporting manager in one query
        Employee employee = employeeRepository.findByIdWithManager(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if (leave.getStatus() != EnumConstants.LeaveStatus.PENDING) {
            log.warn("LeaveId={} update rejected — current status={}", leaveId, leave.getStatus());
            throw new IllegalStateException("Only pending leaves can be updated");
        }

        System.out.println(employee.getDesignation());
        Double availableLeaves = employee.getAvailableLeaves() != null ? employee.getAvailableLeaves() : 0;
        Double workingDays = leave.getLeaveDuration() != null ? Double.valueOf(leave.getLeaveDuration()) : 0.0;

        // Validate working days
        if (workingDays <= 0) {
            throw new IllegalArgumentException("Working days must be greater than zero");
        }

        // Deduct only if leave type is not PAID
        if (leave.getFinancialType() != null && leave.getFinancialType() != EnumConstants.FinancialType.UNPAID&&status.equals(EnumConstants.LeaveStatus.APPROVED)) {
            if (workingDays > availableLeaves) {
                throw new IllegalArgumentException("Insufficient available leaves");
            }


            Double result = (availableLeaves - workingDays);
            employee.setAvailableLeaves(result);

            // Persist updated employee safely
            employeeRepository.save(employee);
        }


        // Validate that the logged-in manager is the reporting manager
        if (!isAdmin) {
            if (employee.getReportingManager() == null ||
                    !employee.getReportingManager().getEmployeeId().equals(approver.getEmployeeId())) {
                log.warn("Unauthorized attempt: '{}' tried to update leaveId={} not belonging to their team",
                        approverEmail, leaveId);
                throw new AccessDeniedException("You are not authorized to update this leave");
            }
            leave.setReportingManager(approver); // keep reporting manager for consistency
        }

        // Update leave details
        leave.setStatus(status != null ? status : leave.getStatus());
        leave.setApproverComment(approverComment != null ? approverComment : leave.getApproverComment());
        leave.setApproverName(approverName);
        leave.setUpdatedAt(LocalDateTime.now());

        EmployeeLeave updatedLeave = leaveRepository.save(leave);
        log.info("Leave status updated successfully: {}", updatedLeave.getStatus());

        String statusMsg = leave.getStatus() == EnumConstants.LeaveStatus.APPROVED ? "approved" : "rejected";
        notificationService.sendNotification(
                leave.getEmployee().getUser(),
                "Your leave from " + leave.getFromDate() + " to " + leave.getToDate() +
                        " (" + leave.getLeaveDuration() + " days) has been " + statusMsg +
                        ". Comment: " + approverComment,
                leave.getLeaveId()
        );

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
    private void sendLeaveStatusNotification(EmployeeLeave leave, Employee employee) {
        if (leave == null || employee == null) {
            log.warn("Cannot send email: leave or employee is null for leaveId={}", leave != null ? leave.getLeaveId() : "null");
            return;
        }

        try {
            String approverName = leave.getApproverName();
            String employeeEmail = employee.getCompanyEmail();
            String employeeName = employee.getFirstName()+ " "+employee.getLastName();

            String portalLink = "https://portal.company.com/leave";
            String leaveType = leave.getLeaveCategory() != null ? leave.getLeaveCategory().name() : "N/A";
            String totalDays = leave.getLeaveDuration() != null ? String.valueOf(leave.getLeaveDuration()) : "N/A";
            String approvalDate = leave.getUpdatedAt() != null ? leave.getUpdatedAt().toString() : "N/A";
            String approverComment = leave.getApproverComment() != null ? leave.getApproverComment() : "No comments";

            String subject = "";
            String template;
            String body="";

            if (leave.getStatus() == EnumConstants.LeaveStatus.APPROVED) {
                subject = String.format("Leave Approved – %s to %s", leave.getFromDate(), leave.getToDate());
                template = mailTemplateConfig.getTemplate(EnumConstants.MailTemplateType.LEAVE_APPROVED);
                body = String.format(template,
                        employeeName,
                        leave.getFromDate(),
                        leave.getToDate(),
                        approverName,
                        leaveType,
                        totalDays,
                        approverName,
                        approvalDate,
                        portalLink
                );
            } else if (leave.getStatus() == EnumConstants.LeaveStatus.REJECTED) {
                subject = String.format("Leave Request Update – %s to %s", leave.getFromDate(), leave.getToDate());
                template = mailTemplateConfig.getTemplate(EnumConstants.MailTemplateType.LEAVE_REJECTED);
                body = String.format(template,
                        employeeName,
                        leave.getFromDate(),
                        leave.getToDate(),
                        approverName,
                        leaveType,
                        totalDays,
                        approverName,
                        approverComment,
                        portalLink
                );
            }
            mailService.sendMail( employeeEmail, subject, body, null);

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
    public WorkdayResponseDTO calculateWorkingDays(DateRangeRequestDTO request) {
        LocalDate fromDate = request.getFromDate();
        LocalDate endDate = request.getToDate();

        if (fromDate == null || endDate == null || fromDate.isAfter(endDate)) {
            log.error("Invalid date range provided: fromDate={}, toDate={}", fromDate, endDate);
            throw new IllegalArgumentException("Invalid date range provided.");
        }

        // Fetch holidays in the given date range
        List<HolidayCalendar> holidaysList = holidayRepository.findByHolidayDateBetween(fromDate, endDate);
        Set<LocalDate> holidaysSet = holidaysList.stream()
                .map(HolidayCalendar::getHolidayDate)
                .collect(Collectors.toSet());
        int holidays = holidaysSet.size();
        log.debug("Number of holidays between {} and {}: {}", fromDate, endDate, holidays);

        Set<LocalDate> nonWorkingDays = new HashSet<>(holidaysSet);
        // Count weekends
        int weekendCount = 0;
        LocalDate date = fromDate;
        while (!date.isAfter(endDate)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                weekendCount++;
                nonWorkingDays.add(date);
            }
            date = date.plusDays(1);
        }
        log.debug("Number of weekend days between {} and {}: {}", fromDate, endDate, weekendCount);

        // Calculate totals
        int totalDays = (int) ChronoUnit.DAYS.between(fromDate, endDate) + 1;
        int totalHolidays = nonWorkingDays.size();
        Double leaveDuration = (double) (totalDays - totalHolidays);

        Boolean partialDay = Boolean.TRUE.equals(request.getPartialDay());
        if (partialDay) {
            leaveDuration = (leaveDuration != null ? leaveDuration : 0.0) - 0.5;
            if (leaveDuration < 0) {
                leaveDuration = 0.0;
            }
        }

        log.info("Total days: {}, Holidays: {}, Weekends: {}, Working days: {}",
                totalDays, holidays, weekendCount, leaveDuration);

        return WorkdayResponseDTO.builder()
                .totalDays(totalDays)
                .holidays(holidays)
                .weekends(weekendCount)
                .totalHolidays(totalHolidays)
                .leaveDuration(leaveDuration)
                .build();
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
                .approverName(leave.getApproverName())
                .employeeName(
                        leave.getEmployee() != null
                                ? leave.getEmployee().getFirstName()+ " "+leave.getEmployee().getLastName()
                                : null
                )
                .fromDate(leave.getFromDate())
                .toDate(leave.getToDate())
                .leaveCategoryType(leave.getLeaveCategory() != null ? leave.getLeaveCategory().name() : null)
                .financialType(leave.getFinancialType()!=null?leave.getFinancialType().name():null)
                .subject(leave.getSubject())
                .context(leave.getContext())
                .leaveDuration(leave.getLeaveDuration())
                .holidays(leave.getHolidays())
                .status(leave.getStatus() != null ? leave.getStatus().name() : null)
                .approverComment(leave.getApproverComment())
                .attachmentUrl(leave.getAttachmentUrl())
                .build();
    }
    /**
     * @param employeeId   UUID of the employee requesting leave
     * @param leaveDuration Requested leave duration in days
     * @return WebResponseDTO containing LeaveAvailabilityDTO with availability info and guidance
     */
    @Override
    public WebResponseDTO<LeaveAvailabilityDTO> checkLeaveAvailability(UUID employeeId, Double leaveDuration) {
        log.info("Checking  leave availability for employeeId={}, requestedDuration={}, leaveType={}", employeeId, leaveDuration);


        // Validate leave duration
        if (leaveDuration == null || leaveDuration <= 0) {
            log.warn("Invalid leave duration provided: {}", leaveDuration);
            return new WebResponseDTO<>(false, "Invalid leave duration. Must be greater than 0.", 400);
        }

        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            log.warn("Employee not found for ID: {}", employeeId);
            return new WebResponseDTO<>(false, "Employee not found for ID: " + employeeId, 404);
        }

        Employee employee = employeeOpt.get();
        Double availableLeaves = employee.getAvailableLeaves() != null ? employee.getAvailableLeaves() : 0;
        log.debug("Employee {} has {} available CASUAL leaves", employeeId, availableLeaves);

        boolean sufficient = availableLeaves >= leaveDuration;

        String message;
        if (sufficient) {
            message = "You have sufficient  leaves.";
        } else {
            message = "You do not have enough  leaves. " +
                    "Please adjust the leave dates or duration, " +
                    "otherwise select UNPAID Leave.";
        }
        LeaveAvailabilityDTO dto = LeaveAvailabilityDTO.builder()
                .isAvailable(sufficient)
                .availableLeaves(availableLeaves)
                .requestedLeave(leaveDuration)
                .message(message)
                .build();

        log.info("Leave check result for employeeId={}: isAvailable={}, leaveType={}", employeeId, sufficient);

        return new WebResponseDTO<>(true, "Leave check completed.", 200, dto);
    }

    /**
     * Fetches all pending leaves for a manager identified by their company email.
     *
     * This method performs the following steps:
     * 1. Finds the User entity by the provided manager email.
     * 2. Retrieves the corresponding Employee record for that User.
     * 3. Queries the leave repository for all pending leaves under this manager.
     * 4. Maps the pending EmployeeLeave entities to ManagerLeaveDashboardDTO objects.
     *
     * @param userCompanyEmail the company email of the manager or admin
     * @return a list of ManagerLeaveDashboardDTO containing pending leave details
     * @throws EntityNotFoundException if the User or Employee corresponding to the email is not found
     */
    public List<PendingLeavesResponseDTO> getPendingLeavesForManagerAndAdmin(String userCompanyEmail) {
        log.info("==== Start: Fetching pending leaves for user email={} ====", userCompanyEmail);

        // 1. Fetch user
        User user = userRepository.findByCompanyEmail(userCompanyEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userCompanyEmail));

        // 2. Determine role
        String role = user.getRole().name();
        log.info("User {} has role={}", userCompanyEmail, role);

        List<EmployeeLeave> leaves;

        // 3. Logic for Manager and Admin
        if (role.equals("MANAGER")) {
            // Manager: Only team’s pending leaves
            Employee manager = employeeRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Manager record not found for userId: " + user.getUserId()));

            leaves = leaveRepository.findPendingLeavesByManager(
                    manager.getEmployeeId(),
                    EnumConstants.LeaveStatus.PENDING
            );
            log.info("Manager {} -> Found {} pending leaves", manager.getEmployeeId(), leaves.size());

        } else if (role.equals("ADMIN")) {
            // Admin: Fetch all pending leaves (any manager or none)
            leaves = leaveRepository.findByStatus(EnumConstants.LeaveStatus.PENDING);
            log.info("Admin {} -> Found {} pending leaves (all employees)", userCompanyEmail, leaves.size());

        } else {
            throw new AccessDeniedException("Only MANAGER or ADMIN can view pending leaves");
        }

        // 4. Map to DTOs
        return leaves.stream()
                .map(el -> PendingLeavesResponseDTO.builder()
                        .leaveId(el.getLeaveId())
                        .employeeName(el.getEmployee().getFirstName() + " " + el.getEmployee().getLastName())
                        .leaveCategoryTpe(el.getLeaveCategory().name())
                        .financialType(el.getFinancialType().name())
                        .fromDate(el.getFromDate())
                        .toDate(el.getToDate())
                        .leaveDuration(el.getLeaveDuration())
                        .context(el.getContext())
                        .attachmentUrl(el.getAttachmentUrl())
                        .remainingLeaves(el.getEmployee().getAvailableLeaves())
                        .status(el.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * Fetches approved leave days for the specified employee and year, applying role-based access rules.
     * <p>
     * - EMPLOYEE: Can view only their own leaves.<br>
     * - MANAGER: Can view only subordinates’ leaves.<br>
     * - ADMIN: Can view any employee’s leaves.<br>
     * <p>
     * Weekends (Saturday, Sunday) and company-declared holidays are excluded from the results.
     *
     * @param companyMail the logged-in user’s company email for role and access validation
     * @param employeeId  the target employee ID (required for MANAGER and ADMIN roles)
     * @param currentYear the year to fetch approved leaves for; if null, defaults to the current year
     * @return list of {@link EmployeeLeaveDayDTO} containing daily approved leave entries excluding weekends and holidays
     */
    @Override
    public List<EmployeeLeaveDayDTO> getApprovedLeavesForCurrentYear(String companyMail, UUID employeeId, LocalDate currentYear) {
        log.info("Fetching approved leaves for user: {} (employeeId param: {})", companyMail, employeeId);

        // Get logged-in user
        User loggedInUser = userRepository.findByCompanyEmail(companyMail)
                .orElseThrow(() -> new RuntimeException("User not found: " + companyMail));

        String role = loggedInUser.getRole().name();
        log.debug("User role identified as: {}", role);

        Employee targetEmployee;

        //  Role-based employee selection
        if (role.equals("EMPLOYEE")) {
            // Employee can only view own data
            targetEmployee = employeeRepository.findByUser_UserId(loggedInUser.getUserId())
                    .orElseThrow(() -> new RuntimeException("Employee record not found for user: " + companyMail));

        } else if (role.equals("MANAGER")) {
            // Manager must pass employeeId and it must belong to them
            if (employeeId == null)
                throw new RuntimeException("Manager must provide employeeId to fetch subordinate's leaves");

            Employee manager = employeeRepository.findByUser_UserId(loggedInUser.getUserId())
                    .orElseThrow(() -> new RuntimeException("Manager record not found"));

             targetEmployee = employeeRepository.findByIdWithManager(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

            if (targetEmployee.getReportingManager() == null ||
                    !targetEmployee.getReportingManager().getEmployeeId().equals(manager.getEmployeeId())) {
                throw new RuntimeException("Access denied: Employee does not report to this manager");
            }

        } else if (role.equals("ADMIN")) {
            // Admin can view any employee's leaves
            if (employeeId == null)
                throw new RuntimeException("Admin must provide employeeId to fetch employee's leaves");

            targetEmployee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        } else {
            throw new RuntimeException("Unsupported role: " + role);
        }

        //  Compute date range for current year
        LocalDate now = (currentYear != null) ? currentYear : LocalDate.now();
        LocalDate startOfYear = now.withDayOfYear(1);
        LocalDate endOfYear = now.withMonth(12).withDayOfMonth(31);

        // Fetch approved leaves
        List<EmployeeLeave> leaves = leaveRepository.findByEmployeeAndStatusAndFromDateBetween(
                targetEmployee,
                EnumConstants.LeaveStatus.APPROVED,
                startOfYear,
                endOfYear
        );
        log.info("Found {} approved leaves for employee: {}", leaves.size(), targetEmployee.getEmployeeId());

        if (leaves.isEmpty()) return Collections.emptyList();

        //  Fetch holidays
        List<HolidayCalendar> holidayList = holidayRepository.findByHolidayDateBetween(startOfYear, endOfYear);
        Set<LocalDate> holidays = holidayList.stream()
                .map(HolidayCalendar::getHolidayDate)
                .collect(Collectors.toSet());
        log.debug("Loaded {} holidays for year {}", holidays.size(), now.getYear());

        // Convert each leave into daily entries and exclude weekends & holidays
        List<EmployeeLeaveDayDTO> leaveDays = leaves.stream()
                .flatMap(leave -> {
                    LocalDate from = leave.getFromDate();
                    LocalDate to = leave.getToDate();
                    double dailyDuration = leave.getPartialDay() ? leave.getLeaveDuration() : 1.0;

                    return from.datesUntil(to.plusDays(1))
                            .filter(date -> !isNonWorkingDay(date, holidays))
                            .map(date -> EmployeeLeaveDayDTO.builder()
                                    .date(date)
                                    .leaveCategory(leave.getLeaveCategory())
                                    .duration(dailyDuration)
                                    .build());
                })
                .toList();

        log.info("Returning {} working leave-day entries for employee {}", leaveDays.size(), targetEmployee.getEmployeeId());
        return leaveDays;
    }

    /**
     * Check whether a date is weekend or holiday.
     */
    private boolean isNonWorkingDay(LocalDate date, Set<LocalDate> holidays) {
        DayOfWeek day = date.getDayOfWeek();
        boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
        boolean isHoliday = holidays.contains(date);
        return isWeekend || isHoliday;
    }


}
