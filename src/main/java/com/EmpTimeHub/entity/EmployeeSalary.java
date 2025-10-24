package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "employee_salary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSalary {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "salary_id", updatable = false, nullable = false)
    private UUID salaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "basic_pay", nullable = false)
    private BigDecimal basicPay;

    @Column(name = "pay_type", nullable = false)
    private String payType;

    @Column(name = "standard_hours")
    private BigDecimal standardHours;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "pay_class")
    private String payClass;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "employeeSalary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeAllowance> allowances;

    @OneToMany(mappedBy = "employeeSalary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeDeduction> deductions;
}
