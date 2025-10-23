package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.HolidayCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface HolidayCalendarRepository extends JpaRepository<com.EmpTimeHub.entity.HolidayCalendar, UUID> {
    List<HolidayCalendar> findByHolidayDateBetween(LocalDate start, LocalDate end);

}
