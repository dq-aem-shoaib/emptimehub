package com.EmpTimeHub.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TimeSheetModel {
    private LocalDate workDate;
    private BigDecimal hoursWorked;
    private String taskName;
    private String taskDescription;
}
