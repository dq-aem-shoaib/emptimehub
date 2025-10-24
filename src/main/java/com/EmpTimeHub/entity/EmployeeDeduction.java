package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "employee_deductions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDeduction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "deduction_id", updatable = false, nullable = false)
    private UUID deductionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    private EmployeeSalary employeeSalary;

    @Column(name = "deduction_type", nullable = false)
    private String deductionType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
