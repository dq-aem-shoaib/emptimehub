package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee_equipment")
public class EmployeeEquipment {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "equipment_id", updatable = false, nullable = false)
    private UUID equipmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "equipment_type", length = 50)
    private String equipmentType; // Laptop, Phone, etc.

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "returned_date")
    private LocalDate returnedDate;
}
