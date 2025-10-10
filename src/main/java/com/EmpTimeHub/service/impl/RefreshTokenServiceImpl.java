package com.EmpTimeHub.service.impl;
import com.EmpTimeHub.dto.DeviceSessionDTO;
import com.EmpTimeHub.dto.MySessionsResponseDTO;
import com.EmpTimeHub.entity.DeviceSession;
import com.EmpTimeHub.entity.RefreshToken;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.repository.DeviceSessionRepository;
import com.EmpTimeHub.repository.RefreshTokenRepository;
import com.EmpTimeHub.repository.UserRepository;
import com.EmpTimeHub.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final DeviceSessionRepository sessionRepo;
    private final RefreshTokenRepository tokenRepo;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, DeviceSessionDTO dto) {
        // Reuse or create new device session
        DeviceSession session = sessionRepo.findByUserAndDeviceId(user, dto.getDeviceId())
                .map(existing -> {
                    if (existing.getRefreshToken() != null) {
                        // Delete the existing token
                        tokenRepo.deleteByDeviceSession(existing);
                        // Break reference
                        existing.setRefreshToken(null);
                    }

                    existing.setIsActive(true);
                    existing.setLogoutTime(null);
                    existing.setUserAgent(dto.getUserAgent());
                    existing.setIpAddress(dto.getIpAddress());
                    existing.setDeviceName(dto.getDeviceName());
                    existing.setLastAccessed(Instant.now());

                    return sessionRepo.save(existing);
                })
                .orElseGet(() -> {
                    DeviceSession newSession = DeviceSession.builder()
                            .user(user)
                            .deviceId(dto.getDeviceId())
                            .deviceName(dto.getDeviceName())
                            .ipAddress(dto.getIpAddress())
                            .userAgent(dto.getUserAgent())
                            .isActive(true)
                            .lastAccessed(Instant.now())
                            .build();
                    return sessionRepo.save(newSession);
                });

//Create new refresh token (after deleting old one and clearing reference)
        RefreshToken newToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .deviceSession(session)
                .build();

//  Save new refresh token first
        tokenRepo.save(newToken);

// Then update the session with this new refresh token
        session.setRefreshToken(newToken);
        sessionRepo.save(session);

        return newToken;
    }

    @Override
    public boolean validateRefreshToken(String token) {
        return tokenRepo.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .filter(rt -> rt.getDeviceSession().getIsActive() != null && rt.getDeviceSession().getIsActive())
                .isPresent();
    }

    @Override
    public User getUserFromRefreshToken(String token) {
        return tokenRepo.findByToken(token)
                .map(rt -> rt.getDeviceSession().getUser())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

    @Override
    @Transactional
    public void logoutFromDevice(User user, String deviceId) {
        sessionRepo.findByUserAndDeviceId(user, UUID.fromString(deviceId)).ifPresent(session -> {
            session.setLogoutTime(LocalDateTime.now());      // log when user logs out
            session.setIsActive(false);                      // mark session inactive
            tokenRepo.deleteByDeviceSession(session);        // remove refresh token(s) for this session
            session.setRefreshToken(null);                   //  nullify back-reference (if bidirectional)
            sessionRepo.save(session);                       //  persist changes
        });
    }


    @Override
    @Transactional
    public void logoutFromAllDevices(User user) {
        List<DeviceSession> sessions = sessionRepo.findAllByUser(user);
        for (DeviceSession session : sessions) {
            session.setLogoutTime(LocalDateTime.now());
            session.setIsActive(false);
            tokenRepo.deleteByDeviceSession(session);
            session.setRefreshToken(null);
        }
        sessionRepo.saveAll(sessions);
    }

    @Override
    public MySessionsResponseDTO getUserSessions(User user) {
        List<DeviceSession> sessions = sessionRepo.findAllByUser(user);
        return MySessionsResponseDTO.from(sessions);
    }


    public boolean isSessionActive(String userKey, UUID deviceId) {
        User user = userRepository.findByCompanyEmail(userKey)
                .or(() -> userRepository.findByUserName(userKey))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return sessionRepo.findByUserAndDeviceId(user, deviceId)
                .filter(DeviceSession::getIsActive)
                .isPresent();
    }


}
