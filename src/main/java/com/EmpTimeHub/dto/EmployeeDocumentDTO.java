package com.EmpTimeHub.dto;

import com.EmpTimeHub.constants.EnumConstants;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EmployeeDocumentDTO {
        private UUID documentId;
        private EnumConstants.DocumentType docType;
        private String fileUrl;
}
