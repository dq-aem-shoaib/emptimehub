package com.EmpTimeHub.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEmploymentDetailsDTO {
    private UUID employmentId;
    private UUID employeeId;
    private String noticePeriodDuration;
    private Boolean probationApplicable;
    private String probationDuration;
    private String probationNoticePeriod;
    private Boolean bondApplicable;
    private String bondDuration;
    private String workingModel;
    private String shiftTiming;
    private String department;
    private LocalDate dateOfConfirmation;
    private String location;
}
