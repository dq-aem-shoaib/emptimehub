package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.entity.TimeSheet;
import com.EmpTimeHub.model.TimeSheetModel;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.UUID;

public interface TimeSheetService {
    TimeSheet createTimeSheet(TimeSheetModel timeSheet, String loggedInUserEmail);
    TimeSheetResponseDto getTimeSheetById(UUID timesheetId, String loggedInUserEmail);
    Page<TimeSheetResponseDto> getAllTimeSheets(int page, int size, String direction, String orderBy,
                                     String loggedInUserEmail, LocalDate startDate, LocalDate endDate);

    void updateTimeSheet(UUID timesheetId,TimeSheetModel updatedSheet, String loggedInUserEmail);
    TimeSheet updateStatus(UUID timesheetId, String status);
    void deleteTimeSheet(UUID timesheetId, String loggedInUserEmail);
}
