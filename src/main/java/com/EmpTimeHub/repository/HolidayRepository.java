package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByHolidayDateBetween(LocalDate start, LocalDate end);
}
