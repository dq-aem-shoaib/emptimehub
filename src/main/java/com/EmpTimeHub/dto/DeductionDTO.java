package com.EmpTimeHub.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductionDTO {
    private UUID deductionId;
    private String deductionType;   // e.g., PF, ESI, TDS
    private BigDecimal amount;
}
