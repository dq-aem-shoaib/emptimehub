package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.TimeSheet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheet, UUID> {

    TimeSheet findByEmployee(Employee emp);

    Page<TimeSheet> findByEmployee(Employee employee, Pageable pageable);

    Page<TimeSheet> findByEmployeeAndWorkDateBetween(Employee employee, LocalDate startDate,
            LocalDate endDate, Pageable pageable);

}
