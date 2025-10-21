package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.*;
import com.EmpTimeHub.entity.RefreshToken;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.exceptions.customExceptions.auth.InvalidLoginException;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtServiceImpl  jwtServiceImpl ;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final EmployeeRepository employeeRepository;
    @Override
    @Transactional
    public ApiResponse<?> authenticateUser(LoginDTO loginRequest, DeviceSessionDTO dto) {

        String inputKey = loginRequest.getInputKey();
        String password = loginRequest.getPassword();


        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(inputKey, password)
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidLoginException("Invalid username/email or password.");
        }

        User user = userRepository.findByUserName(inputKey)
                        .orElseGet(() -> userRepository.findByCompanyEmail(inputKey)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                        "User not found by given ID, Mobile or Email ID: ")));

        String role = user.getRole().toString();

        String token = jwtServiceImpl.generateToken(user.getUserName(), List.of(role), dto.getDeviceId());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, dto);


        LocalDateTime expiryDate = refreshToken.getExpiryDate();

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .accessToken(token)
                .role(role)
                .refreshExpiresAt(expiryDate)
                .tokenType("Bearer")
                .refreshToken(refreshToken.getToken())
                .build();
        if (user.getRole().equals(EnumConstants.Role.EMPLOYEE)) {
            EmployeeLoginResponseDTO employeeLoginResponseDTO=new EmployeeLoginResponseDTO();

            employeeRepository.findByUser_UserId(user.getUserId()).ifPresentOrElse(employee -> {

                BeanUtils.copyProperties(employee , employeeLoginResponseDTO);
                if(employee.getClient()!=null) {
                    employeeLoginResponseDTO.setClientId(employee.getClient().getClientId());
                }
                employeeLoginResponseDTO.setResponseMessage("Employee login successful!");
                employeeLoginResponseDTO.setLoginResponseDTO(loginResponseDTO);
            }, () -> employeeLoginResponseDTO.setResponseMessage("Employee details not found for this user."));

       return  new ApiResponse<>(employeeLoginResponseDTO,"Employee details  found for this user.");
        }
        else if(user.getRole().equals(EnumConstants.Role.ADMIN)){
            UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
            BeanUtils.copyProperties(user , userLoginResponseDTO);
            userLoginResponseDTO.setLoginResponseDTO(loginResponseDTO);

            return  new ApiResponse<>(userLoginResponseDTO,"Admin details  found for this user.");
        }
        else if(user.getRole().equals(EnumConstants.Role.MANAGER)){
            UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
            BeanUtils.copyProperties(user , userLoginResponseDTO);
            userLoginResponseDTO.setLoginResponseDTO(loginResponseDTO);

            return  new ApiResponse<>(userLoginResponseDTO,"Manager details  found for this user.");
        }else {
            return  new ApiResponse<>(null,"User details not found for this user.");
        }


    }
}

