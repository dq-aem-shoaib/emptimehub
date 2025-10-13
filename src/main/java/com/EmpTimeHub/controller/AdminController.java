package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.ClientDTO;
import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.model.ClientModel;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.service.AdminService;
import com.EmpTimeHub.service.ClientService;
import com.EmpTimeHub.service.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.EmpTimeHub.constants.EndpointConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {


    private final EmployeeService employeeService;
    private final AdminService adminService;
    private final ClientService clientService;

    @PostMapping(ADD_EMPLOYEE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<Employee>> addEmployee(
            @ModelAttribute EmployeeModel employeeModel){

        Employee employee = employeeService.addEmployee(employeeModel);


        WebResponseDTO<Employee> response = WebResponseDTO.<Employee>builder()
                .flag(true)
                .message("Employee added successfully")
                .status(HttpStatus.CREATED.value())
                .response(employee)
                .build();

        return ResponseEntity.status(201).body(response);

    }
    @PostMapping(ADD_CLIENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<Client>> addClient(
            @ModelAttribute ClientModel clientModel){

        Client client = clientService.addClient(clientModel);

        WebResponseDTO<Client> response = WebResponseDTO.<Client>builder()
                .flag(true)
                .message("client added successfully")
                .status(HttpStatus.CREATED.value())
                .response(client)
                .build();

        return ResponseEntity.status(201).body(response);

    }

    @PutMapping(ADMIN_UPDATE_EMP)
    public ResponseEntity<WebResponseDTO<String>> updateEmployee(
            @ModelAttribute EmployeeModel employeeModel,
            @PathVariable UUID empId){

        employeeService.updateEmployeeById(empId ,employeeModel);

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Employee Updated successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.status(200).body(response);

    }

    @GetMapping(ADMIN_GET_EMP)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<EmployeeDTO>> getEmployeeById(
            @PathVariable UUID empId){

        Employee employee = employeeService.getEmployeeById(empId);
        EmployeeDTO employeeDTO = new EmployeeDTO();

        BeanUtils.copyProperties(employee,employeeDTO);
        employeeDTO.setClientId(employee.getClient().getClientId());
        employeeDTO.setClientName(employee.getClient().getCompanyName());

        WebResponseDTO<EmployeeDTO> response = WebResponseDTO.<EmployeeDTO>builder()
                .flag(true)
                .message("Employee fetched successfully")
                .status(HttpStatus.OK.value())
                .response(employeeDTO)
                .build();

        return ResponseEntity.status(200).body(response);

    }


    @GetMapping(ADMIN_GET_ALL_EMP)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<List<EmployeeDTO>>> getAllEmployee(){
        List<EmployeeDTO> allEmployee = employeeService.getAllEmployee();

        WebResponseDTO<List<EmployeeDTO>> response = WebResponseDTO.<List<EmployeeDTO>>builder()
                .flag(true)
                .message("Employee fetched successfully")
                .status(HttpStatus.OK.value())
                .response(allEmployee)
                .build();

        return ResponseEntity.status(200).body(response);
    }


    @GetMapping(ADMIN_GET_CLIENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<ClientDTO>> getClientById(
            @PathVariable UUID clientId){

        ClientDTO clientDTO = clientService.getClientById(clientId);


        WebResponseDTO<ClientDTO> response = WebResponseDTO.<ClientDTO>builder()
                .flag(true)
                .message("Client fetched successfully")
                .status(HttpStatus.OK.value())
                .response(clientDTO)
                .build();

        return ResponseEntity.status(200).body(response);

    }


    @GetMapping(ADMIN_GET_ALL_CLIENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<List<ClientDTO>>> getAllClient(){
        List<ClientDTO> allClient = clientService.getAllClient();

        WebResponseDTO<List<ClientDTO>> response = WebResponseDTO.<List<ClientDTO>>builder()
                .flag(true)
                .message("Clients fetched successfully")
                .status(HttpStatus.OK.value())
                .response(allClient)
                .build();

        return ResponseEntity.status(200).body(response);
    }


    @PutMapping(ADMIN_UPDATE_CLIENT)
    public ResponseEntity<WebResponseDTO<String>> updateClient(
            @ModelAttribute ClientModel clientModel,
            @PathVariable UUID clientId){

        clientService.updateClientById(clientId ,clientModel);

        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Client Updated successfully")
                .status(HttpStatus.CREATED.value())
                .response(null)
                .build();

        return ResponseEntity.status(204).body(response);

    }

    @DeleteMapping(ADMIN_DELETE_EMP)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> deleteEmployeeById(
            @PathVariable UUID empId){

         employeeService.removeEmployeeById(empId);


        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Employee Deleted successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.status(200).body(response);

    }

    @DeleteMapping(ADMIN_DELETE_CLIENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WebResponseDTO<String>> deleteClientById(
            @PathVariable UUID clientId){

        clientService.removeClientById(clientId);


        WebResponseDTO<String> response = WebResponseDTO.<String>builder()
                .flag(true)
                .message("Client Deleted successfully")
                .status(HttpStatus.OK.value())
                .response(null)
                .build();

        return ResponseEntity.status(200).body(response);

    }

    @PatchMapping(ADMIN_UNASSIGN_CLIENT)
    public ResponseEntity<WebResponseDTO<String>> unassignEmployeeFromClient(@PathVariable UUID empId) {
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
     * <p>
     * Accessible by users with roles 'ADMIN' or 'EMPLOYEE'.
     *
     * @return ResponseEntity containing a WebResponseDTO with the list of admin names,
     *         a success flag, status code, and message.
     */
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @GetMapping(ADMIN_NAMES)
    public ResponseEntity<WebResponseDTO<List<String>>> getAllAdminNames() {
        log.info("Request received to fetch all admin names");

        List<String> adminNames = adminService.getAllAdminNames();
        log.debug("Fetched {} admin names: {}", adminNames.size(), adminNames);

        WebResponseDTO<List<String>> response = WebResponseDTO.<List<String>>builder()
                .flag(true)
                .message("Admin names fetched successfully")
                .status(200)
                .response(adminNames)
                .build();

        log.info("Returning response with {} admin names", adminNames.size());
        return ResponseEntity.ok(response);
    }

}
