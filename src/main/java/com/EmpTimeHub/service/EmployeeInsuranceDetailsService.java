package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.EmployeeInsuranceDetailsDTO;
import com.EmpTimeHub.entity.EmployeeInsuranceDetails;

import java.util.UUID;

public interface EmployeeInsuranceDetailsService {

    EmployeeInsuranceDetails saveInsuranceDetails(EmployeeInsuranceDetailsDTO dto, UUID employeeId);

    EmployeeInsuranceDetails updateInsuranceDetails(UUID employeeId, EmployeeInsuranceDetailsDTO dto);

    EmployeeInsuranceDetails getInsuranceDetailsById(UUID insuranceId);

    void deleteInsuranceDetails(UUID insuranceId);

    EmployeeInsuranceDetails getInsuranceDetailsByEmployeeId(UUID employeeId);

    EmployeeInsuranceDetailsDTO toDTO(EmployeeInsuranceDetails insuranceDetails);
}
