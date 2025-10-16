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

    @Column(name = "leave_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EnumConstants.LeaveType type;

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
    private String managerComment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();


    @Column(name = "working_days", nullable = false)
    private Integer workingDays;

    @Column(name = "holidays", nullable = false)
    private Integer holidays;
}
