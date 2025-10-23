package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.entity.TimeSheet;
import com.EmpTimeHub.model.TimeSheetModel;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TimeSheetService {
    List<TimeSheet> createTimeSheet(List<TimeSheetModel> timeSheet, String loggedInUserEmail);
    TimeSheetResponseDto getTimeSheetById(UUID timesheetId, String loggedInUserEmail);
    Page<TimeSheetResponseDto> getAllTimeSheets(int page, int size, String direction, String orderBy,
                                     String loggedInUserEmail, LocalDate startDate, LocalDate endDate);

    void updateTimeSheet(UUID timesheetId,TimeSheetModel updatedSheet, String loggedInUserEmail);
    TimeSheet updateStatus(UUID timesheetId, String status);
    void deleteTimeSheet(UUID timesheetId, String loggedInUserEmail);
    void requestToManager(UUID timesheetId, String loggedInEmail);
}
