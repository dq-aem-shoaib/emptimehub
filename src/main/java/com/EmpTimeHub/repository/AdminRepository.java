package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Admin;
import com.EmpTimeHub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {

    Optional<Admin> findByFullName(String fullName);

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByUser_UserId(UUID userId);

}
