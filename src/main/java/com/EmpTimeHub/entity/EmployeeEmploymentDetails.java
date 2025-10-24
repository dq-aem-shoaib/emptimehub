package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employee_employment_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEmploymentDetails {

    @Id
    @GeneratedValue
    @Column(name = "employment_id", columnDefinition = "UUID")
    private UUID employmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "notice_period_duration", length = 20)
    private String noticePeriodDuration;

    @Column(name = "probation_applicable")
    private Boolean probationApplicable = false;

    @Column(name = "probation_duration", length = 20)
    private String probationDuration;

    @Column(name = "probation_notice_period", length = 20)
    private String probationNoticePeriod;

    @Column(name = "bond_applicable")
    private Boolean bondApplicable = false;

    @Column(name = "bond_duration", length = 20)
    private String bondDuration;

    @Column(name = "working_model", length = 20)
    private String workingModel;

    @Column(name = "shift_timing", length = 50)
    private String shiftTiming;

    @Column(length = 50)
    private String department;

    @Column(name = "date_of_confirmation")
    private LocalDate dateOfConfirmation;

    @Column(length = 100)
    private String location;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
