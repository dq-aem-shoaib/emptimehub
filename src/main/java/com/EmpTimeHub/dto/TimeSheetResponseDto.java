package com.EmpTimeHub.dto;

import com.EmpTimeHub.constants.EnumConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class TimeSheetResponseDto {
    private UUID timesheetId;
    private UUID clientId;
    private String clientName;
    private UUID employeeId;
    private String employeeName;
    private BigDecimal workedHours;
    private LocalDate workDate;
    private String taskName;
    private String managerComment;
    private String projectName;
    private LocalDate projectStartedAt;
    private LocalDate projectEndedAt;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
