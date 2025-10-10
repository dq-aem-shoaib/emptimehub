package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.entity.TimeSheet;
import com.EmpTimeHub.model.TimeSheetModel;
import com.EmpTimeHub.service.TimeSheetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.EmpTimeHub.constants.EndpointConstants.*;

@RestController
@AllArgsConstructor
public class EmployeeController {

    private TimeSheetService timeSheetService;

    @PostMapping(EMPLOYEE_TIMESHEET_REGISTER)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<TimeSheet>> registerTimeSheet(
            @RequestBody TimeSheetModel tsModel,
            @AuthenticationPrincipal UserDetails userDetails
            ){

        TimeSheet timeSheet = timeSheetService.createTimeSheet(tsModel, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(
                true,"Time Sheet is created Successfully", timeSheet));
    }


    @GetMapping(EMPLOYEE_TIMESHEET_VIEW)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<TimeSheetResponseDto>> getTimeSheetDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID timesheetId
    ){

        TimeSheetResponseDto oneTimeSheet = timeSheetService.getTimeSheetById(timesheetId, userDetails.getUsername());

        WebResponseDTO<TimeSheetResponseDto> dto = new WebResponseDTO<>(true,"getting timesheet",oneTimeSheet);
        return new ResponseEntity<WebResponseDTO<TimeSheetResponseDto>>(dto,HttpStatus.OK);
    }

    @GetMapping(VIEW_ALL_TIMESHEET)
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<WebResponseDTO<TimeSheetResponseDto>> getTimeSheetDetails(
            
    ){
        WebResponseDTO<TimeSheetResponseDto> dto = new WebResponseDTO<>(true,"getting timesheet12");

        return   new ResponseEntity<WebResponseDTO<TimeSheetResponseDto>>(dto,HttpStatus.OK);
    }
}
