package com.EmpTimeHub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "entity_address")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityAddress {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "address_type")
    private String addressType; // HOME, OFFICE, BILLING, etc.

    @Column(name = "entity_type")
    private String entityType; // CLIENT, EMPLOYEE

    @Column(name = "entity_id")
    private UUID entityId; // clientId or employeeId
}
