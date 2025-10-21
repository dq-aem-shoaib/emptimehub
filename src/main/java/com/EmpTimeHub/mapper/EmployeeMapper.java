package com.EmpTimeHub.mapper;

import com.EmpTimeHub.dto.EmployeeDTO;
import com.EmpTimeHub.model.AddressModel;
import com.EmpTimeHub.model.EmployeeModel;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.entity.BankDetails;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.constants.EnumConstants;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;

import java.util.List;

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
                .gender(model.getGender())
                .maritalStatus(model.getMaritalStatus())
                .numberOfChildren(model.getNumberOfChildren())
                .employeePhotoUrl(model.getEmployeePhotoUrl())
                .dateOfBirth(model.getDateOfBirth())
                .dateOfJoining(model.getDateOfJoining())
                .designation(model.getDesignation())
                .rateCard(model.getRateCard())
                .panNumber(model.getPanNumber())
                .aadharNumber(model.getAadharNumber())
                .availableLeaves(12D)
                .panCardUrl(model.getPanCardUrl())
                .aadharCardUrl(model.getAadharCardUrl())
                .bankPassbookUrl(model.getBankPassbookUrl())
                .tenthCftUrl(model.getTenthCftUrl())
                .interCftUrl(model.getInterCftUrl())
                .degreeCftUrl(model.getDegreeCftUrl())
                .postGraduationCftUrl(model.getPostGraduationCftUrl())
                .companyId(companyId)
                .status("ACTIVE")
                .build();
    }

    /**
     * Convert Employee entity → EmployeeDTO
     */
    public EmployeeDTO toDTO(Employee employee, List<AddressModel> addresses) {
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
