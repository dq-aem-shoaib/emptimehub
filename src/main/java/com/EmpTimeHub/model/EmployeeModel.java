package com.EmpTimeHub.model;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.*;
import com.EmpTimeHub.entity.EmployeeStatutoryDetails;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeModel implements Serializable {

    // ---------- Basic Info ----------
    private String firstName;
    private String lastName;
    private String personalEmail;
    private String companyEmail;
    private String contactNumber;
    private String alternateContactNumber;
    private String gender;
    private String maritalStatus;
    private Integer numberOfChildren;
    private String employeePhotoUrl;
    private String nationality;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String remarks;
    private String skillsAndCertification;

    // ---------- Associations ----------
    private UUID clientId;
    private UUID reportingManagerId;

    // ---------- Job Info ----------
    private EnumConstants.Designation designation;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String currency;
    private BigDecimal rateCard;
    private EnumConstants.EmploymentType employmentType;
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
    private List<AddressModel> addresses;

    //--------------- Documents------------------
    private List<EmployeeDocumentDTO> documents;

    private EmployeeSalaryDTO employeeSalaryDTO;


    private EmployeeAdditionalDetailsDTO employeeAdditionalDetailsDTO;

    private EmployeeEmploymentDetailsDTO employeeEmploymentDetailsDTO;

    private EmployeeInsuranceDetailsDTO employeeInsuranceDetailsDTO;

    private EmployeeStatutoryDetailsDTO employeeStatutoryDetailsDTO;

    private EmployeeEquipmentDTO employeeEquipmentDTO;

}
