package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.NotificationDTO;
import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.exceptions.customExceptions.UserNotFoundException;
import com.EmpTimeHub.exceptions.customExceptions.UserRoleNotFoundException;
import com.EmpTimeHub.model.TimeSheetModel;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.ProjectRepository;
import com.EmpTimeHub.repository.TimeSheetRepository;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.MailService;
import com.EmpTimeHub.service.NotificationService;
import com.EmpTimeHub.service.TimeSheetService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Mohammad Shoaib
 * @since 2025-10-13
 * <p>
 * <p>
 * Implementation of {@link TimeSheetService} that manages
 * CRUD operations and filtering logic for employee timesheets.
 */
@Service
@AllArgsConstructor
public class TimeSheetServiceImpl implements TimeSheetService {

    private static final Logger LOG = LoggerFactory.getLogger(TimeSheetServiceImpl.class);

    private final TimeSheetRepository timeSheetRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final NotificationService notificationService;

    /**
     * Creates a new timesheet entry for the logged-in employee.
     *
     * @param timeSheetModels   TimeSheetModel containing timesheet details
     * @param loggedInUserEmail Logged-in employee's company email
     * @return The created {@link TimeSheet} entity
     * @throws UserNotFoundException if user email does not match company email
     */
    @Override
    public List<TimeSheet> createTimeSheet(List<TimeSheetModel> timeSheetModels, String loggedInUserEmail) {
        LOG.info("Creating timesheet for user: {}", loggedInUserEmail);

        Employee emp = employeeRepository.getEmployeeByEmail(loggedInUserEmail);
        if (emp == null) {
            LOG.error("Employee not found for email: {}", loggedInUserEmail);
            throw new UserNotFoundException("Employee not found with provided email");
        }

        if (!emp.getCompanyEmail().equals(loggedInUserEmail)) {
            LOG.error("Email mismatch: provided [{}], expected [{}]", loggedInUserEmail, emp.getCompanyEmail());
            throw new UserNotFoundException("Please login with your company email address");
        }

        Client client = emp.getClient();
        Project project = projectRepository.findByEmployeeAndClient(emp, client);

        List<TimeSheet> timeSheetsToSave = new ArrayList<>();
        LocalDate projectStart = project.getStartDate();
        LocalDate projectEnd = project.getEndDate();

        for (TimeSheetModel timeSheetModel : timeSheetModels) {
            LocalDate currentDate = timeSheetModel.getWorkDate();

            if (currentDate.isBefore(projectStart) || currentDate.isAfter(projectEnd)) {
                LOG.error("Work date [{}] not within project duration [{} - {}]", currentDate, projectStart, projectEnd);
                throw new RuntimeException("Your project work date must " +
                        "be within start[" + projectStart + "] and end date[" + projectEnd + "] range!");
            }

            LOG.debug("Validating work date [{}] between project start [{}] and end [{}]",
                    currentDate, projectStart, projectEnd);

            TimeSheet sheet = TimeSheet.builder()
                    .timesheetId(timeSheetModel.getTimesheetId())
                    .employee(emp)
                    .client(client)
                    .hoursWorked(timeSheetModel.getHoursWorked())
                    .workDate(timeSheetModel.getWorkDate())
                    .taskName(timeSheetModel.getTaskName())
                    .managerComment(timeSheetModel.getManagerComment())
                    .status(EnumConstants.WorkRequest.SUBMITTED.name())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            timeSheetsToSave.add(sheet);
        }

        projectRepository.save(project);
        List<TimeSheet> savedSheets = timeSheetRepository.saveAll(timeSheetsToSave);
        List<UUID> ids = savedSheets.stream().map(TimeSheet::getTimesheetId).toList();
        LOG.info("Timesheets created successfully for employee: {}, timesheetId: {}, total timesheets: {} ",
                emp.getEmployeeId(), ids, savedSheets.size());

        return savedSheets.isEmpty() ? null : savedSheets;
    }

