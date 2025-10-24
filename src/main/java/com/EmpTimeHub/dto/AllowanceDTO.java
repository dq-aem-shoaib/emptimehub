package com.EmpTimeHub.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllowanceDTO {
    private UUID allowanceId;
    private String allowanceType;   // e.g., HRA, Transport, Medical
    private BigDecimal amount;
}
