package com.EmpTimeHub.controller;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.TimeSheet;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.model.TimeSheetModel;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.AddressService;
import com.EmpTimeHub.service.EmployeeService;
import com.EmpTimeHub.service.TimeSheetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.EmpTimeHub.constants.EndpointConstants.*;

/**
 * Controller class that handles all employee-related operations,
 * including timesheet management, employee profile updates,
 * and retrieval operations.
 *
 * <p>This controller provides REST endpoints accessible to users
 * with roles such as <b>EMPLOYEE</b> and <b>ADMIN</b> depending on
 * the specific functionality.</p>
 */
@Slf4j
@RestController
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final TimeSheetService timeSheetService;
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Registers a new timesheet entry for the authenticated employee.
     *
     * @param tsModel the {@link TimeSheetModel} containing timesheet data.
     * @param userDetails the authenticated user's details.
     * @return a response containing the created {@link TimeSheet}.
     */
    @PostMapping(EMPLOYEE_TIMESHEET_REGISTER)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<TimeSheet>> registerTimeSheet(
            @RequestBody TimeSheetModel tsModel,
            @AuthenticationPrincipal UserDetails userDetails) {

        TimeSheet timeSheet = timeSheetService.createTimeSheet(tsModel, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Time Sheet is created Successfully", HttpStatus.CREATED.value(), timeSheet));
    }

    /**
     * Retrieves details of a specific timesheet by its ID.
     *
     * @param userDetails the authenticated user's details.
     * @param timesheetId the UUID of the timesheet to be retrieved.
     * @return a response containing the {@link TimeSheetResponseDto}.
     */
    @GetMapping(EMPLOYEE_TIMESHEET_VIEW)
    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<TimeSheetResponseDto>> getTimeSheetDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID timesheetId) {

        TimeSheetResponseDto oneTimeSheet = timeSheetService.getTimeSheetById(timesheetId, userDetails.getUsername());
        WebResponseDTO<TimeSheetResponseDto> dto = new WebResponseDTO<>(
                true, "Getting timesheet by ID", HttpStatus.OK.value(), oneTimeSheet);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Retrieves all timesheets for the authenticated employee or all employees (if ADMIN),
     * with pagination and optional date filtering.
     *
     * @param page       the page number (default: 0).
     * @param size       the number of records per page (default: 5).
     * @param direction  sort direction ("asc" or "desc").
     * @param orderBy    field to order by (default: "createdAt").
     * @param startDate  optional filter for start date.
     * @param endDate    optional filter for end date.
     * @param userDetails the authenticated user's details.
     * @return a paginated list of {@link TimeSheetResponseDto}.
     */
    @GetMapping(VIEW_ALL_TIMESHEET)
    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<List<TimeSheetResponseDto>>> getTimeSheetDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails) {

        Page<TimeSheetResponseDto> allTimeSheets = timeSheetService.getAllTimeSheets(
                page, size, direction, orderBy, userDetails.getUsername(), startDate, endDate);
        List<TimeSheetResponseDto> content = allTimeSheets.getContent();
        long totalElements = allTimeSheets.getTotalElements();

        WebResponseDTO<List<TimeSheetResponseDto>> dto = new WebResponseDTO<>(
                true, "Getting all timesheets", HttpStatus.OK.value(), content, totalElements);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Updates a specific timesheet entry for the authenticated employee.
     *
     * @param timesheetId the UUID of the timesheet to update.
     * @param sheetModel  the {@link TimeSheetModel} containing updated data.
     * @param userDetails the authenticated user's details.
     * @return a response indicating the success of the update operation.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping(EMPLOYEE_TIMESHEET_UPDATE)
    public ResponseEntity<WebResponseDTO<String>> getTimeSheetUpdate(
            @RequestParam UUID timesheetId,
            @RequestBody TimeSheetModel sheetModel,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("updating time sheet...{}",sheetModel);
        timeSheetService.updateTimeSheet(timesheetId, sheetModel, userDetails.getUsername());
        WebResponseDTO<String> dto = new WebResponseDTO<>(
                true, "Timesheet updated successfully", HttpStatus.OK.value());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Deletes a specific timesheet entry for the authenticated employee.
     *
     * @param timesheetId the UUID of the timesheet to delete.
     * @param userDetails the authenticated user's details.
     * @return a response indicating the success of the delete operation.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping(EMPLOYEE_TIMESHEET_DELETE)
    public ResponseEntity<WebResponseDTO<String>> getTimeSheetDelete(
            @RequestParam UUID timesheetId,
            @AuthenticationPrincipal UserDetails userDetails) {

        timeSheetService.deleteTimeSheet(timesheetId, userDetails.getUsername());
        WebResponseDTO<String> dto = new WebResponseDTO<>(
                true, "Timesheet deleted successfully", HttpStatus.OK.value());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Updates the profile details of the authenticated employee.
     *
     * @param employeeModel the updated employee information.
     * @param userDetails   the authenticated user's details.
     * @return a response confirming successful update.
     */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping(EMPLOYEE_UPDATE)
    public ResponseEntity<WebResponseDTO<String>> updateEmployee(
            @RequestBody EmployeeModel employeeModel,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByCompanyEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Employee not found for user ID: " + user.getUserId()));

        employeeService.updateEmployeeById(employee.getEmployeeId(), employeeModel);

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Employee updated successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the employee details of the authenticated user.
     *
     * @param userDetails the authenticated user's details.
     * @return a response containing the {@link EmployeeDTO}.
     */
    @GetMapping(EMPLOYEE_VIEW)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<EmployeeDTO>> getEmployeeById(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByCompanyEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Employee not found for user ID: " + user.getUserId()));

        EmployeeDTO employeeDTO = employeeService.getEmployeeById(employee.getEmployeeId());

        WebResponseDTO<EmployeeDTO> response = WebResponseDTO.<EmployeeDTO>builder()
                .flag(true)
                .message("Employee fetched successfully")
                .status(HttpStatus.OK.value())
                .response(employeeDTO)
                .build();

        return ResponseEntity.status(200).body(response);
    }

    /**
     * Retrieves a list of employees filtered by their designation.
     *
     * @param designation the {@link EnumConstants.Designation} to filter employees by.
     * @return a response containing the list of {@link EmployeeDTO}.
     */
    @GetMapping(GET_BY_DESIGNATION)
    public ResponseEntity<WebResponseDTO<List<EmployeeDTO>>> getEmployeesByDesignation(
            @PathVariable EnumConstants.Designation designation) {

        List<EmployeeDTO> employees = employeeService.findByDesignation(designation);

        WebResponseDTO<List<EmployeeDTO>> response = WebResponseDTO.<List<EmployeeDTO>>builder()
                .flag(true)
                .message(employees.isEmpty()
                        ? "No employees found for designation: " + designation
                        : "Employees fetched successfully")
                .status(HttpStatus.OK.value())
                .response(employees)
                .build();

        return ResponseEntity.status(200).body(response);
    }
}