    /**
     * Retrieves a timesheet by its ID for the logged-in employee.
     *
     * @param timesheetId       Timesheet unique identifier
     * @param loggedInUserEmail Logged-in employee's company email
     * @return A {@link TimeSheetResponseDto} with detailed timesheet information
     */
    @Override
    public TimeSheetResponseDto getTimeSheetById(UUID timesheetId, String loggedInUserEmail) {
        LOG.info("Fetching timesheet with ID: {} for user: {}", timesheetId, loggedInUserEmail);

        TimeSheet oneTimeSheet = timeSheetRepository.findById(timesheetId)
                .orElseThrow(() -> {
                    LOG.error("Timesheet not found for ID: {}", timesheetId);
                    return new RuntimeException("Timesheet not found");
                });

        Employee emp = employeeRepository.getEmployeeByEmail(loggedInUserEmail);
        Client client = emp.getClient();

        LOG.debug("Mapping timesheet [{}] to DTO for employee [{}]", timesheetId, emp.getEmployeeId());

        return TimeSheetResponseDto.builder()
                .timesheetId(oneTimeSheet.getTimesheetId())
                .clientId(client.getClientId())
                .clientName(client.getCompanyName())
                .employeeId(emp.getEmployeeId())
                .employeeName(emp.getFirstName() + " " + emp.getLastName())
                .taskName(oneTimeSheet.getTaskName())
                .managerComment(oneTimeSheet.getManagerComment())
                .workDate(oneTimeSheet.getWorkDate())
                .workedHours(oneTimeSheet.getHoursWorked())
                .status(oneTimeSheet.getStatus())
                .createdAt(oneTimeSheet.getCreatedAt())
                .build();
    }

    /**
     * Retrieves all timesheets for the logged-in user with pagination, sorting, and optional date filtering.
     *
     * @param page              Page number (zero-based)
     * @param size              Number of records per page
     * @param direction         Sort direction ("asc" or "desc")
     * @param orderBy           Field name to sort by
     * @param loggedInUserEmail Logged-in employee's email
     * @param startDate         Optional start date filter
     * @param endDate           Optional end date filter
     * @return A paginated {@link Page} of {@link TimeSheetResponseDto}
     */
    @Override
    public Page<TimeSheetResponseDto> getAllTimeSheets(int page, int size, String direction,
                                                       String orderBy, String loggedInUserEmail,
                                                       LocalDate startDate, LocalDate endDate) {
        LOG.info("Fetching all timesheets for user: {}, page: {}, size: {}", loggedInUserEmail, page, size);

        User user = userRepository.findByCompanyEmail(loggedInUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found!!"));

        String role = user.getRole().name();

        if (
                !role.equals(EnumConstants.Role.EMPLOYEE.name()) &&
                        !role.equals(EnumConstants.Role.ADMIN.name()) &&
                        !role.equals(EnumConstants.Role.MANAGER.name())
        ) {
            LOG.error("Unauthorized role: {}", role);
            throw new UserRoleNotFoundException("Only 'EMPLOYEE', 'ADMIN', or 'MANAGER' roles have access");
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction.toUpperCase()), orderBy));

        Page<TimeSheet> employeeTS;
        Employee emp = null;
        Project project = null;

        if (role.equals(EnumConstants.Role.EMPLOYEE.name())) {
            emp = employeeRepository.getEmployeeByEmail(loggedInUserEmail);
            if (emp == null) {
                LOG.error("Employee not found for email: {}", loggedInUserEmail);
                throw new UserNotFoundException("Employee not found");
            }

            project = projectRepository.findByEmployeeAndClient(emp, emp.getClient());

            if (startDate != null && endDate != null) {
                LOG.debug("Employee - Applying date filter from {} to {}", startDate, endDate);
                employeeTS = timeSheetRepository.findByEmployeeAndWorkDateBetween(emp, startDate, endDate, pageable);
            } else {
                employeeTS = timeSheetRepository.findByEmployee(emp, pageable);
            }

        } else {

            if (startDate != null && endDate != null) {
                LOG.debug("Admin - Applying date filter from {} to {}", startDate, endDate);
                employeeTS = timeSheetRepository.findByWorkDateBetween(startDate, endDate, pageable);
            } else {
                employeeTS = timeSheetRepository.findAll(pageable);
            }
        }

