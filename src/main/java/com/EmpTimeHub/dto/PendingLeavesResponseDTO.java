package com.EmpTimeHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO representing leave details shown on the manager dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingLeavesResponseDTO {

    private UUID leaveId;
    private String employeeName;

    private String leaveCategoryTpe;
    private String financialType;

    private LocalDate fromDate;
    private LocalDate toDate;

    private Double leaveDuration;
    private String attachmentUrl;
    private Double remainingLeaves;
    private String context;
    private String status;
}
