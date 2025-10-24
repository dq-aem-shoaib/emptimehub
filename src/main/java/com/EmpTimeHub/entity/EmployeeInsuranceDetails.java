package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "employee_insurance_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeInsuranceDetails {

    @Id
    @GeneratedValue
    @Column(name = "insurance_id", columnDefinition = "UUID")
    private UUID insuranceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "policy_number", length = 50)
    private String policyNumber;

    @Column(name = "provider_name", length = 100)
    private String providerName;

    @Column(name = "coverage_start")
    private LocalDate coverageStart;

    @Column(name = "coverage_end")
    private LocalDate coverageEnd;

    @Column(name = "nominee_name", length = 100)
    private String nomineeName;

    @Column(name = "nominee_relation", length = 50)
    private String nomineeRelation;

    @Column(name = "nominee_contact", length = 20)
    private String nomineeContact;

    @Column(name = "group_insurance")
    private Boolean groupInsurance = false;

    // Use JSONB mapping for other benefits
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = com.EmpTimeHub.util.MapToJsonConverter.class)
    @Column(name = "other_benefits", columnDefinition = "jsonb")
    private Map<String, String> otherBenefits;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
