package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, UUID> {

    // Find all documents for an employee
    List<EmployeeDocument> findByEmployeeId(UUID employeeId);

    // Optional: find document by employee and type
    EmployeeDocument findByEmployeeIdAndDocType(UUID employeeId, String docType);

}
