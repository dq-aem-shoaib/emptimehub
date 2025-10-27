package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.dto.EmployeeStatutoryDetailsDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.EmployeeStatutoryDetails;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.EmployeeStatutoryDetailsRepository;
import com.EmpTimeHub.service.EmployeeStatutoryDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeStatutoryDetailsServiceImpl implements EmployeeStatutoryDetailsService {

    private final EmployeeStatutoryDetailsRepository statutoryRepository;
    private final EmployeeRepository employeeRepository;

    // -------------------- Save Statutory Details --------------------
    /**
     * Saves statutory details for a given employee.
     *
     * @param dto        the DTO containing statutory details
     * @param employeeId the employee's UUID
     * @return saved EmployeeStatutoryDetails entity
     */
    @Transactional
    @Override
    public EmployeeStatutoryDetails saveStatutoryDetails(EmployeeStatutoryDetailsDTO dto, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeStatutoryDetails details = EmployeeStatutoryDetails.builder()
                .employee(employee)
                .passportNumber(dto.getPassportNumber())
                .taxRegime(dto.getTaxRegime())
                .pfUanNumber(dto.getPfUanNumber())
                .esiNumber(dto.getEsiNumber())
                .ssnNumber(dto.getSsnNumber())
                .updatedAt(LocalDateTime.now())
                .build();

        EmployeeStatutoryDetails saved = statutoryRepository.save(details);
        log.info("Saved statutory details for employee {}", employeeId);
        return saved;
    }

    // -------------------- Update Statutory Details --------------------
    /**
     * Updates existing statutory details or creates new if not present.
     *
     * @param employeeId the employee's UUID
     * @param dto        the DTO containing updated statutory details
     * @return updated EmployeeStatutoryDetails entity
     */
    @Transactional
    @Override
    public EmployeeStatutoryDetails updateStatutoryDetails(UUID employeeId, EmployeeStatutoryDetailsDTO dto) {
        EmployeeStatutoryDetails existingDetails = statutoryRepository
                .findByEmployee_EmployeeId(employeeId)
                .orElse(EmployeeStatutoryDetails.builder().build());

        // Ensure employee is always set
        if (existingDetails.getEmployee() == null) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            existingDetails.setEmployee(employee);
        }

        // Update only non-null fields
        if (dto.getPassportNumber() != null) existingDetails.setPassportNumber(dto.getPassportNumber());
        if (dto.getTaxRegime() != null) existingDetails.setTaxRegime(dto.getTaxRegime());
        if (dto.getPfUanNumber() != null) existingDetails.setPfUanNumber(dto.getPfUanNumber());
        if (dto.getEsiNumber() != null) existingDetails.setEsiNumber(dto.getEsiNumber());
        if (dto.getSsnNumber() != null) existingDetails.setSsnNumber(dto.getSsnNumber());

        existingDetails.setUpdatedAt(LocalDateTime.now());

        EmployeeStatutoryDetails updated = statutoryRepository.save(existingDetails);
        log.info("Updated statutory details for employee {}", employeeId);
        return updated;
    }

    // -------------------- Get Statutory Details --------------------
    /**
     * Fetches statutory details by statutory ID.
     *
     * @param statutoryId the statutory details ID
     * @return EmployeeStatutoryDetails entity
     */
    @Override
    public EmployeeStatutoryDetails getStatutoryDetailsById(UUID statutoryId) {
        return statutoryRepository.findById(statutoryId)
                .orElseThrow(() -> new RuntimeException("Statutory details not found"));
    }

    /**
     * Fetches statutory details by employee ID.
     *
     * @param employeeId the employee UUID
     * @return EmployeeStatutoryDetails entity or null
     */
    @Override
    public EmployeeStatutoryDetails getStatutoryDetailsByEmployeeId(UUID employeeId) {
        return statutoryRepository.findByEmployee_EmployeeId(employeeId).orElse(null);
    }

    // -------------------- Delete Statutory Details --------------------
    /**
     * Deletes statutory details by statutory ID.
     *
     * @param statutoryId the statutory details ID
     */
    @Override
    public void deleteStatutoryDetails(UUID statutoryId) {
        if (!statutoryRepository.existsById(statutoryId)) {
            throw new RuntimeException("Statutory details not found");
        }

        statutoryRepository.deleteById(statutoryId);
        log.info("Deleted statutory details with ID {}", statutoryId);
    }

    // -------------------- Convert Entity to DTO --------------------
    /**
     * Converts EmployeeStatutoryDetails entity to DTO.
     *
     * @param details EmployeeStatutoryDetails entity
     * @return EmployeeStatutoryDetailsDTO
     */
    @Override
    public EmployeeStatutoryDetailsDTO toDTO(EmployeeStatutoryDetails details) {
        if (details == null) return null;

        return EmployeeStatutoryDetailsDTO.builder()
                .statutoryId(details.getStatutoryId())
                .employeeId(details.getEmployee() != null ? details.getEmployee().getEmployeeId() : null)
                .passportNumber(details.getPassportNumber())
                .taxRegime(details.getTaxRegime())
                .pfUanNumber(details.getPfUanNumber())
                .esiNumber(details.getEsiNumber())
                .ssnNumber(details.getSsnNumber())
                .build();
    }
}
