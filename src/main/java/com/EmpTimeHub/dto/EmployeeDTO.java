package com.EmpTimeHub.dto;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.model.AddressModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private String alternateContactNumber;
    private String gender;
    private String maritalStatus;
    private Integer numberOfChildren;
    private LocalDate dateOfBirth;
    private String employeePhotoUrl;

    // ---------- Job Info ----------
    private String designation;
    private LocalDate dateOfJoining;
    private BigDecimal rateCard;
    private Integer availableLeaves;
    private EnumConstants.EmploymentType employmentType;
    private String companyId;

    // ---------- Bank Details ----------
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private String ifscCode;
    private String branchName;


    // ---------- Identification ----------
    private String panNumber;
    private String aadharNumber;

    // ---------- Associations ----------
    private UUID clientId;
    private String clientName;
    private UUID reportingManagerId;
    private String reportingManagerName;

    // ---------- Document URLs ----------
    private String panCardUrl;
    private String aadharCardUrl;
    private String bankPassbookUrl;
    private String tenthCftUrl;
    private String interCftUrl;
    private String degreeCftUrl;
    private String postGraduationCftUrl;

    private List<AddressModel> addresses;

    // ---------- Status ----------
    private String status;
    private LocalDateTime createdAt;         // ✅ Added
    private LocalDateTime updatedAt;         // ✅ Added
}
