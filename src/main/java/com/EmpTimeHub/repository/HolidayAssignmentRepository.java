package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.HolidayAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HolidayAssignmentRepository extends JpaRepository<HolidayAssignment, UUID> {

    public List<HolidayAssignment> findByEmployee(Employee employee);
}
