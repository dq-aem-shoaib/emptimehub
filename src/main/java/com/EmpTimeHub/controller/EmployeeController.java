package com.EmpTimeHub.controller;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.AddressService;
import com.EmpTimeHub.service.EmployeeService;
import com.EmpTimeHub.service.TimeSheetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
     * Deletes a specific address of the authenticated employee.
     *
     * @param userDetails the authenticated user's details.
     * @param addressId   the UUID of the address to delete.
     * @return a {@link WebResponseDTO} confirming deletion.
     */
    @DeleteMapping(EMPLOYEE_ADDRESS_DELETE)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<Void>> deleteEmployeeAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID addressId) {

        // Fetch authenticated user
        User user = userRepository.findByCompanyEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get employee linked to this user
        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Employee not found for user ID: " + user.getUserId()));

        // Perform deletion
        addressService.deleteAddress(employee.getEmployeeId(), addressId);

        // Build success response
        WebResponseDTO<Void> response = WebResponseDTO.<Void>builder()
                .flag(true)
                .message("Address deleted successfully")
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
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

    /**
     * Fetch all employees under the logged-in manager.
     *
     * @param userDetails Authenticated manager's details
     * @return Standardized {@link WebResponseDTO} containing list of {@link EmployeeDTO}
     */
    @GetMapping(MAANAGER_VIEW_EMPLOYEES)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<WebResponseDTO<List<EmployeeDTO>>> getEmployeesUnderManager(
            @AuthenticationPrincipal UserDetails userDetails) {

        //  Validate and fetch manager user
        User user = userRepository.findByCompanyEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("Authenticated user not found: {}", userDetails.getUsername());
                    return new RuntimeException("User not found");
                });

        log.debug("Manager user found: {} (userId: {})", user.getCompanyEmail(), user.getUserId());

        // Get employee DTO list from service
        List<EmployeeDTO> employees = employeeService.getEmployeesUnderManager(user.getUserId());

        //  Build WebResponseDTO here in the controller
        WebResponseDTO<List<EmployeeDTO>> response = WebResponseDTO.<List<EmployeeDTO>>builder()
                .flag(true)
                .message(employees.isEmpty()
                        ? "No employees found under this manager"
                        : "Employees fetched successfully")
                .response(employees)
                .build();

        log.info("Returning {} employees for manager {}", employees.size(), user.getUserId());

        return ResponseEntity.ok(response);
    }
}
