package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.TimeSheet;
import com.EmpTimeHub.model.TimeSheetModel;
import com.EmpTimeHub.repository.ClientRepository;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.TimeSheetRepository;
import com.EmpTimeHub.service.TimeSheetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TimeSheetServiceImpl implements TimeSheetService {

    private TimeSheetRepository timeSheetRepository;
    private EmployeeRepository employeeRepository;
    private ClientRepository clientRepository;

    @Override
    public TimeSheet createTimeSheet(TimeSheetModel timeSheet,String loggedInUserEmail) {

        Employee emp = employeeRepository.getEmployeeByEmail(loggedInUserEmail);
        Client client = emp.getClient();

        TimeSheet sheet = TimeSheet.builder()
                .employee(emp)
                .client(client)
                .hoursWorked(timeSheet.getHoursWorked())
                .workDate(LocalDate.now())
                .taskName(timeSheet.getTaskName())
                .taskDescription(timeSheet.getTaskDescription())
                .status(timeSheet.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return timeSheetRepository.save(sheet);
    }

    @Override
    public TimeSheetResponseDto getTimeSheetById(UUID timesheetId, String loggedInUserEmail) {


        TimeSheet oneTimeSheet =  timeSheetRepository.findById(timesheetId)
                .orElseThrow(() -> new RuntimeException("Timesheet not found"));

        Employee emp = employeeRepository.getEmployeeByEmail(loggedInUserEmail);
        Client client = emp.getClient();


        TimeSheetResponseDto responseDto = TimeSheetResponseDto.builder()
                .clientId(client.getClientId())
                .clientName(client.getCompanyName())
                .employeeId(emp.getEmployeeId())
                .employeeName(emp.getFirstName()+" "+emp.getLastName())
                .taskName(oneTimeSheet.getTaskName())
                .taskDescription(oneTimeSheet.getTaskDescription())
                .workDate(oneTimeSheet.getWorkDate())
                .workedHours(oneTimeSheet.getHoursWorked())
                .status(oneTimeSheet.getStatus())
                .createdAt(oneTimeSheet.getCreatedAt())
                .build();
        return responseDto;
    }

    @Override
    public List<TimeSheet> getAllTimeSheets() {
        return timeSheetRepository.findAll();
    }

    @Override
    public List<TimeSheet> getTimeSheetsByEmployee(UUID employeeId) {
        return List.of();
    }

    @Override
    public List<TimeSheet> getTimeSheetsByClient(UUID clientId) {
        return List.of();
    }

    @Override
    public List<TimeSheet> getTimeSheetsByDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public List<TimeSheet> getTimeSheetsByStatus(String status) {
        return List.of();
    }

    @Override
    public TimeSheet updateTimeSheet(UUID timesheetId, TimeSheet updatedSheet) {
        return null;
    }

    @Override
    public TimeSheet updateStatus(UUID timesheetId, String status) {
        return null;
    }

    @Override
    public void deleteTimeSheet(UUID timesheetId) {

    }
}
