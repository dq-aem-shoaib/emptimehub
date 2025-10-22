package com.EmpTimeHub.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationDTO {
    private UUID id;
    private String message;
    private UUID referenceId;
    private Boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

