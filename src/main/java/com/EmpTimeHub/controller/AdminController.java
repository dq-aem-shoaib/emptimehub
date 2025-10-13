package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.EmpTimeHub.constants.EndpointConstants.ADMIN_NAMES;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService  adminService;

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
