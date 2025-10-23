package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.HolidaySchemeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HolidaySchemeMappingRepository extends JpaRepository<HolidaySchemeMapping, UUID> {
    public List<HolidaySchemeMapping> findByHolidayScheme_SchemeId(UUID schemeId);
}
