package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.ClientDTO;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.model.ClientModel;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.service.AddressService;
import com.EmpTimeHub.service.AdminService;
import com.EmpTimeHub.service.ClientService;
import com.EmpTimeHub.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.EmpTimeHub.constants.EndpointConstants.*;

/**
 * Controller responsible for handling all administrative operations
 * related to employees, clients, and admin management.
 *
 * <p>Accessible primarily to users with the {@code ADMIN} role.
 * Some endpoints may allow access to users with {@code EMPLOYEE} roles
 * based on authorization configuration.
 *
 * <p>Provides functionality for:
 * <ul>
 *   <li>Adding, updating, fetching, and deleting employees and clients</li>
 *   <li>Unassigning employees from clients</li>
 *   <li>Fetching admin names</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final EmployeeService employeeService;
    private final AdminService adminService;
    private final ClientService clientService;
    private final AddressService addressService;

    /**
     * Adds a new employee to the system.
     *
     * @param employeeModel The employee details to be added.
     * @return ResponseEntity containing the added employee details
     *         wrapped in a {@link WebResponseDTO}.
     */
    @PostMapping(ADD_EMPLOYEE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<EmployeeDTO>> addEmployee(
            @RequestBody EmployeeModel employeeModel) {

        EmployeeDTO employeeDto = employeeService.addEmployee(employeeModel);

        WebResponseDTO<EmployeeDTO> response = WebResponseDTO.<EmployeeDTO>builder()
                .flag(true)
                .message("Employee added successfully")
                .status(HttpStatus.CREATED.value())
                .response(employeeDto)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Adds a new client to the system.
     *
     * @param clientModel The client details to be added.
     * @return ResponseEntity containing the added client entity
     *         wrapped in a {@link WebResponseDTO}.
     */
    @PostMapping(ADD_CLIENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<Client>> addClient(
            @RequestBody ClientModel clientModel) {

        Client client = clientService.addClient(clientModel);

        WebResponseDTO<Client> response = WebResponseDTO.<Client>builder()
                .flag(true)
                .message("Client added successfully")
                .status(HttpStatus.CREATED.value())
                .response(client)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing employee’s information.
     *
     * @param employeeModel The updated employee details.
     * @param empId         The UUID of the employee to update.
     * @return ResponseEntity with a success message wrapped in {@link WebResponseDTO}.
     */
    @PutMapping(ADMIN_UPDATE_EMP)
    public ResponseEntity<WebResponseDTO<String>> updateEmployee(
            @RequestBody EmployeeModel employeeModel,
            @PathVariable UUID empId) {

        employeeService.updateEmployeeById(empId, employeeModel);

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Employee updated successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches details of a specific employee by ID.
     *
     * @param empId The UUID of the employee to fetch.
     * @return ResponseEntity containing the employee details wrapped in {@link WebResponseDTO}.
     */
    @GetMapping(ADMIN_GET_EMP)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<EmployeeDTO>> getEmployeeById(
            @PathVariable UUID empId) {

        EmployeeDTO employeeDTO = employeeService.getEmployeeById(empId);

        WebResponseDTO<EmployeeDTO> response = WebResponseDTO.<EmployeeDTO>builder()
                .flag(true)
                .message("Employee fetched successfully")
                .status(HttpStatus.OK.value())
                .response(employeeDTO)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches all employees in the system.
     *
     * @return ResponseEntity containing a list of employee DTOs wrapped in {@link WebResponseDTO}.
     */
    @GetMapping(ADMIN_GET_ALL_EMP)
    @PreAuthorize("hasRole('ADMIN') OR hasRole('MANAGER')")
    public ResponseEntity<WebResponseDTO<List<EmployeeDTO>>> getAllEmployee() {

        List<EmployeeDTO> allEmployee = employeeService.getAllEmployee();

        WebResponseDTO<List<EmployeeDTO>> response = WebResponseDTO.<List<EmployeeDTO>>builder()
                .flag(true)
                .message("Employees fetched successfully")
                .status(HttpStatus.OK.value())
                .response(allEmployee)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches details of a specific client by ID.
     *
     * @param clientId The UUID of the client to fetch.
     * @return ResponseEntity containing the client details wrapped in {@link WebResponseDTO}.
     */
    @GetMapping(ADMIN_GET_CLIENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<ClientDTO>> getClientById(
            @PathVariable UUID clientId) {

        ClientDTO clientDTO = clientService.getClientById(clientId);

        WebResponseDTO<ClientDTO> response = WebResponseDTO.<ClientDTO>builder()
                .flag(true)
                .message("Client fetched successfully")
                .status(HttpStatus.OK.value())
                .response(clientDTO)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches all clients in the system.
     *
     * @return ResponseEntity containing a list of client DTOs wrapped in {@link WebResponseDTO}.
     */
    @GetMapping(ADMIN_GET_ALL_CLIENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<List<ClientDTO>>> getAllClient() {

        List<ClientDTO> allClient = clientService.getAllClient();

        WebResponseDTO<List<ClientDTO>> response = WebResponseDTO.<List<ClientDTO>>builder()
                .flag(true)
                .message("Clients fetched successfully")
                .status(HttpStatus.OK.value())
                .response(allClient)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Updates details of an existing client.
     *
     * @param clientModel The updated client data.
     * @param clientId    The UUID of the client to update.
     * @return ResponseEntity with a success message wrapped in {@link WebResponseDTO}.
     */
    @PutMapping(ADMIN_UPDATE_CLIENT)
    public ResponseEntity<WebResponseDTO<String>> updateClient(
            @ModelAttribute ClientModel clientModel,
            @PathVariable UUID clientId) {

        clientService.updateClientById(clientId, clientModel);

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Client updated successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an employee by ID.
     *
     * @param empId The UUID of the employee to delete.
     * @return ResponseEntity with a success message wrapped in {@link WebResponseDTO}.
     */
    @DeleteMapping(ADMIN_DELETE_EMP)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> deleteEmployeeById(
            @PathVariable UUID empId) {

        employeeService.removeEmployeeById(empId);

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Employee deleted successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a client by ID.
     *
     * @param clientId The UUID of the client to delete.
     * @return ResponseEntity with a success message wrapped in {@link WebResponseDTO}.
     */
    @DeleteMapping(ADMIN_DELETE_CLIENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> deleteClientById(
            @PathVariable UUID clientId) {

        clientService.removeClientById(clientId);

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Client deleted successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Unassigns an employee from their associated client.
     *
     * @param empId The UUID of the employee to unassign.
     * @return ResponseEntity with a success message wrapped in {@link WebResponseDTO}.
     */
    @PatchMapping(ADMIN_UNASSIGN_CLIENT)
    public ResponseEntity<WebResponseDTO<String>> unassignEmployeeFromClient(
            @PathVariable UUID empId) {

        employeeService.unassignEmployeeFromClient(empId);

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Employee unassigned from client successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches the names of all admins in the system.
     *
     * <p>Accessible by users with roles {@code ADMIN} or {@code EMPLOYEE}.
     *
     * @return ResponseEntity containing a list of admin names wrapped in {@link WebResponseDTO}.
     */
    @PreAuthorize("hasRole('ADMIN') OR hasRole('EMPLOYEE')")
    @GetMapping(ADMIN_NAMES)
    public ResponseEntity<WebResponseDTO<List<String>>> getAllAdminNames() {
        log.info("Request received to fetch all admin names");

        List<String> adminNames = adminService.getAllAdminNames();
        log.debug("Fetched {} admin names: {}", adminNames.size(), adminNames);

        WebResponseDTO<List<String>> response = WebResponseDTO.<List<String>>builder()
                .flag(true)
                .message("Admin names fetched successfully")
                .status(HttpStatus.OK.value())
                .response(adminNames)
                .build();

        log.info("Returning response with {} admin names", adminNames.size());
        return ResponseEntity.ok(response);
    }

}
