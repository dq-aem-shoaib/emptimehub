package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.EmployeeAdditionalDetailsDTO;
import com.EmpTimeHub.entity.EmployeeAdditionalDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface EmployeeAdditionalDetailsService {

    // Save additional details for an employee
    EmployeeAdditionalDetails saveAdditionalDetails(EmployeeAdditionalDetails employeeAdditionalDetails);

    // Update additional details
    @Transactional
    EmployeeAdditionalDetailsDTO updateAdditionalDetails(EmployeeAdditionalDetailsDTO dto, UUID employeeId);

    // Fetch additional details by employee ID
    EmployeeAdditionalDetails getAdditionalDetailsByEmployeeId(UUID employeeId);

    // Delete additional details
    void deleteAdditionalDetails(UUID adminId);
}
