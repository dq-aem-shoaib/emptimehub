package com.EmpTimeHub.mapper;

import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.dto.EmployeeDocumentDTO;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.model.AddressModel;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.constants.EnumConstants;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    /**
     * Convert EmployeeModel + related entities → Employee entity
     */
    public Employee toEntity(EmployeeModel model,
                             User user,
                             Client client,
                             Employee reportingManager,
                             BankDetails bankDetails,
                             String companyId) {

        return Employee.builder()
                .user(user)
                .client(client)
                .reportingManager(reportingManager)
                .bankDetails(bankDetails)
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .personalEmail(model.getPersonalEmail())
                .companyEmail(model.getCompanyEmail())
                .contactNumber(model.getContactNumber())
                .alternateContactNumber(model.getAlternateContactNumber())
                .nationality(model.getNationality())
                .emergencyContactName(model.getEmergencyContactName())
                .emergencyContactNumber(model.getEmergencyContactNumber())
                .gender(model.getGender())
                .maritalStatus(model.getMaritalStatus())
                .numberOfChildren(model.getNumberOfChildren())
                .employeePhotoUrl(model.getEmployeePhotoUrl())
                .dateOfBirth(model.getDateOfBirth())
                .dateOfJoining(model.getDateOfJoining())
                .designation(model.getDesignation())
                .rateCard(model.getRateCard())
                .remarks(model.getRemarks())
                .skillsAndCertification(model.getSkillsAndCertification())
                .panNumber(model.getPanNumber())
                .aadharNumber(model.getAadharNumber())
                .availableLeaves(12D)
                .companyId(companyId)
                .status("ACTIVE")
                .build();
    }

    /**
     * Convert Employee entity → EmployeeDTO
     */
    public EmployeeDTO toDTO(Employee employee, List<AddressModel> addresses , List<EmployeeDocument> documents) {
        EmployeeDTO dto = new EmployeeDTO();
        BeanUtils.copyProperties(employee, dto);

        if (employee.getClient() != null) {
            dto.setClientId(employee.getClient().getClientId());
            dto.setClientName(employee.getClient().getCompanyName());
        }

        if (employee.getDesignation() != null) {
            dto.setDesignation(employee.getDesignation().name()); // convert enum to string
        }

        if (employee.getReportingManager() != null) {
            dto.setReportingManagerId(employee.getReportingManager().getEmployeeId());
            dto.setReportingManagerName(employee.getReportingManager().getFirstName() + " " +
                    employee.getReportingManager().getLastName());
        }

        dto.setAddresses(addresses);

        if (employee.getBankDetails() != null) {
            dto.setAccountHolderName(employee.getBankDetails().getAccountHolderName());
            dto.setBankName(employee.getBankDetails().getBankName());
            dto.setIfscCode(employee.getBankDetails().getIfscCode());
            dto.setBranchName(employee.getBankDetails().getBranchName());
        }

        // ---------- Documents ----------
        List<EmployeeDocumentDTO> documentDTOs = documents == null
                ? Collections.emptyList()
                : documents.stream()
                .map(doc -> EmployeeDocumentDTO.builder()
                        .documentId(doc.getDocumentId())
                        .fileUrl(doc.getFileUrl())
                        .docType(doc.getDocType())
                        .build())
                .collect(Collectors.toList());

        dto.setDocuments(documentDTOs);




        return dto;
    }

    /**
     * Convert Designation enum → Role enum (optional helper)
     */
    public EnumConstants.Role mapDesignationToRole(EnumConstants.Designation designation) {
        if (designation == EnumConstants.Designation.REPORTING_MANAGER) {
            return EnumConstants.Role.MANAGER;
        } else {
            return EnumConstants.Role.EMPLOYEE;
        }
    }
}
