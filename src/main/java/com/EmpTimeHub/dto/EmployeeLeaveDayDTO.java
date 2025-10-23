package com.EmpTimeHub.dto;

import com.EmpTimeHub.constants.EnumConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeaveDayDTO {
    private LocalDate date;
    private EnumConstants.LeaveCategory leaveCategory;
    private Double duration;
}
