package com.EmpTimeHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private UUID employeeId;

    // ---------- Personal Info ----------
    private String firstName;
    private String lastName;
    private String personalEmail;
    private String companyEmail;
    private String contactNumber;
    private LocalDate dateOfBirth;

    // ---------- Job Info ----------
    private String designation;
    private LocalDate dateOfJoining;
    private String currency;
    private BigDecimal rateCard;
    private Integer availableLeaves;

    // ---------- Identification ----------
    private String panNumber;
    private String aadharNumber;

    // ---------- Client Info ----------
    private UUID clientId;
    private String clientName;

    // ---------- Document URLs ----------
    private String panCardUrl;
    private String aadharCardUrl;
    private String bankPassbookUrl;
    private String tenthCftUrl;
    private String interCftUrl;
    private String degreeCftUrl;
    private String postGraduationCftUrl;

    // ---------- Status ----------
    private String status;
}
