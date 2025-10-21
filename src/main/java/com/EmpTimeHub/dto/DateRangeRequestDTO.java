package com.EmpTimeHub.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeRequestDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private  Boolean partialDay;
}
