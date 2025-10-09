package com.EmpTimeHub.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_sessions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "device_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private UUID deviceId;

    private String deviceName;
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    private Instant createdAt;

    private Instant lastAccessed;
    private Boolean isActive;
    @Column(name = "login_time")
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;



    @OneToOne(mappedBy = "deviceSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RefreshToken refreshToken;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        lastAccessed = Instant.now();
        loginTime = LocalDateTime.now();
        isActive = true;
    }
}