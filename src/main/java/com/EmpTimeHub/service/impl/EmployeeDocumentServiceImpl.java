package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.dto.EmployeeDocumentDTO;
import com.EmpTimeHub.entity.EmployeeDocument;
import com.EmpTimeHub.repository.EmployeeDocumentRepository;
import com.EmpTimeHub.service.EmployeeDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {

    private final EmployeeDocumentRepository repository;

    // -------------------- Fetch All Documents for Employee --------------------
    @Override
    public List<EmployeeDocument> getDocumentsByEmployeeId(UUID employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    // -------------------- Save New Document --------------------
    @Transactional
    @Override
    public EmployeeDocument saveDocument(EmployeeDocumentDTO documentDTO, UUID employeeId) {
        EmployeeDocument doc = EmployeeDocument.builder()
                .docType(documentDTO.getDocType())
                .employeeId(employeeId)
                .fileUrl(documentDTO.getFileUrl())
                .uploadedAt(LocalDateTime.now()) // Track upload time
                .build();

        return repository.save(doc);
    }

    // -------------------- Delete Document by ID --------------------
    @Transactional
    @Override
    public void deleteDocument(UUID employeeId, UUID documentId) {
        EmployeeDocument document = repository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Check if the document belongs to the employee
        if (!document.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("This document does not belong to the specified employee");
        }

        repository.delete(document);
    }


    // -------------------- Fetch Document by Type --------------------
    @Override
    public EmployeeDocument getDocumentByType(UUID employeeId, String docType) {
        return repository.findByEmployeeIdAndDocType(employeeId, docType);
    }

    // -------------------- Update Document by ID --------------------
    @Transactional
    @Override
    public void updateDocumentById(UUID documentId, UUID employeeId, EmployeeDocumentDTO dto) {
        // Fetch existing document or create new if not present
        EmployeeDocument employeeDocument = repository.findById(documentId)
                .orElseGet(() -> EmployeeDocument.builder()
                        .employeeId(employeeId)
                        .uploadedAt(LocalDateTime.now())
                        .build());

        // Update only non-null fields
        if (dto.getDocType() != null) employeeDocument.setDocType(dto.getDocType());
        if (dto.getFileUrl() != null) employeeDocument.setFileUrl(dto.getFileUrl());

        // Always update upload time
        employeeDocument.setUploadedAt(LocalDateTime.now());

        repository.save(employeeDocument);
    }
}
