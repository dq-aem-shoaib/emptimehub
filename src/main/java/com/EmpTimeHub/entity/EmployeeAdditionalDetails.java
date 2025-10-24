package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee_additional_details")
public class EmployeeAdditionalDetails {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "additional_details_id", updatable = false, nullable = false)
    private UUID additionalDetailId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "offer_letter_url", length = 255)
    private String offerLetterUrl;

    @Column(name = "contract_url", length = 255)
    private String contractUrl;

    @Column(name = "tax_declaration_form_url", length = 255)
    private String taxDeclarationFormUrl;

    @Column(name = "work_permit_url", length = 255)
    private String workPermitUrl;

    @Column(name = "background_check_status", length = 50)
    private String backgroundCheckStatus = "PENDING"; // default

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "updated_at")
    @CreationTimestamp
    private LocalDateTime updatedAt;
}
