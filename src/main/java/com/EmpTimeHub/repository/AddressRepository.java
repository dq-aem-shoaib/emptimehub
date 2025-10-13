package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address , UUID> {
}
