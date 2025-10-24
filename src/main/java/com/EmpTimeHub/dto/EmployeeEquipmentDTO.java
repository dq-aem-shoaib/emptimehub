package com.EmpTimeHub.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEquipmentDTO {
    private UUID equipmentId;
    private String equipmentType;
    private String serialNumber;
    private LocalDate issuedDate;
    private LocalDate returnedDate;
}
