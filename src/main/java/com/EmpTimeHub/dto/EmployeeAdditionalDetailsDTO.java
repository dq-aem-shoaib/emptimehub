package com.EmpTimeHub.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAdditionalDetailsDTO {
    private String offerLetterUrl;
    private String contractUrl;
    private String taxDeclarationFormUrl;
    private String workPermitUrl;
    private String backgroundCheckStatus;
    private String remarks;
}
