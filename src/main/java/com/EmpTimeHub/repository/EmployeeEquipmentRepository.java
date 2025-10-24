package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EmployeeEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeEquipmentRepository extends JpaRepository<EmployeeEquipment, UUID> {
    Optional<List<EmployeeEquipment>> findByEmployee_EmployeeId(UUID employeeId);

}
