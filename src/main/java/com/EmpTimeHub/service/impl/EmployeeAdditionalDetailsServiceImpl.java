package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.dto.EmployeeAdditionalDetailsDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.EmployeeAdditionalDetails;
import com.EmpTimeHub.repository.EmployeeAdditionalDetailsRepository;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.service.EmployeeAdditionalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeAdditionalDetailsServiceImpl implements EmployeeAdditionalDetailsService {

    private final EmployeeAdditionalDetailsRepository additionalDetailsRepository;
    private final EmployeeRepository employeeRepository;

    // -------------------- Save Additional Details --------------------
    @Override
    public EmployeeAdditionalDetails saveAdditionalDetails(EmployeeAdditionalDetails entity) {
        return additionalDetailsRepository.save(entity);
    }

    // -------------------- Update Additional Details --------------------
    @Transactional
    @Override
    public EmployeeAdditionalDetailsDTO updateAdditionalDetails(EmployeeAdditionalDetailsDTO dto, UUID employeeId) {

        // Fetch existing details or create a new one if absent
        EmployeeAdditionalDetails existingDetails = additionalDetailsRepository
                .findByEmployee_EmployeeId(employeeId)
                .orElse(EmployeeAdditionalDetails.builder().build());

        // Ensure the Employee reference is always set
        if (existingDetails.getEmployee() == null) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            existingDetails.setEmployee(employee);
        }

        // Update only non-null fields from DTO
        if (dto.getOfferLetterUrl() != null) existingDetails.setOfferLetterUrl(dto.getOfferLetterUrl());
        if (dto.getContractUrl() != null) existingDetails.setContractUrl(dto.getContractUrl());
        if (dto.getTaxDeclarationFormUrl() != null) existingDetails.setTaxDeclarationFormUrl(dto.getTaxDeclarationFormUrl());
        if (dto.getWorkPermitUrl() != null) existingDetails.setWorkPermitUrl(dto.getWorkPermitUrl());
        if (dto.getBackgroundCheckStatus() != null) existingDetails.setBackgroundCheckStatus(dto.getBackgroundCheckStatus());
        if (dto.getRemarks() != null) existingDetails.setRemarks(dto.getRemarks());

        // Update timestamp
        existingDetails.setUpdatedAt(LocalDateTime.now());

        // Save updated entity
        EmployeeAdditionalDetails saved = additionalDetailsRepository.save(existingDetails);

        // Map to DTO and return
        return mapToDTO(saved);
    }

    // -------------------- Fetch Additional Details by Employee ID --------------------
    @Override
    public EmployeeAdditionalDetails getAdditionalDetailsByEmployeeId(UUID employeeId) {
        return additionalDetailsRepository.findByEmployee_EmployeeId(employeeId)
                .orElse(EmployeeAdditionalDetails.builder().build());
    }

    // -------------------- Delete Additional Details --------------------
    @Override
    public void deleteAdditionalDetails(UUID hrAdminId) {
        additionalDetailsRepository.deleteById(hrAdminId);
    }

    // -------------------- Helper: Map Entity to DTO --------------------
    private EmployeeAdditionalDetailsDTO mapToDTO(EmployeeAdditionalDetails entity) {
        EmployeeAdditionalDetailsDTO dto = new EmployeeAdditionalDetailsDTO();
        dto.setOfferLetterUrl(entity.getOfferLetterUrl());
        dto.setContractUrl(entity.getContractUrl());
        dto.setTaxDeclarationFormUrl(entity.getTaxDeclarationFormUrl());
        dto.setWorkPermitUrl(entity.getWorkPermitUrl());
        dto.setBackgroundCheckStatus(entity.getBackgroundCheckStatus());
        dto.setRemarks(entity.getRemarks());
        return dto;
    }
}
