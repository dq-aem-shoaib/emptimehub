package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.HolidayScheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HolidaySchemeRepository extends JpaRepository<HolidayScheme, UUID> {

}
