package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, UUID>,
        JpaSpecificationExecutor<EmployeeLeave> {
}

