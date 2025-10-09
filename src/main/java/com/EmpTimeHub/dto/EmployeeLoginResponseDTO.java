package com.EmpTimeHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLoginResponseDTO {
    private String employeeId;
    private String fullName;
    private String contactNumber;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String designation;
    private BigDecimal rateCard;
    private String panNumber;
    private Integer availableLeaves;
    private String aadharNumber;
    private String accountNumber;
    private String status;
    private String responseMessage;
    private LoginResponseDTO loginResponseDTO;
}
