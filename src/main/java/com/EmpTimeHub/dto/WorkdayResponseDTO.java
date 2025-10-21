package com.EmpTimeHub.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkdayResponseDTO {
    private int totalDays;
    private int holidays;
    private int weekends;
    private int totalHolidays;
    private Double leaveDuration;
}