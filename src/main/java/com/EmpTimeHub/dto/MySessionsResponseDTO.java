package com.EmpTimeHub.dto;


import com.EmpTimeHub.entity.DeviceSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MySessionsResponseDTO {
    private List<DeviceSessionDTO> activeSessions;
    private List<DeviceSessionDTO> loggedOutSessions;

    public static MySessionsResponseDTO from(List<DeviceSession> sessions) {
        List<DeviceSessionDTO> active = new ArrayList<>();
        List<DeviceSessionDTO> loggedOut = new ArrayList<>();
        for (DeviceSession session : sessions) {
            DeviceSessionDTO dto = DeviceSessionDTO.from(session);
            if (session.getLogoutTime() == null) active.add(dto);
            else loggedOut.add(dto);
        }
        return MySessionsResponseDTO.builder()
                .activeSessions(active)
                .loggedOutSessions(loggedOut)
                .build();
    }
}

