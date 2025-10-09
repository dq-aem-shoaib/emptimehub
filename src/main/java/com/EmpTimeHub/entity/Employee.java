package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee") // matches SQL table name
public class Employee {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "employee_id", updatable = false, nullable = false)
    private UUID employeeId;

    // Relationship to User (Many Employees can be linked to one User)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relationship to Client (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(length = 100)
    private String designation;

    @Column(name = "rate_card", precision = 10, scale = 2)
    private BigDecimal rateCard = BigDecimal.ZERO;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "available_leaves", precision = 5)
    private Integer availableLeaves;

    @Column(name = "aadhar_number", length = 20)
    private String aadharNumber;

    @Column(name = "account_number", length = 30)
    private String accountNumber;

    @Column(length = 20)
    private String status = "ACTIVE"; // ACTIVE | INACTIVE

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
