package com.EmpTimeHub.service;


import com.EmpTimeHub.dto.ApiResponse;
import com.EmpTimeHub.dto.DeviceSessionDTO;
import com.EmpTimeHub.dto.LoginDTO;

public interface AuthenticationService {
    ApiResponse<?> authenticateUser(LoginDTO loginRequest, DeviceSessionDTO dto);
}
