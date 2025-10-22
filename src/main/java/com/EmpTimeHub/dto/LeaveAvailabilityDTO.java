package com.EmpTimeHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveAvailabilityDTO {
    private boolean isAvailable;
    private Double availableLeaves;
    private double requestedLeave;
    private String message;
}
