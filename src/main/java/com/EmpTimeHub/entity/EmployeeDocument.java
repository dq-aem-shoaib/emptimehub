package com.EmpTimeHub.entity;

import com.EmpTimeHub.constants.EnumConstants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee_documents")
public class EmployeeDocument {

    @Id
    @GeneratedValue
    @Column(name = "document_id", updatable = false, nullable = false)
    private UUID documentId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false)
    private EnumConstants.DocumentType docType;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();

}