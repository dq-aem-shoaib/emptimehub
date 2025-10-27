package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "employee_allowances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAllowance {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "allowance_id", updatable = false, nullable = false)
    private UUID allowanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id", nullable = false)
    private EmployeeSalary employeeSalary;

    @Column(name = "allowance_type", nullable = false)
    private String allowanceType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
}
