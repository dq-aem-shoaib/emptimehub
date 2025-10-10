package com.EmpTimeHub.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TimeSheetModel {
    private BigDecimal hoursWorked;
    private String taskName;
    private String taskDescription;
    private String status;
}
