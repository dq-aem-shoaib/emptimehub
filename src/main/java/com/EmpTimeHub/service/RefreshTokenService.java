package com.EmpTimeHub.service;


import com.EmpTimeHub.dto.DeviceSessionDTO;
import com.EmpTimeHub.dto.MySessionsResponseDTO;
import com.EmpTimeHub.entity.RefreshToken;
import com.EmpTimeHub.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user, DeviceSessionDTO dto);
    boolean validateRefreshToken(String token);
    User getUserFromRefreshToken(String token);
    void logoutFromDevice(User user, String deviceId);
    void logoutFromAllDevices(User user);
    MySessionsResponseDTO getUserSessions(User user);
}
