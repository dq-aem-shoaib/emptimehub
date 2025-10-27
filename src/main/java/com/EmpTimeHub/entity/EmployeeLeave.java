package com.EmpTimeHub.entity;

import com.EmpTimeHub.constants.EnumConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employee_leave")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeave {

    @Id
    @GeneratedValue
    @Column(name = "leave_id")
    private UUID leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id")
    private Employee reportingManager;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_category", nullable = false, length = 50)
    private EnumConstants.LeaveCategory leaveCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_type", nullable = false, length = 50)
    private EnumConstants.FinancialType financialType;


    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(length = 255)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumConstants.LeaveStatus status = EnumConstants.LeaveStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String approverComment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "leave_duration", nullable = false)
    private Double leaveDuration;

    @Column(name = "holidays", nullable = false)
    private Integer holidays;

    @Column(name = "partial_day", nullable = false)
    private Boolean partialDay = false;

    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;

    @Column(name = "withdrawn", nullable = false)
    private Boolean withdrawn = false;

    @Column(name = "policy_violation", nullable = false)
    private Boolean policyViolation = false;

    @Column(columnDefinition = "TEXT")
    private String violationReason;

    @Column(name = "notice_period_violation", nullable = false)
    private Boolean noticePeriodViolation = false;

    @Column(name = "approver_name", length = 255)
    private String approverName;
}
