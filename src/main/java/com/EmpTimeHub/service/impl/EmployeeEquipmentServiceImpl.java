package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.dto.EmployeeEquipmentDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.EmployeeEquipment;
import com.EmpTimeHub.repository.EmployeeEquipmentRepository;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.service.EmployeeEquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeEquipmentServiceImpl implements EmployeeEquipmentService {

    private final EmployeeEquipmentRepository equipmentRepository;
    private final EmployeeRepository employeeRepository;

    /** Save equipment details for an employee */
    @Transactional
    @Override
    public EmployeeEquipment saveEquipment(EmployeeEquipmentDTO dto, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeEquipment equipment = EmployeeEquipment.builder()
                .employee(employee)
                .equipmentType(dto.getEquipmentType())
                .serialNumber(dto.getSerialNumber())
                .issuedDate(dto.getIssuedDate() != null ? dto.getIssuedDate() : LocalDate.now())
                .returnedDate(dto.getReturnedDate())
                .build();

        EmployeeEquipment saved = equipmentRepository.save(equipment);
        log.info("Saved equipment '{}' for employee {}", dto.getEquipmentType(), employeeId);
        return saved;
    }

    /** Update existing equipment details */
    @Transactional
    @Override
    public EmployeeEquipmentDTO updateEquipment(EmployeeEquipmentDTO dto, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeEquipment equipment;

        // If DTO contains an equipmentId, update that specific record
        if (dto.getEquipmentId() != null) {
            equipment = equipmentRepository.findById(dto.getEquipmentId())
                    .orElseThrow(() -> new RuntimeException("Equipment not found for ID: " + dto.getEquipmentId()));
        } else {
            // Otherwise, create a new one
            equipment = new EmployeeEquipment();
            equipment.setEmployee(employee);
        }

        // Update fields
        if (dto.getEquipmentType() != null)
            equipment.setEquipmentType(dto.getEquipmentType());
        if (dto.getSerialNumber() != null)
            equipment.setSerialNumber(dto.getSerialNumber());
        if (dto.getIssuedDate() != null)
            equipment.setIssuedDate(dto.getIssuedDate());
        if (dto.getReturnedDate() != null)
            equipment.setReturnedDate(dto.getReturnedDate());

        EmployeeEquipment saved = equipmentRepository.save(equipment);
        log.info("{} equipment record for employee {}",
                dto.getEquipmentId() != null ? "Updated" : "Added new", employeeId);

        return toDTO(saved);
    }

    /** Get equipment details by equipment ID */
    @Override
    public EmployeeEquipment getEquipmentById(UUID equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
    }

    /** Delete equipment record */
    @Override
    public void deleteEquipment(UUID equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) {
            throw new RuntimeException("Equipment not found");
        }
        equipmentRepository.deleteById(equipmentId);
        log.info("Deleted equipment with id {}", equipmentId);
    }

    /** Get equipment by employee ID */
    @Override
    public List<EmployeeEquipment> getEquipmentByEmployeeId(UUID employeeId) {
        return equipmentRepository.findByEmployee_EmployeeId(employeeId).orElse(null);
    }

    /** Convert entity to DTO */
    @Override
    public EmployeeEquipmentDTO toDTO(EmployeeEquipment equipment) {
        if (equipment == null) return null;

        return EmployeeEquipmentDTO.builder()
                .equipmentId(equipment.getEquipmentId())
                .equipmentType(equipment.getEquipmentType())
                .serialNumber(equipment.getSerialNumber())
                .issuedDate(equipment.getIssuedDate())
                .returnedDate(equipment.getReturnedDate())
                .build();
    }
}
