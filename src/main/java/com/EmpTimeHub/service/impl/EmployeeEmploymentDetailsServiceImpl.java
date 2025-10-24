package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.dto.EmployeeEmploymentDetailsDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.EmployeeEmploymentDetails;
import com.EmpTimeHub.repository.EmployeeEmploymentDetailsRepository;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.service.EmployeeEmploymentDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeEmploymentDetailsServiceImpl implements EmployeeEmploymentDetailsService {

    private final EmployeeEmploymentDetailsRepository employmentDetailsRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public EmployeeEmploymentDetails saveEmploymentDetails(EmployeeEmploymentDetailsDTO dto, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeEmploymentDetails details = EmployeeEmploymentDetails.builder()
                .employee(employee)
                .noticePeriodDuration(dto.getNoticePeriodDuration())
                .probationApplicable(dto.getProbationApplicable())
                .probationDuration(dto.getProbationDuration())
                .probationNoticePeriod(dto.getProbationNoticePeriod())
                .bondApplicable(dto.getBondApplicable())
                .bondDuration(dto.getBondDuration())
                .workingModel(dto.getWorkingModel())
                .shiftTiming(dto.getShiftTiming())
                .department(dto.getDepartment())
                .dateOfConfirmation(dto.getDateOfConfirmation())
                .location(dto.getLocation())
                .updatedAt(LocalDateTime.now())
                .build();

        EmployeeEmploymentDetails saved = employmentDetailsRepository.save(details);
        log.info("Saved employment details for employee {}", employeeId);
        return saved;
    }

    @Transactional
    @Override
    public EmployeeEmploymentDetails updateEmploymentDetails(UUID employeeId, EmployeeEmploymentDetailsDTO dto) {
        // Fetch the employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Fetch existing employment details or create new
        EmployeeEmploymentDetails details = employmentDetailsRepository.findByEmployee_EmployeeId(employeeId)
                .orElseGet(() -> {
                    EmployeeEmploymentDetails newDetails = EmployeeEmploymentDetails.builder()
                            .employee(employee)
                            .build();
                    return newDetails;
                });

        // Ensure the employee reference is always set
        details.setEmployee(employee);

        // Update only non-null fields
        if (dto.getNoticePeriodDuration() != null) details.setNoticePeriodDuration(dto.getNoticePeriodDuration());
        if (dto.getProbationApplicable() != null) details.setProbationApplicable(dto.getProbationApplicable());
        if (dto.getProbationDuration() != null) details.setProbationDuration(dto.getProbationDuration());
        if (dto.getProbationNoticePeriod() != null) details.setProbationNoticePeriod(dto.getProbationNoticePeriod());
        if (dto.getBondApplicable() != null) details.setBondApplicable(dto.getBondApplicable());
        if (dto.getBondDuration() != null) details.setBondDuration(dto.getBondDuration());
        if (dto.getWorkingModel() != null) details.setWorkingModel(dto.getWorkingModel());
        if (dto.getShiftTiming() != null) details.setShiftTiming(dto.getShiftTiming());
        if (dto.getDepartment() != null) details.setDepartment(dto.getDepartment());
        if (dto.getDateOfConfirmation() != null) details.setDateOfConfirmation(dto.getDateOfConfirmation());
        if (dto.getLocation() != null) details.setLocation(dto.getLocation());

        // Update timestamp
        details.setUpdatedAt(LocalDateTime.now());

        // Save and return
        EmployeeEmploymentDetails updated = employmentDetailsRepository.save(details);
        log.info("Updated employment details for employee {}", employeeId);
        return updated;
    }


    @Override
    public EmployeeEmploymentDetails getEmploymentDetailsById(UUID employmentId) {
        return employmentDetailsRepository.findById(employmentId)
                .orElseThrow(() -> new RuntimeException("Employment details not found"));
    }

    @Override
    public void deleteEmploymentDetails(UUID employmentId) {
        if (!employmentDetailsRepository.existsById(employmentId)) {
            throw new RuntimeException("Employment details not found");
        }
        employmentDetailsRepository.deleteById(employmentId);
        log.info("Deleted employment details with id {}", employmentId);
    }

    @Override
    public EmployeeEmploymentDetails getEmploymentDetailsByEmployeeId(UUID employeeId) {
        return employmentDetailsRepository.findByEmployee_EmployeeId(employeeId).orElse(null);
    }

    @Override
    public EmployeeEmploymentDetailsDTO toDTO(EmployeeEmploymentDetails employmentDetails) {
        if (employmentDetails == null) {
            return null;
        }

        return EmployeeEmploymentDetailsDTO.builder()
                .employmentId(employmentDetails.getEmploymentId())
                .employeeId(employmentDetails.getEmployee() != null ? employmentDetails.getEmployee().getEmployeeId() : null)
                .noticePeriodDuration(employmentDetails.getNoticePeriodDuration())
                .probationApplicable(employmentDetails.getProbationApplicable())
                .probationDuration(employmentDetails.getProbationDuration())
                .probationNoticePeriod(employmentDetails.getProbationNoticePeriod())
                .bondApplicable(employmentDetails.getBondApplicable())
                .bondDuration(employmentDetails.getBondDuration())
                .workingModel(employmentDetails.getWorkingModel())
                .shiftTiming(employmentDetails.getShiftTiming())
                .department(employmentDetails.getDepartment())
                .dateOfConfirmation(employmentDetails.getDateOfConfirmation())
                .location(employmentDetails.getLocation())
                .build();
    }

}
