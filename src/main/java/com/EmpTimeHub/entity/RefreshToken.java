package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiryDate;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_session_id", nullable = false)
    private DeviceSession deviceSession;

    private Boolean isActive = true;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}