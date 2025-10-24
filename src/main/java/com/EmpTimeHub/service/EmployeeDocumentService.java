package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.EmployeeDocumentDTO;
import com.EmpTimeHub.entity.EmployeeDocument;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface EmployeeDocumentService {
    List<EmployeeDocument> getDocumentsByEmployeeId(UUID employeeId);

    @Transactional
    EmployeeDocument saveDocument(EmployeeDocumentDTO doc , UUID employeeID);

    @Transactional
    void deleteDocument(UUID employeeId , UUID documentId);


    EmployeeDocument getDocumentByType(UUID employeeId, String docType);

    void updateDocumentById(UUID documentId, UUID employeeId, EmployeeDocumentDTO dto);
}
