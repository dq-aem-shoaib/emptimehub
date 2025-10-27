package com.EmpTimeHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
public class EmployeeSalaryDTO {
    private UUID employeeId;
    private BigDecimal basicPay;
    private String payType;          // MONTHLY, HOURLY, CONTRACT
    private BigDecimal standardHours;

    private String bankAccountNumber;
    private String ifscCode;
    private String payClass;

    private List<AllowanceDTO> allowances;
    private List<DeductionDTO> deductions;
}
