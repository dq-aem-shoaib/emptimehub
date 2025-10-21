package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.ClientPoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientPocRepository extends JpaRepository<ClientPoc , UUID> {
}
