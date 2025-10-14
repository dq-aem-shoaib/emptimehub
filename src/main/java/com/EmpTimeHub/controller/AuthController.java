package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.*;
import com.EmpTimeHub.entity.RefreshToken;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.AuthenticationService;
import com.EmpTimeHub.service.impl.JwtServiceImpl;
import com.EmpTimeHub.service.impl.RefreshTokenServiceImpl;
import com.EmpTimeHub.service.impl.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static com.EmpTimeHub.constants.EndpointConstants.LOGIN;
import static com.EmpTimeHub.constants.EndpointConstants.REFRESH_TOKEN;

@RestController
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtService ;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private  final RefreshTokenServiceImpl refreshTokenService;
    @Qualifier("userDetailsServiceImpl")
    private final UserDetailsService userDetailsService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtServiceImpl jwtService,
            UserRepository userRepository,
            AuthenticationService authenticationService,
            @Qualifier("userDetailsServiceImpl") UserDetailsServiceImpl userDetailsService,
            RefreshTokenServiceImpl  refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService=refreshTokenService;
    }


    @PostMapping(LOGIN)
    @Operation(summary = "Login endpoint", description = "Accepts device headers for session tracking")
    public ResponseEntity<WebResponseDTO<ApiResponse<?>>> login(
            @ModelAttribute LoginDTO loginRequest,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceIdHeader,
            @RequestHeader(value = "X-Device-Name", required = false) String deviceNameHeader,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest request
    ) {
        // Get IP Address
        String ipAddress = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());

        log.info("Username and password,{},{}",loginRequest.getInputKey(),loginRequest.getPassword());

        // Parse or Generate UUID from headers
        UUID deviceId = deviceIdHeader != null
                ? UUID.fromString(deviceIdHeader)
                : UUID.nameUUIDFromBytes((userAgent + ipAddress).getBytes());

        // Parse device name if not sent
        String deviceName = deviceNameHeader != null
                ? deviceNameHeader
                : parseDeviceName(userAgent);

        // Build DTO
        DeviceSessionDTO deviceInfo = DeviceSessionDTO.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        // Auth service call
        ApiResponse<?> apiResponse = authenticationService.authenticateUser(loginRequest, deviceInfo);
            return ResponseEntity.ok(WebResponseDTO.<ApiResponse<?>>builder()
                    .flag(true)
                    .message("User details fetched successfully.")
                    .status(HttpStatus.OK.value())
                    .response(apiResponse)
                    .build());


    }

    private String parseDeviceName(String userAgent) {
        if (userAgent == null) return "Unknown Device";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone")) return "iPhone";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Macintosh")) return "Mac";
        return "Browser";
    }
    @PostMapping(REFRESH_TOKEN)
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO requestDTO,
                                                                @RequestHeader(value = "X-Device-Id", required = false) String deviceIdHeader,
                                                                @RequestHeader(value = "X-Device-Name", required = false) String deviceNameHeader,
                                                                @RequestHeader(value = "User-Agent", required = false) String userAgent, HttpServletRequest request) {
        if (!refreshTokenService.validateRefreshToken(requestDTO.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User user = refreshTokenService.getUserFromRefreshToken(requestDTO.getRefreshToken());


        String ipAddress = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());

        // Parse or Generate UUID from headers
        UUID deviceId = deviceIdHeader != null
                ? UUID.fromString(deviceIdHeader)
                : UUID.nameUUIDFromBytes((userAgent + ipAddress).getBytes());

        // Parse device name if not sent
        String deviceName = deviceNameHeader != null
                ? deviceNameHeader
                : parseDeviceName(userAgent);

        // Build DTO
        DeviceSessionDTO deviceInfo = DeviceSessionDTO.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();



        String accessToken = jwtService.generateToken(user.getUserName(), List.of(user.getRole().name()),deviceInfo.getDeviceId());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user, deviceInfo);

        return ResponseEntity.ok(RefreshTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .refreshExpiresAt(newRefreshToken.getExpiryDate().atZone(ZoneId.systemDefault()).toInstant())
                .build());
    }



    @PostMapping("/web/api/v1/auth/preset-device")
    @Operation(summary = "Login endpoint", description = "Accepts device headers for session tracking")

    public void presetDevice( @RequestHeader(value = "X-Device-Id", required = false) String deviceIdHeader,
                              @RequestHeader(value = "X-Device-Name", required = false) String deviceNameHeader,
                              @RequestHeader(value = "User-Agent", required = false) String userAgent,HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ipAddress = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());

        // Parse or Generate UUID from headers
        UUID deviceId = deviceIdHeader != null
                ? UUID.fromString(deviceIdHeader)
                : UUID.nameUUIDFromBytes((userAgent + ipAddress).getBytes());

        // Parse device name if not sent
        String deviceName = deviceNameHeader != null
                ? deviceNameHeader
                : parseDeviceName(userAgent);

        // Store device data in session (or cookie if stateless)
        request.getSession().setAttribute("X-Device-Id", deviceId);
        request.getSession().setAttribute("X-Device-Name", deviceName);
        request.getSession().setAttribute("User-Agent", userAgent);
        request.getSession().setAttribute("X-Forwarded-For", ipAddress);

        // Redirect to Google login
        response.sendRedirect("/oauth2/authorization/google");
    }

}

