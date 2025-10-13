package com.EmpTimeHub.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeModel implements Serializable {

    // ---------- Basic Info ----------
    private String firstName;
    private String lastName;
    private String personalEmail;   // Employee's personal email
    private String companyEmail;    // Company-provided email
    private String contactNumber;

    // ---------- Associations ----------
    private UUID clientId;          // Assigned client ID (nullable)
    // ---------- Job Info ----------
    private String designation;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String currency;        // e.g., "INR", "USD"
    private BigDecimal rateCard;    // Hourly or daily rate

    // ---------- Identification ----------
    private String panNumber;
    private String aadharNumber;

    // ---------- Bank Details ----------
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private String ifscCode;
    private String branchName;


    // ---------- Address ----------
    private String houseNo;
    private String streetName;
    private String city;
    private String state;
    private String pinCode;
    private String country;

    // ---------- Document URLs (optional uploads) ----------
    private String panCardUrl;
    private String aadharCardUrl;
    private String bankPassbookUrl;
    private String tenthCftUrl;
    private String interCftUrl;
    private String degreeCftUrl;
    private String postGraduationCftUrl;

}
