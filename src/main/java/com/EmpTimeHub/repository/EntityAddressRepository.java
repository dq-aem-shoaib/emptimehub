package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EntityAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EntityAddressRepository extends JpaRepository<EntityAddress, UUID> {

    List<EntityAddress> findByEntityTypeAndEntityId(String entityType, UUID entityId);

}
