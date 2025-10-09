package com.EmpTimeHub.repository;


import com.EmpTimeHub.entity.DeviceSession;
import com.EmpTimeHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceSessionRepository extends JpaRepository<DeviceSession, UUID> {
    Optional<DeviceSession> findByUserAndDeviceId(User user, UUID deviceId);
    List<DeviceSession> findAllByUser(User user);
}