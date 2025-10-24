package com.EmpTimeHub.dto;

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
public class LeaveResponseDTO {

    private UUID leaveId;
    private String approverName;
    private String employeeName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String financialType;
    private String leaveCategoryType;
    private String subject;
    private String context;
    private String status;
    private String approverComment;
    private int holidays;
    private Double leaveDuration;
    private String attachmentUrl;
}
