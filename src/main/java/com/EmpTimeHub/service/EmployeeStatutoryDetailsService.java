package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.EmployeeStatutoryDetailsDTO;
import com.EmpTimeHub.entity.EmployeeStatutoryDetails;

import java.util.UUID;

public interface EmployeeStatutoryDetailsService {

    EmployeeStatutoryDetails saveStatutoryDetails(EmployeeStatutoryDetailsDTO dto, UUID employeeId);

    EmployeeStatutoryDetails updateStatutoryDetails(UUID employeeId, EmployeeStatutoryDetailsDTO dto);

    EmployeeStatutoryDetails getStatutoryDetailsById(UUID statutoryId);

    void deleteStatutoryDetails(UUID statutoryId);

    EmployeeStatutoryDetails getStatutoryDetailsByEmployeeId(UUID employeeId);

    EmployeeStatutoryDetailsDTO toDTO(EmployeeStatutoryDetails details);
}
