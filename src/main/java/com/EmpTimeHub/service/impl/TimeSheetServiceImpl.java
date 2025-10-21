package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Mohammad Shoaib
 * @since 2025-10-13
 *
 *
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

    /**
     * Creates a new timesheet entry for the logged-in employee.
     *
     * @param timeSheetModels  TimeSheetModel containing timesheet details
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
                        "be within start["+projectStart+"] and end date["+projectEnd+"] range!");
            }

        LOG.debug("Validating work date [{}] between project start [{}] and end [{}]",
                currentDate, projectStart, projectEnd);

            TimeSheet sheet = TimeSheet.builder()
                    .employee(emp)
                    .client(client)
                    .hoursWorked(timeSheetModel.getHoursWorked())
                    .workDate(timeSheetModel.getWorkDate())
                    .taskName(timeSheetModel.getTaskName())
                    .taskDescription(timeSheetModel.getTaskDescription())
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
     * @param timesheetId        Timesheet unique identifier
     * @param loggedInUserEmail  Logged-in employee's company email
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
                .taskDescription(oneTimeSheet.getTaskDescription())
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

        if (!role.equals(EnumConstants.Role.EMPLOYEE.name()) && !role.equals(EnumConstants.Role.ADMIN.name())) {
            LOG.error("Unauthorized role: {}", role);
            throw new UserRoleNotFoundException("Only 'EMPLOYEE' and 'ADMIN' roles have access");
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
                            .taskDescription(dto.getTaskDescription())
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
     * @param timesheetId       The timesheet ID to update
     * @param updatedSheet      Updated timesheet details
     * @param loggedInUserEmail Logged-in employee email for validation
     * @throws UserNotFoundException if the email does not match the employee
     */
    @Override
    public void updateTimeSheet(UUID timesheetId, TimeSheetModel updatedSheet, String loggedInUserEmail) {
        LOG.info("Updating timesheet [{}] for user: {}", timesheetId, loggedInUserEmail);

        TimeSheet tsUpdated = timeSheetRepository.findById(timesheetId)
                .orElseThrow(() -> {
                    LOG.error("Timesheet not found for ID: {}", timesheetId);
                    return new RuntimeException("Timesheet not found");
                });

        Employee emp = tsUpdated.getEmployee();
        if (!emp.getCompanyEmail().equals(loggedInUserEmail)) {
            LOG.error("Email mismatch for update: provided [{}], expected [{}]",
                    loggedInUserEmail, emp.getCompanyEmail());
            throw new UserNotFoundException("Please login with your company email address");
        }

        if(tsUpdated.getStatus().equals(EnumConstants.WorkRequest.APPROVED.name())){
            throw new RuntimeException("You can not edit this");
        }

        LOG.debug("Applying field updates for timesheet: {}", timesheetId);
        if (updatedSheet.getHoursWorked() != null)
            tsUpdated.setHoursWorked(updatedSheet.getHoursWorked());
        if (updatedSheet.getWorkDate() != null)
            tsUpdated.setWorkDate(updatedSheet.getWorkDate());
        if (updatedSheet.getTaskName() != null)
            tsUpdated.setTaskName(updatedSheet.getTaskName());
        if (updatedSheet.getTaskDescription() != null)
            tsUpdated.setTaskDescription(updatedSheet.getTaskDescription());
        tsUpdated.setUpdatedAt(LocalDateTime.now());

        timeSheetRepository.save(tsUpdated);
        LOG.info("Timesheet [{}] updated successfully", timesheetId);
    }

    @Override
    public TimeSheet updateStatus(UUID timesheetId, String status) {
        LOG.warn("updateStatus() not yet implemented");
        return null;
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
    public void requestToManager(String loggedInEmail) {



    }
}
