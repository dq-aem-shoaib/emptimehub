package com.EmpTimeHub.model;

import lombok.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Setter
@Getter
public class HolidayAssignmentModel {
    private UUID employeeId;
    private UUID holidaySchemeId;
    private UUID assignedByAdminId;
    private boolean inheritanceFlag;
}
