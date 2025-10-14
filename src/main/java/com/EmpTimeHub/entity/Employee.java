package com.EmpTimeHub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "employee_id", updatable = false, nullable = false)
    private UUID employeeId;

    /**
     * Each employee is tied to one unique user account.
     * When the employee is deleted, the user can also be removed if needed (cascade = ALL optional).
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_employee_user")
    )
    private User user;

    /** Optional relationship to Client — can be NULL */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "client_id",
            foreignKey = @ForeignKey(name = "fk_employee_client")
    )
    private Client client;

    /** Each employee has unique bank details */
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "bank_account_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_employee_bank")
    )
    private BankDetails bankDetails;

    /** Self-referencing relationship — an employee can have a reporting manager */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reporting_manager_id",
            foreignKey = @ForeignKey(name = "fk_employee_reporting_manager")
    )
    private Employee reportingManager;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "personal_email", nullable = false, length = 100)
    private String personalEmail;

    @Column(name = "company_email", nullable = false, length = 100)
    private String companyEmail;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "rate_card", precision = 10, scale = 2)
    private BigDecimal rateCard = BigDecimal.ZERO;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "available_leaves", precision = 5)
    private Integer availableLeaves;

    @Column(name = "aadhar_number", length = 20)
    private String aadharNumber;

    // File URLs for documents
    @Column(name = "pan_card_url", length = 255)
    private String panCardUrl;

    @Column(name = "aadhar_card_url", length = 255)
    private String aadharCardUrl;

    @Column(name = "bank_passbook_url", length = 255)
    private String bankPassbookUrl;

    @Column(name = "tenth_cft_url", length = 255)
    private String tenthCftUrl;

    @Column(name = "inter_cft_url", length = 255)
    private String interCftUrl;

    @Column(name = "degree_cft_url", length = 255)
    private String degreeCftUrl;

    @Column(name = "post_graduation_cft_url", length = 255)
    private String postGraduationCftUrl;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "ACTIVE"; // ACTIVE | INACTIVE

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TimeSheet> timeSheets;
}
