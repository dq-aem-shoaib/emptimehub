package com.EmpTimeHub.dto;

import com.EmpTimeHub.constants.EnumConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDTO {
    private UUID userId;
    private String userName;
    private String email;
    private EnumConstants.Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String token;
    private LoginResponseDTO loginResponseDTO;

}
