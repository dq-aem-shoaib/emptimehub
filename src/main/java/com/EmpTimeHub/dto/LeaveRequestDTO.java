package com.EmpTimeHub.dto;

import com.EmpTimeHub.constants.EnumConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {
    private UUID leaveId;
    private EnumConstants.LeaveType type;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String subject;
    private String context;
}
