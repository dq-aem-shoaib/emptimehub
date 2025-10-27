package com.EmpTimeHub.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TimeSheetModel {
    private UUID timesheetId;
    private LocalDate workDate;
    private BigDecimal hoursWorked;
    private String taskName;
    private String managerComment;
}
