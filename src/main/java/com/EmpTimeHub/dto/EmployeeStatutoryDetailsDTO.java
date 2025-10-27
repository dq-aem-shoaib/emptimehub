package com.EmpTimeHub.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeStatutoryDetailsDTO {
    private UUID statutoryId;
    private UUID employeeId;
    private String passportNumber;
    private String taxRegime;
    private String pfUanNumber;
    private String esiNumber;
    private String ssnNumber;
}
