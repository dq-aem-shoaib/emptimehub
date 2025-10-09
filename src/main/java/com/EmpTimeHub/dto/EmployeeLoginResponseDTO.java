package com.EmpTimeHub.dto;

import com.EmpTimeHub.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLoginResponseDTO {
    private UUID employeeId;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private Address address;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String designation;
    private BigDecimal rateCard;
    private UUID clientId;
    private String panNumber;
    private Integer availableLeaves;
    private String aadharNumber;
    private String accountNumber;
    private String status;
    private String responseMessage;
    private LoginResponseDTO loginResponseDTO;
}
