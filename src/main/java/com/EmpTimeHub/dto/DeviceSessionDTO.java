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
        return DeviceSessionDTO.builder()
                .deviceId(session.getDeviceId())
                .deviceName(session.getDeviceName())
                .ipAddress(session.getIpAddress())
                .userAgent(session.getUserAgent())
                .loginTime(session.getLoginTime())
                .logoutTime(session.getLogoutTime())
                .status(session.getIsActive()? "Active" : "De-Activate")
                .token(null) // populate if needed
                .build();
    }
}