        List<TimeSheetResponseDto> collect = employeeTS.stream()
                .map(dto -> {
                    Employee e = dto.getEmployee();
                    Client c = dto.getClient();
                    Project p = projectRepository.findByEmployeeAndClient(e, c);

                    return TimeSheetResponseDto.builder()
                            .timesheetId(dto.getTimesheetId())
                            .employeeId(e.getEmployeeId())
                            .employeeName(e.getFirstName() + " " + e.getLastName())
                            .clientId(c.getClientId())
                            .clientName(c.getCompanyName())
                            .taskName(dto.getTaskName())
                            .managerComment(dto.getManagerComment())
                            .workDate(dto.getWorkDate())
                            .workedHours(dto.getHoursWorked())
                            .createdAt(dto.getCreatedAt())
                            .updatedAt(dto.getUpdatedAt())
                            .status(dto.getStatus())
                            .projectName(p != null ? p.getProjectName() : null)
                            .projectStartedAt(p != null ? p.getStartDate() : null)
                            .projectEndedAt(p != null ? p.getEndDate() : null)
                            .build();
                })
                .collect(Collectors.toList());

        LOG.info("Timesheets fetched successfully: {} records found", employeeTS.getTotalElements());
        return new PageImpl<>(collect, pageable, employeeTS.getTotalElements());
    }

    /**
     * Updates an existing timesheet for the logged-in employee.
     *
     * @param updatedSheets     Updated timesheet details
     * @param loggedInUserEmail Logged-in employee email for validation
     * @throws UserNotFoundException if the email does not match the employee
     */
    @Override
    public void updateTimeSheet(List<TimeSheetModel> updatedSheets, String loggedInUserEmail) {
        LOG.info("Updating {} timesheets for user: {}", updatedSheets.size(), loggedInUserEmail);

        List<TimeSheet> timesheetsToUpdate = new ArrayList<>();

        for (TimeSheetModel model : updatedSheets) {
            UUID timesheetId = model.getTimesheetId();

            TimeSheet ts = timeSheetRepository.findById(timesheetId)
                    .orElseThrow(() -> {
                        LOG.error("Timesheet not found for ID: {}", timesheetId);
                        return new RuntimeException("Timesheet not found for ID: " + timesheetId);
                    });

            Employee emp = ts.getEmployee();

            if (!emp.getCompanyEmail().equals(loggedInUserEmail)) {
                LOG.error("Email mismatch for update: provided [{}], expected [{}] for timesheet [{}]",
                        loggedInUserEmail, emp.getCompanyEmail(), timesheetId);
                throw new UserNotFoundException("Please login with your company email address");
            }

            if (EnumConstants.WorkRequest.APPROVED.name().equals(ts.getStatus()) ||
                    EnumConstants.WorkRequest.PENDING.name().equals(ts.getStatus())) {
                LOG.warn("Cannot edit timesheet [{}] in status [{}]", timesheetId, ts.getStatus());
                throw new RuntimeException("Timesheet " + timesheetId + " is in " + ts.getStatus() + " state, it cannot be edited");
            }

            LOG.debug("Applying updates to timesheet [{}]: {}", timesheetId, model);
            if (model.getHoursWorked() != null) ts.setHoursWorked(model.getHoursWorked());
            if (model.getWorkDate() != null) ts.setWorkDate(model.getWorkDate());
            if (model.getTaskName() != null) ts.setTaskName(model.getTaskName());
            if (model.getManagerComment() != null) ts.setManagerComment(model.getManagerComment());
            ts.setUpdatedAt(LocalDateTime.now());

            timesheetsToUpdate.add(ts);
        }

        timeSheetRepository.saveAll(timesheetsToUpdate);
        LOG.info("Successfully updated {} timesheets for user: {}", timesheetsToUpdate.size(), loggedInUserEmail);
    }

    /**
     * Approves the given list of timesheets by the logged-in manager.
     *
     * <p>This method:
     * <ul>
     *   <li>Validates that the timesheets belong to employees reporting to the logged-in manager.</li>
     *   <li>Updates the status of valid timesheets to {@code APPROVED}.</li>
     *   <li>Saves all changes in the database.</li>
     *   <li>Sends an approval email notification to the employee.</li>
     * </ul>
     *
     * @param timesheetIds list of timesheet UUIDs to approve
     * @param loggedInUser email of the logged-in manager
     * @throws RuntimeException if any timesheet does not belong to the manager
     */
    @Override
    public void approveByManager(List<UUID> timesheetIds, String loggedInUser) {
        LOG.info("Manager [{}] attempting to approve timesheets: {}", loggedInUser, timesheetIds);

        Employee manager = employeeRepository.getEmployeeByEmail(loggedInUser);
        List<TimeSheet> timesheets = timeSheetRepository.findAllById(timesheetIds);

        for (TimeSheet timeSheet : timesheets) {
            UUID reportingManagerId = timeSheet.getEmployee().getReportingManager().getEmployeeId();

            if (manager.getEmployeeId().equals(reportingManagerId)) {
                timeSheet.setStatus(EnumConstants.WorkRequest.APPROVED.name());
                LOG.debug("Timesheet [{}] approved by manager [{}]", timeSheet.getTimesheetId(), loggedInUser);
            } else {
                LOG.error("Timesheet [{}] does not belong to manager [{}]", timeSheet.getTimesheetId(), loggedInUser);
                throw new RuntimeException("Timesheet not related to that manager");
            }
        }

        List<TimeSheet> updatedSheets = timeSheetRepository.saveAll(timesheets);
        LOG.info("Saved {} approved timesheets by manager [{}]", updatedSheets.size(), loggedInUser);

        String approvalMessage = "<h2 style='color:red;'>=== This is a System Generated Email ===</h2>" +
                "<h3>Your Timesheet Details:</h3>" +
                "<table border='1' cellpadding='5' cellspacing='0'>" +
                "<thead>" +
                "<tr>" +
                "<th>Timesheet ID</th>" +
                "<th>Status</th>" +
                "<th>Work Date</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                updatedSheets.stream()
                        .map(ts -> "<tr>" +
                                "<td>" + ts.getTimesheetId() + "</td>" +
                                "<td>" + ts.getStatus() + "</td>" +
                                "<td>" + ts.getWorkDate() + "</td>" +
                                "</tr>")
                        .collect(Collectors.joining()) +
                "</tbody>" +
                "</table>" +
                "<br><br>Thank you.";

        // Send email to the employee (assuming all belong to the same employee)
        updatedSheets.stream()
                .findFirst()
                .ifPresent(first -> {
                    String email = first.getEmployee().getCompanyEmail();
                    mailService.sendMail(email, approvalMessage);
                    LOG.info("Approval email sent to [{}]", email);
                });
    }

    /**
     * Rejects the given list of timesheets by the logged-in manager.
     *
     * <p>This method:
     * <ul>
     *   <li>Validates that the timesheets belong to employees reporting to the logged-in manager.</li>
     *   <li>Updates the status of valid timesheets to {@code REJECTED}.</li>
     *   <li>Saves all changes in the database.</li>
     *   <li>Sends a rejection email notification to the employee.</li>
     * </ul>
     *
     * @param timesheetIds list of timesheet UUIDs to reject
     * @param loggedInUser email of the logged-in manager
     * @throws RuntimeException if any timesheet does not belong to the manager
     */
    @Override
    public void rejectByManager(List<UUID> timesheetIds, String loggedInUser) {
        LOG.info("Manager [{}] attempting to reject timesheets: {}", loggedInUser, timesheetIds);

        Employee manager = employeeRepository.getEmployeeByEmail(loggedInUser);
        List<TimeSheet> timesheets = timeSheetRepository.findAllById(timesheetIds);

        for (TimeSheet timeSheet : timesheets) {
            if (manager.getEmployeeId().equals(timeSheet.getEmployee().getReportingManager().getEmployeeId())) {
                timeSheet.setStatus(EnumConstants.WorkRequest.REJECTED.name());
                timeSheet.setUpdatedAt(LocalDateTime.now());
                LOG.debug("Timesheet [{}] rejected by manager [{}]", timeSheet.getTimesheetId(), loggedInUser);
            } else {
                LOG.error("Timesheet [{}] does not belong to manager [{}]", timeSheet.getTimesheetId(), loggedInUser);
                throw new RuntimeException("Timesheet not related to that manager");
            }
        }

        List<TimeSheet> updatedSheets = timeSheetRepository.saveAll(timesheets);
        LOG.info("Saved {} rejected timesheets by manager [{}]", updatedSheets.size(), loggedInUser);

        String rejectionMessage = "<h2 style='color:red;'>=== This is a System Generated Email ===</h2>" +
                "<h3>Your Timesheet Details:</h3>" +
                "<table border='1' cellpadding='5' cellspacing='0'>" +
                "<thead>" +
                "<tr>" +
                "<th>Timesheet ID</th>" +
                "<th>Status</th>" +
                "<th>Work Date</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                updatedSheets.stream()
                        .map(ts -> "<tr>" +
                                "<td>" + ts.getTimesheetId() + "</td>" +
                                "<td>" + ts.getStatus() + "</td>" +
                                "<td>" + ts.getWorkDate() + "</td>" +
                                "</tr>")
                        .collect(Collectors.joining()) +
                "</tbody>" +
                "</table>" +
                "<br><br>Thank you.";
        updatedSheets.stream()
                .findFirst()
                .ifPresent(first -> {
                    String email = first.getEmployee().getCompanyEmail();
                    mailService.sendMail(email, rejectionMessage);
                    LOG.info("Rejection email sent to [{}]", email);
                });
    }

    /**
     * Deletes a timesheet entry for the logged-in employee.
     *
     * @param timesheetId       The ID of the timesheet to delete
     * @param loggedInUserEmail Logged-in employee's email for verification
     * @throws UserNotFoundException if the email does not match company email
     */
    @Override
    public void deleteTimeSheet(UUID timesheetId, String loggedInUserEmail) {
        LOG.info("Deleting timesheet [{}] for user: {}", timesheetId, loggedInUserEmail);

        TimeSheet tsUpdated = timeSheetRepository.findById(timesheetId)
                .orElseThrow(() -> {
                    LOG.error("Timesheet not found for ID: {}", timesheetId);
                    return new RuntimeException("Timesheet not found");
                });

        Employee emp = tsUpdated.getEmployee();
        if (!emp.getCompanyEmail().equals(loggedInUserEmail)) {
            LOG.error("Unauthorized delete attempt by: {}", loggedInUserEmail);
            throw new UserNotFoundException("Please login with your company email address");
        }

        if (emp.getUser().getRole().equals(EnumConstants.Role.EMPLOYEE)) {
            timeSheetRepository.deleteById(timesheetId);
            LOG.info("Timesheet [{}] deleted successfully by employee [{}]", timesheetId, emp.getEmployeeId());
        } else {
            LOG.warn("Delete operation skipped: Role [{}] is not authorized", emp.getUser().getRole());
        }
    }

    @Override
    public void requestToManager(List<UUID> timesheetIds, String loggedInEmail) {

        List<TimeSheet> listOfTs = timesheetIds.stream().map(id ->
                timeSheetRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Timesheet Not found with Id: " + id))
        ).toList();

        listOfTs.forEach(ts -> ts.setStatus(EnumConstants.WorkRequest.PENDING.name()));
        listOfTs.forEach(ts -> ts.setUpdatedAt(LocalDateTime.now()));
        List<TimeSheet> updatedSheets = timeSheetRepository.saveAll(listOfTs);

        Employee employee = employeeRepository.getEmployeeByEmail(loggedInEmail);
        Employee manager = employeeRepository.findByIdWithManager(employee.getEmployeeId())
                .map(mngr -> {
                    if (mngr.getReportingManager() == null) {
                        throw new UserNotFoundException("Manager not found for employee with id: " + mngr.getEmployeeId());
                    }
                    return mngr.getReportingManager();
                })
                .orElseThrow(() -> new UserNotFoundException("Employee not found with ID: " + employee.getEmployeeId()));

        String approvalMessage = "<h2 style='color:red;'>=== This is a System Generated Email ===</h2>" +
                "<h3>Your Timesheet Details:</h3>" +
                "<table border='1' cellpadding='5' cellspacing='0'>" +
                "<thead>" +
                "<tr>" +
                "<th>Timesheet ID</th>" +
                "<th>Status</th>" +
                "<th>Work Date</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                updatedSheets.stream()
                        .map(ts -> "<tr>" +
                                "<td>" + ts.getTimesheetId() + "</td>" +
                                "<td>" + ts.getStatus() + "</td>" +
                                "<td>" + ts.getWorkDate() + "</td>" +
                                "</tr>")
                        .collect(Collectors.joining()) +
                "</tbody>" +
                "</table>" +
                "<br><br>Thank you." +
                "<h3>Please approve TimeSheets for a past week,having employee_name: " +
                employee.getFirstName() + " " + employee.getLastName() +
                ".</h3>\n <h4>follow link below:<h/4>\n"
                + "<a href=\"https://192.168.1.19:8081/web/api/v1/employee/manager/timesheet\">Approve TimeSheets</a>";

        List<NotificationDTO> notificationDTOS = notificationService.sendNotificationToManager(
                manager.getUser(),
                "Please approve TimeSheets for a past week,having employee_name: " + employee.getFirstName() + " " + employee.getLastName(),
                timesheetIds);
        mailService.sendMail(manager.getCompanyEmail(), approvalMessage);

    }
}
