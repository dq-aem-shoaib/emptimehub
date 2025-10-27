package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employee_statutory_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeStatutoryDetails {

    @Id
    @GeneratedValue
    @Column(name = "statutory_id", columnDefinition = "UUID")
    private UUID statutoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "passport_number", length = 20)
    private String passportNumber;

    @Column(name = "tax_regime", length = 10) // OLD / NEW
    private String taxRegime;

    @Column(name = "pf_uan_number", length = 50)
    private String pfUanNumber;

    @Column(name = "esi_number", length = 50)
    private String esiNumber;

    @Column(name = "ssn_number", length = 50)
    private String ssnNumber; // for international hires

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
