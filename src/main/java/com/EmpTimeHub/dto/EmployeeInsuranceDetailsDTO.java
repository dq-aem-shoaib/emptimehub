package com.EmpTimeHub.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeInsuranceDetailsDTO {
    private UUID insuranceId;
    private UUID employeeId;
    private String policyNumber;
    private String providerName;
    private LocalDate coverageStart;
    private LocalDate coverageEnd;
    private String nomineeName;
    private String nomineeRelation;
    private String nomineeContact;
    private Boolean groupInsurance;
    private Map<String, String> otherBenefits;
}
