package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.entity.TimeSheet;
import com.EmpTimeHub.model.TimeSheetModel;

import java.time.LocalDate;
import java.util.*;
import java.util.UUID;

public interface TimeSheetService {
    TimeSheet createTimeSheet(TimeSheetModel timeSheet, String loggedInUserEmail);
    TimeSheetResponseDto getTimeSheetById(UUID timesheetId, String loggedInUserEmail);
    List<TimeSheet> getAllTimeSheets();
    List<TimeSheet> getTimeSheetsByEmployee(UUID employeeId);
    List<TimeSheet> getTimeSheetsByClient(UUID clientId);
    List<TimeSheet> getTimeSheetsByDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate);
    List<TimeSheet> getTimeSheetsByStatus(String status);
    TimeSheet updateTimeSheet(UUID timesheetId, TimeSheet updatedSheet);
    TimeSheet updateStatus(UUID timesheetId, String status);
    void deleteTimeSheet(UUID timesheetId);
}
