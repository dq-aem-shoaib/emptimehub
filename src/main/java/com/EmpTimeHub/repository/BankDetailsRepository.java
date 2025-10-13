package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankDetailsRepository extends JpaRepository<BankDetails , UUID> {
}
