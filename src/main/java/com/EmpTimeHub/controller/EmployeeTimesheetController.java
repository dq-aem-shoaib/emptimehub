package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.TimeSheet;
import com.EmpTimeHub.model.TimeSheetModel;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.TimeSheetRepository;
import com.EmpTimeHub.service.MailService;
import com.EmpTimeHub.service.NotificationService;
import com.EmpTimeHub.service.TimeSheetService;
import lombok.AllArgsConstructor;
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
import static com.EmpTimeHub.constants.EndpointConstants.EMPLOYEE_TIMESHEET_DELETE;
import static com.EmpTimeHub.constants.EndpointConstants.EMPLOYEE_TIMESHEET_UPDATE;

@RestController
@AllArgsConstructor
public class EmployeeTimesheetController {

    private final TimeSheetService timeSheetService;
    private final MailService mailService;
    private final TimeSheetRepository timeSheetRepository;

    @PostMapping(EMPLOYEE_TIMESHEET_REGISTER)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<?>> registerTimeSheet(
            @RequestBody List<TimeSheetModel> tsModel,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        List<TimeSheet> timeSheet = timeSheetService.createTimeSheet(tsModel, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Time Sheet is created Successfully", HttpStatus.CREATED.value(), timeSheet));
    }


    @GetMapping(EMPLOYEE_TIMESHEET_VIEW)
    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('ADMIN') OR hasRole('MANAGER')")
    public ResponseEntity<WebResponseDTO<TimeSheetResponseDto>> getTimeSheetDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID timesheetId
    ) {

        TimeSheetResponseDto oneTimeSheet = timeSheetService.getTimeSheetById(timesheetId, userDetails.getUsername());

        WebResponseDTO<TimeSheetResponseDto> dto = new WebResponseDTO<>(
                true, "getting timesheet by ID", HttpStatus.OK.value(), oneTimeSheet);
        return new ResponseEntity<WebResponseDTO<TimeSheetResponseDto>>(dto, HttpStatus.OK);
    }

    @GetMapping(VIEW_ALL_TIMESHEET)
    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('ADMIN') OR hasRole('MANAGER')")
    public ResponseEntity<WebResponseDTO<List<TimeSheetResponseDto>>> getTimeSheetDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        Page<TimeSheetResponseDto> allTimeSheets = timeSheetService.getAllTimeSheets(page, size, direction, orderBy,
                userDetails.getUsername(), startDate, endDate);
        List<TimeSheetResponseDto> content = allTimeSheets.getContent();
        long totalElements = allTimeSheets.getTotalElements();

        WebResponseDTO<List<TimeSheetResponseDto>> dto = new WebResponseDTO<>(
                true, "getting all timesheets", HttpStatus.OK.value(), content, totalElements);

        return new ResponseEntity<WebResponseDTO<List<TimeSheetResponseDto>>>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping(EMPLOYEE_TIMESHEET_UPDATE)
    public ResponseEntity<WebResponseDTO<String>> updateTimeSheetUpdate(
            @RequestBody List<TimeSheetModel> sheetModels,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        timeSheetService.updateTimeSheet(sheetModels, userDetails.getUsername());
        WebResponseDTO<String> dto = new WebResponseDTO<>(
                true, "timesheet updated successfully", HttpStatus.OK.value());

        return new ResponseEntity<WebResponseDTO<String>>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping(EMPLOYEE_TIMESHEET_DELETE)
    public ResponseEntity<WebResponseDTO<String>> getTimeSheetDelete(
            @RequestParam UUID timesheetId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        timeSheetService.deleteTimeSheet(timesheetId, userDetails.getUsername());
        WebResponseDTO<String> dto = new WebResponseDTO<>(
                true, "timesheet Deleted successfully", HttpStatus.OK.value());
        return new ResponseEntity<WebResponseDTO<String>>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping(EMPLOYEE_TIMESHEET_MANAGER_REQUEST)
    public ResponseEntity<WebResponseDTO<String>> requestSendToManager(
            @RequestParam List<UUID> timesheetIds,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        timeSheetService.requestToManager(timesheetIds, userDetails.getUsername());
        return new ResponseEntity<WebResponseDTO<String>>(
                new WebResponseDTO<>(true, "Successfully send to Manager"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping(EMPLOYEE_TIMESHEET_APPROVE_BY_MANAGER)
    public ResponseEntity<WebResponseDTO<String>> getApproveByManager(
            @RequestParam List<UUID> timesheetsIds,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();

        timeSheetService.approveByManager(timesheetsIds, email);
        return new ResponseEntity<WebResponseDTO<String>>(
                new WebResponseDTO<>(true, "Successfully notify by Manager"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping(EMPLOYEE_TIMESHEET_REJECT_BY_MANAGER)
    public ResponseEntity<WebResponseDTO<String>> getRejectByManager(
            @RequestParam List<UUID> timesheetsIds,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        timeSheetService.rejectByManager(timesheetsIds, userDetails.getUsername());
        return new ResponseEntity<WebResponseDTO<String>>(
                new WebResponseDTO<>(true, "Successfully notify by Manager"), HttpStatus.OK);
    }


}
