package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.dto.EmployeeInsuranceDetailsDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.EmployeeInsuranceDetails;
import com.EmpTimeHub.repository.EmployeeInsuranceDetailsRepository;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.service.EmployeeInsuranceDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeInsuranceDetailsServiceImpl implements EmployeeInsuranceDetailsService {

    private final EmployeeInsuranceDetailsRepository insuranceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public EmployeeInsuranceDetails saveInsuranceDetails(EmployeeInsuranceDetailsDTO dto, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeInsuranceDetails details = EmployeeInsuranceDetails.builder()
                .employee(employee)
                .policyNumber(dto.getPolicyNumber())
                .providerName(dto.getProviderName())
                .coverageStart(dto.getCoverageStart())
                .coverageEnd(dto.getCoverageEnd())
                .nomineeName(dto.getNomineeName())
                .nomineeRelation(dto.getNomineeRelation())
                .nomineeContact(dto.getNomineeContact())
                .groupInsurance(dto.getGroupInsurance())
                .otherBenefits(dto.getOtherBenefits())
                .updatedAt(LocalDateTime.now())
                .build();

        EmployeeInsuranceDetails saved = insuranceRepository.save(details);
        log.info("Saved insurance details for employee {}", employeeId);
        return saved;
    }

    @Transactional
    @Override
    public EmployeeInsuranceDetails updateInsuranceDetails(UUID employeeId, EmployeeInsuranceDetailsDTO dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Fetch existing insurance details or create new
        EmployeeInsuranceDetails details = insuranceRepository.findByEmployee_EmployeeId(employeeId)
                .orElseGet(() -> {
                    EmployeeInsuranceDetails newDetails = EmployeeInsuranceDetails.builder()
                            .employee(employee)
                            .build();
                    return newDetails;
                });

        // Ensure employee reference is set
        details.setEmployee(employee);

        // Update only non-null fields
        if (dto.getPolicyNumber() != null) details.setPolicyNumber(dto.getPolicyNumber());
        if (dto.getProviderName() != null) details.setProviderName(dto.getProviderName());
        if (dto.getCoverageStart() != null) details.setCoverageStart(dto.getCoverageStart());
        if (dto.getCoverageEnd() != null) details.setCoverageEnd(dto.getCoverageEnd());
        if (dto.getNomineeName() != null) details.setNomineeName(dto.getNomineeName());
        if (dto.getNomineeRelation() != null) details.setNomineeRelation(dto.getNomineeRelation());
        if (dto.getNomineeContact() != null) details.setNomineeContact(dto.getNomineeContact());
        if (dto.getGroupInsurance() != null) details.setGroupInsurance(dto.getGroupInsurance());
        if (dto.getOtherBenefits() != null) details.setOtherBenefits(dto.getOtherBenefits());

        details.setUpdatedAt(LocalDateTime.now());

        EmployeeInsuranceDetails updated = insuranceRepository.save(details);
        log.info("Updated insurance details for employee {}", employeeId);
        return updated;
    }

    @Override
    public EmployeeInsuranceDetails getInsuranceDetailsById(UUID insuranceId) {
        return insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new RuntimeException("Insurance details not found"));
    }

    @Override
    public void deleteInsuranceDetails(UUID insuranceId) {
        if (!insuranceRepository.existsById(insuranceId)) {
            throw new RuntimeException("Insurance details not found");
        }
        insuranceRepository.deleteById(insuranceId);
        log.info("Deleted insurance details with id {}", insuranceId);
    }

    @Override
    public EmployeeInsuranceDetails getInsuranceDetailsByEmployeeId(UUID employeeId) {
        return insuranceRepository.findByEmployee_EmployeeId(employeeId).orElse(null);
    }

    @Override
    public EmployeeInsuranceDetailsDTO toDTO(EmployeeInsuranceDetails details) {
        if (details == null) return null;

        return EmployeeInsuranceDetailsDTO.builder()
                .insuranceId(details.getInsuranceId())
                .employeeId(details.getEmployee() != null ? details.getEmployee().getEmployeeId() : null)
                .policyNumber(details.getPolicyNumber())
                .providerName(details.getProviderName())
                .coverageStart(details.getCoverageStart())
                .coverageEnd(details.getCoverageEnd())
                .nomineeName(details.getNomineeName())
                .nomineeRelation(details.getNomineeRelation())
                .nomineeContact(details.getNomineeContact())
                .groupInsurance(details.getGroupInsurance())
                .otherBenefits(details.getOtherBenefits())
                .build();
    }
}
