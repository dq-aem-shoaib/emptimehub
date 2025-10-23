package com.EmpTimeHub.entity;

import com.EmpTimeHub.constants.EnumConstants;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="holiday_calendar")
public class HolidayCalendar {

        @Id
        @GeneratedValue(generator = "uuid2")
        @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(name = "holiday_id", columnDefinition = "UUID")
        private UUID holidayId;

        @Column(name = "holiday_name", nullable = false)
        private String holidayName;

        @Column(name = "holiday_date", nullable = false)
        private LocalDate holidayDate;

        @Enumerated(EnumType.STRING)
        @Column(name = "holiday_type", nullable = false)
        private EnumConstants.HolidayType holidayType;

        @Column(name = "location_region")
        private String locationRegion;

        @Column(columnDefinition = "TEXT")
        private String description;

        @Enumerated(EnumType.STRING)
        @Column(name = "recurrence_rule", columnDefinition = "ENUM('ANNUAL','ONE_TIME')")
        private EnumConstants.RecurrenceRule recurrenceRule;

        @Column(name = "country_code", length = 10)
        private String countryCode;

        @Column(name = "active_status")
        private boolean activeStatus;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "created_by")
        private Admin createdBy;

        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

}
