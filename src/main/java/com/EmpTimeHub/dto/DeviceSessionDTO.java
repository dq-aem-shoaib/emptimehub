package com.EmpTimeHub.dto;


import com.EmpTimeHub.entity.DeviceSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSessionDTO {
    private UUID deviceId;
    private String deviceName;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String status;
    private TokenInfo token;


    @Builder
    public static DeviceSessionDTO from(DeviceSession session) {
        if (session == null) {
            return null;
        }

        return DeviceSessionDTO.builder()
                .deviceId(session.getDeviceId())
                .deviceName(session.getDeviceName() != null ? session.getDeviceName() : "Unknown Device")
                .ipAddress(session.getIpAddress() != null ? session.getIpAddress() : "Unknown IP")
                .userAgent(session.getUserAgent() != null ? session.getUserAgent() : "Unknown User-Agent")
                .loginTime(session.getLoginTime())
                .logoutTime(session.getLogoutTime())
                .status(Boolean.TRUE.equals(session.getIsActive()) ? "Active" : "De-Activate")
                .token(null) // populate if needed
                .build();
    }

}

