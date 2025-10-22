package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.HolidayCalendarDTO;
import com.EmpTimeHub.dto.HolidaySchemeDTO;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.model.HolidayCalendarModel;
import com.EmpTimeHub.model.HolidaySchemeModel;
import com.EmpTimeHub.service.HolidayCalendarManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.EmpTimeHub.constants.EndpointConstants.*;

@RestController
public class HolidaysController {

    @Autowired
    private HolidayCalendarManagement calendarManagement;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(HOLIDAYS_CALENDAR_REGISTER)
    public ResponseEntity<WebResponseDTO<String>> createHoliday(
            @RequestBody HolidayCalendarModel model,
            @AuthenticationPrincipal UserDetails userDetails) {
        String response = calendarManagement.createHolidayCalendar(model, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(true, response, HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(HOLIDAYS_CALENDAR_UPDATE)
    public ResponseEntity<WebResponseDTO<String>> updateHoliday(
            @PathVariable UUID id,
            @RequestBody HolidayCalendarModel model,
            @AuthenticationPrincipal UserDetails userDetails) {

        calendarManagement.updateHolidayCalendar(id, model, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Updated Holiday Calendar Successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(HOLIDAYS_CALENDAR_DELETE)
    public ResponseEntity<WebResponseDTO<String>> deleteHoliday(
            @RequestParam UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        calendarManagement.deleteHolidayCalendar(id, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Safely Deleted Holiday Calendar Successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(HOLIDAYS_SCHEME_REGISTER)
    public ResponseEntity<WebResponseDTO<String>> createScheme(
            @RequestBody HolidaySchemeModel model,
            @AuthenticationPrincipal UserDetails userDetails) {

        String response = calendarManagement.createHolidayScheme(model, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(true, response, HttpStatus.CREATED.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(HOLIDAYS_SCHEME_UPDATE)
    public ResponseEntity<WebResponseDTO<String>> updateScheme(
            @RequestParam UUID id,
            @RequestBody HolidaySchemeModel model,
            @AuthenticationPrincipal UserDetails userDetails) {

        calendarManagement.updateHolidayScheme(id, model, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Updated Holiday Scheme Successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(HOLIDAYS_SCHEME_DELETE)
    public ResponseEntity<WebResponseDTO<String>> deleteScheme(
            @RequestParam UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        calendarManagement.deleteHolidayScheme(id, userDetails.getUsername());
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Safely Deleted Holiday Scheme Successfully", HttpStatus.OK.value()));
    }

    @GetMapping(HOLIDAYS_SCHEME_VIEW_ID)
    public ResponseEntity<WebResponseDTO<?>> getByIdScheme(@PathVariable UUID id) {

        HolidaySchemeDTO dto = calendarManagement.getHolidaySchemeById(id);
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Got Holiday Scheme Successfully by Id", HttpStatus.OK.value(), dto));
    }


    @GetMapping(HOLIDAYS_CALENDAR_VIEW_ID)
    public ResponseEntity<WebResponseDTO<HolidayCalendarDTO>> getByIdCalendar(@PathVariable UUID id) {

        HolidayCalendarDTO dto = calendarManagement.getHolidayCalendarById(id);
        return ResponseEntity.ok(new WebResponseDTO<HolidayCalendarDTO>(
                true, "Got Holiday Calendar Successfully by Id", HttpStatus.OK.value(), dto));
    }

    @GetMapping(HOLIDAYS_SCHEME_VIEW_ALL)
    public ResponseEntity<WebResponseDTO<?>> getAllScheme() {

        List<HolidaySchemeDTO> dtos = calendarManagement.getAllHolidaySchemesForAdmin();
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Got All Holiday Scheme Successfully", HttpStatus.OK.value(),dtos));
    }

    @GetMapping(HOLIDAYS_CALENDAR_VIEW_ALL)
    public ResponseEntity<WebResponseDTO<?>> getAllCalendar() {

        List<HolidayCalendarDTO> dtos = calendarManagement.getAllHolidayCalendarsForAdmin();
        return ResponseEntity.ok(new WebResponseDTO<>(
                true, "Got All Holiday Calendar Successfully", HttpStatus.OK.value(),dtos));
    }
}
