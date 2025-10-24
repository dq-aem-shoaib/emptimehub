package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.EmployeeEmploymentDetailsDTO;
import com.EmpTimeHub.entity.EmployeeEmploymentDetails;

import java.util.UUID;

public interface EmployeeEmploymentDetailsService {

    EmployeeEmploymentDetails saveEmploymentDetails(EmployeeEmploymentDetailsDTO dto, UUID employeeId);

    EmployeeEmploymentDetails updateEmploymentDetails(UUID employmentId, EmployeeEmploymentDetailsDTO dto);

    EmployeeEmploymentDetails getEmploymentDetailsById(UUID employmentId);

    void deleteEmploymentDetails(UUID employmentId);

    EmployeeEmploymentDetails getEmploymentDetailsByEmployeeId(UUID employeeId);

    EmployeeEmploymentDetailsDTO toDTO(EmployeeEmploymentDetails employmentDetails);
}
