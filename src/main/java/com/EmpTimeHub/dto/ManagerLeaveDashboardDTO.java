package com.EmpTimeHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * DTO representing leave details shown on manager dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerLeaveDashboardDTO {
    private UUID leaveId;
    private String employeeName;
    private String leaveType;
    private Double leaveDuration;
    private String reason;
    private String attachmentUrl;
    private Double remainingLeaves;
    private String status;
}
