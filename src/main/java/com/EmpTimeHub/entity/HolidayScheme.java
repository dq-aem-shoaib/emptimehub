package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "holiday_scheme")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayScheme {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "scheme_id", columnDefinition = "UUID")
    private UUID schemeId;

    @Column(name = "scheme_name", nullable = false)
    private String schemeName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "active_status")
    private boolean activeStatus = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Admin createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "holidayScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HolidaySchemeMapping> holidayMappings;

    @OneToMany(mappedBy = "holidayScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HolidayAssignment> assignments;

}
