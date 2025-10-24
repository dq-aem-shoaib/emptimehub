package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.EmployeeEquipmentDTO;
import com.EmpTimeHub.entity.EmployeeEquipment;

import java.util.List;
import java.util.UUID;

public interface EmployeeEquipmentService {
    EmployeeEquipment saveEquipment(EmployeeEquipmentDTO dto, UUID employeeId);
    EmployeeEquipmentDTO updateEquipment(EmployeeEquipmentDTO dto , UUID employeeId);
    EmployeeEquipment getEquipmentById(UUID equipmentId);
    void deleteEquipment(UUID equipmentId);
    List<EmployeeEquipment> getEquipmentByEmployeeId(UUID employeeId);
    EmployeeEquipmentDTO toDTO(EmployeeEquipment equipment);
}
