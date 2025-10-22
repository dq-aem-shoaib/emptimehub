package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.HolidayCalendarDTO;
import com.EmpTimeHub.dto.HolidaySchemeDTO;
import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.exceptions.customExceptions.UserNotFoundException;
import com.EmpTimeHub.model.HolidayCalendarModel;
import com.EmpTimeHub.model.HolidaySchemeModel;
import com.EmpTimeHub.repository.*;
import com.EmpTimeHub.service.HolidayCalendarManagement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mohammad Shoaib
 * @since 2025-10-21
 * <p>
 * <p>
 * Service implementation for managing Holiday Calendars and Schemes.
 */
@AllArgsConstructor
@Slf4j
@Service
public class HolidayCalendarManagementImpl implements HolidayCalendarManagement {

    private HolidayCalendarRepository calendarRepository;
    private HolidayAssignmentRepository assignmentRepository;
    private HolidaySchemeMappingRepository schemeMappingRepository;
    private HolidaySchemeRepository schemeRepository;
    private AdminRepository adminRepository;
    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;
    private ClientRepository clientRepository;

    /**
     * Creates a new Holiday Calendar.
     */
    @Override
    public String createHolidayCalendar(HolidayCalendarModel holidayModel, String loggedInUser) {
        log.info("Creating Holiday Calendar for user: {}", loggedInUser);
        User user = userRepository.findByCompanyEmail(loggedInUser).orElseThrow(() -> {
            log.error("User not found with email: {}", loggedInUser);
            return new UserNotFoundException("User Not Found!");
        });

        Admin admin = adminRepository.findByUser_UserId(user.getUserId()).orElseThrow(() -> {
            log.error("Admin not found with user ID: {}", user.getUserId());
            return new UserNotFoundException("Admin is not found with user id: " + user.getUserId());
        });

        if (!EnumConstants.Role.ADMIN.name().equals(user.getRole().name())) {
            log.warn("Access denied for user: {}", loggedInUser);
            throw new RuntimeException("Only admin has access");
        }

        HolidayCalendar holidayCalendar = new HolidayCalendar();
        holidayCalendar.setHolidayName(holidayModel.getHolidayName());
        holidayCalendar.setDescription(holidayModel.getCalendarDescription());
        holidayCalendar.setHolidayDate(holidayModel.getHolidayDate());
        holidayCalendar.setCreatedBy(admin);
        holidayCalendar.setActiveStatus(true);
        holidayCalendar.setLocationRegion(holidayModel.getLocationRegion());
        holidayCalendar.setRecurrenceRule(holidayModel.getRecurrenceRule());
        holidayCalendar.setCountryCode(holidayModel.getCalendarCountryCode());
        holidayCalendar.setHolidayType(holidayModel.getHolidayType());
        holidayCalendar.setCreatedAt(LocalDateTime.now());
        holidayCalendar.setUpdatedAt(LocalDateTime.now());

        calendarRepository.save(holidayCalendar);
        log.info("Successfully saved Holiday Calendar: {}", holidayCalendar.getHolidayName());
        return "Successfully HolidayCalendar is saved";
    }

    /**
     * Creates a new Holiday Scheme and maps it to a holiday calendar if required.
     */
    @Override
    public String createHolidayScheme(HolidaySchemeModel schemeModel, String loggedInUser) {
        log.info("Creating Holiday Scheme by user: {}", loggedInUser);

        User user = userRepository.findByCompanyEmail(loggedInUser).orElseThrow(() ->
                new UserNotFoundException("User Not Found!"));

        Admin admin = adminRepository.findByUser_UserId(user.getUserId()).orElseThrow(() ->
                new UserNotFoundException("Admin is not found with user id: " + user.getUserId()));

        if (!EnumConstants.Role.ADMIN.name().equals(user.getRole().name()))
            throw new RuntimeException("Only admin has access");

        HolidayScheme scheme = HolidayScheme.builder()
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .city(schemeModel.getCity())
                .state(schemeModel.getState())
                .countryCode(schemeModel.getSchemeCountryCode())
                .schemeName(schemeModel.getSchemeName())
                .description(schemeModel.getSchemeDescription())
                .createdBy(admin)
                .activeStatus(true)
                .build();

        schemeRepository.save(scheme);
        log.info("Saved Holiday Scheme: {}", scheme.getSchemeName());

        HolidayCalendar holidayCalendar = calendarRepository.findById(schemeModel.getHolidayCalendarId()).orElseThrow(
                () -> new RuntimeException("Holiday is not found with id:" + schemeModel.getHolidayCalendarId()));

        if (EnumConstants.HolidayType.RELIGIOUS.equals(holidayCalendar.getHolidayType()) ||
                EnumConstants.HolidayType.REGIONAL.equals(holidayCalendar.getHolidayType()) ||
                EnumConstants.HolidayType.COMPANY_SPECIFIC.equals(holidayCalendar.getHolidayType())) {
            HolidaySchemeMapping schemeMapping = HolidaySchemeMapping.builder()
                    .holidayScheme(scheme)
                    .holidayCalendar(holidayCalendar)
                    .build();
            schemeMappingRepository.save(schemeMapping);
            log.info("Mapped Scheme '{}' to Calendar '{}'", scheme.getSchemeName(), holidayCalendar.getHolidayName());
        }

        return "Successfully HolidayScheme and SchemeMapping are saved";
    }

    /**
     * Returns Holiday Calendar by its ID.
     */
    @Override
    public HolidayCalendarDTO getHolidayCalendarById(UUID holidayCalendarId) {
        log.info("Fetching Holiday Calendar with ID: {}", holidayCalendarId);

        HolidayCalendar holidayCalendar = calendarRepository.findById(holidayCalendarId).orElseThrow(() ->
                new RuntimeException("Holiday Calendar Not found with id: " + holidayCalendarId));

        return HolidayCalendarDTO.builder()
                .holidayName(holidayCalendar.getHolidayName())
                .calendarDescription(holidayCalendar.getDescription())
                .holidayDate(holidayCalendar.getHolidayDate())
                .isHolidayActive(holidayCalendar.isActiveStatus())
                .calendarCountryCode(holidayCalendar.getCountryCode())
                .recurrenceRule(holidayCalendar.getRecurrenceRule())
                .build();
    }

    /**
     * Returns Holiday Scheme by its ID.
     */
    @Override
    public HolidaySchemeDTO getHolidaySchemeById(UUID holidaySchemeId) {
        log.info("Fetching Holiday Scheme with ID: {}", holidaySchemeId);

        HolidayScheme holidayScheme = schemeRepository.findById(holidaySchemeId).orElseThrow(() ->
                new RuntimeException("Holiday Scheme Not found with id: " + holidaySchemeId));
        return HolidaySchemeDTO.builder()
                .schemeName(holidayScheme.getSchemeName())
                .schemeDescription(holidayScheme.getDescription())
                .isSchemeActive(holidayScheme.isActiveStatus())
                .city(holidayScheme.getCity())
                .state(holidayScheme.getState())
                .schemeCountryCode(holidayScheme.getCountryCode())
                .createdByAdminId(holidayScheme.getCreatedBy().getAdminId())
                .build();
    }

    /**
     * Assigns a Holiday Scheme to an Employee.
     */
    @Override
    public HolidayCalendarDTO assignHolidaySchemeToEmployee(HolidayCalendarModel model, UUID employeeId, String loggedInUser) {
        // Not yet implemented
        log.warn("assignHolidaySchemeToEmployee() not implemented");
        return null;
    }

    /**
     * Fetches all Holiday Calendars for Admin.
     */
    @Override
    public List<HolidayCalendarDTO> getAllHolidayCalendarsForAdmin() {
        log.info("Fetching and sorting all Holiday Calendars based on 'Holiday date' for admin");
        return calendarRepository.findAll().stream().sorted(Comparator.comparing(HolidayCalendar::getHolidayDate))
                .map(calendar ->
                        HolidayCalendarDTO.builder()
                                .holidayCalendarId(calendar.getHolidayId())
                                .holidayName(calendar.getHolidayName())
                                .holidayType(calendar.getHolidayType())
                                .calendarCountryCode(calendar.getCountryCode())
                                .calendarDescription(calendar.getDescription())
                                .locationRegion(calendar.getLocationRegion())
                                .isHolidayActive(calendar.isActiveStatus())
                                .holidayDate(calendar.getHolidayDate())
                                .recurrenceRule(calendar.getRecurrenceRule())
                                .createdByAdminId(calendar.getCreatedBy().getAdminId())
                                .build()
                ).collect(Collectors.toList());
    }

    /**
     * Fetches all Holiday Schemes for Admin.
     */
    @Override
    public List<HolidaySchemeDTO> getAllHolidaySchemesForAdmin() {
        log.info("Fetching and sorting all Holiday Schemes based on 'country Code' for admin");

        return schemeRepository.findAll().stream().sorted(Comparator.comparing(HolidayScheme::getCountryCode,
                Comparator.nullsLast(String::compareTo))).map(scheme -> {
            List<HolidaySchemeMapping> mapping = schemeMappingRepository.findByHolidayScheme_SchemeId(scheme.getSchemeId());
            List<UUID> ids = mapping.stream().map(HolidaySchemeMapping::getId).collect(Collectors.toList());

            return HolidaySchemeDTO.builder()
                    .holidaySchemeId(scheme.getSchemeId())
                    .schemeName(scheme.getSchemeName())
                    .schemeDescription(scheme.getDescription())
                    .isSchemeActive(scheme.isActiveStatus())
                    .city(scheme.getCity())
                    .state(scheme.getState())
                    .schemeCountryCode(scheme.getCountryCode())
                    .createdByAdminId(scheme.getCreatedBy().getAdminId())
                    .schemeCreateAt(scheme.getCreatedAt())
                    .schemeUpdateAt(scheme.getUpdatedAt())
                    .holidayCalendarId(ids)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Updates an existing Holiday Calendar.
     */
    @Override
    public void updateHolidayCalendar(UUID holidayId, HolidayCalendarModel model, String loggedInUser) {
        log.info("Updating Holiday Calendar with ID: {}", holidayId);

        HolidayCalendar holidayCalendar = calendarRepository.findById(holidayId).orElseThrow(() ->
                new RuntimeException("Holiday Calendar not found with id: " + holidayId));

        if (model.getHolidayName() != null) holidayCalendar.setHolidayName(model.getHolidayName());
        if (model.getCalendarDescription() != null) holidayCalendar.setDescription(model.getCalendarDescription());
        if (model.getHolidayType() != null) holidayCalendar.setHolidayType(model.getHolidayType());
        if (model.getLocationRegion() != null) holidayCalendar.setLocationRegion(model.getLocationRegion());
        if (model.getCalendarCountryCode() != null) holidayCalendar.setCountryCode(model.getCalendarCountryCode());
        if (model.getRecurrenceRule() != null) holidayCalendar.setRecurrenceRule(model.getRecurrenceRule());
        if (model.getHolidayDate() != null) holidayCalendar.setHolidayDate(model.getHolidayDate());

        if(!holidayCalendar.isActiveStatus()){
            holidayCalendar.setActiveStatus(model.isActiveStatus());
        }
        holidayCalendar.setUpdatedAt(LocalDateTime.now());
        log.info("Holiday Calendar updated: {}", holidayCalendar.getHolidayName());
        calendarRepository.save(holidayCalendar);
    }

    /**
     * Updates an existing Holiday Scheme.
     */
    @Override
    public void updateHolidayScheme(UUID schemeId, HolidaySchemeModel schemeModel, String loggedInUser) {
        log.info("Updating Holiday Scheme with ID: {}", schemeId);

        HolidayScheme holidayScheme = schemeRepository.findById(schemeId).orElseThrow(() ->
                new RuntimeException("Holiday Scheme not found with id: " + schemeId));

        if (schemeModel.getSchemeName() != null) holidayScheme.setSchemeName(schemeModel.getSchemeName());
        if (schemeModel.getSchemeDescription() != null)
            holidayScheme.setDescription(schemeModel.getSchemeDescription());
        if (schemeModel.getCity() != null) holidayScheme.setCity(schemeModel.getCity());
        if (schemeModel.getState() != null) holidayScheme.setState(schemeModel.getState());
        if (schemeModel.getSchemeCountryCode() != null)
            holidayScheme.setCountryCode(schemeModel.getSchemeCountryCode());

        if(!holidayScheme.isActiveStatus()){
            holidayScheme.setActiveStatus(true);
        }
        holidayScheme.setUpdatedAt(LocalDateTime.now());
        log.info("Holiday Scheme updated: {}", holidayScheme.getSchemeName());
        schemeRepository.save(holidayScheme);

        HolidayCalendar holidayCalendar = calendarRepository.findById(schemeModel.getHolidayCalendarId()).orElseThrow(
                () -> new RuntimeException("Holiday is not found with id:" + schemeModel.getHolidayCalendarId()));

        if (EnumConstants.HolidayType.RELIGIOUS.equals(holidayCalendar.getHolidayType()) ||
                EnumConstants.HolidayType.REGIONAL.equals(holidayCalendar.getHolidayType()) ||
                EnumConstants.HolidayType.COMPANY_SPECIFIC.equals(holidayCalendar.getHolidayType())) {
            HolidaySchemeMapping schemeMapping = HolidaySchemeMapping.builder()
                    .holidayScheme(holidayScheme)
                    .holidayCalendar(holidayCalendar)
                    .build();
            schemeMappingRepository.save(schemeMapping);
            log.info("Mapped Scheme '{}' to Calendar '{}' in table", holidayScheme.getSchemeName(), holidayCalendar.getHolidayName());
        }
    }

    /**
     * Soft deletes a Holiday Calendar.
     */
    @Override
    public void deleteHolidayCalendar(UUID holidayId, String loggedInUser) {
        log.info("Soft deleting Holiday Calendar with ID: {}", holidayId);

        HolidayCalendar holidayCalendar = calendarRepository.findById(holidayId).orElseThrow(() ->
                new RuntimeException("Holiday Calendar not found with id: " + holidayId));

        User user = userRepository.findByCompanyEmail(loggedInUser).orElseThrow(() ->
                new UserNotFoundException("User Not Found!"));

        if (!EnumConstants.Role.ADMIN.name().equals(user.getRole().name()))
            throw new RuntimeException("Only admin has access");

        holidayCalendar.setActiveStatus(false);
        log.info("Holiday Calendar marked as inactive: {}", holidayCalendar.getHolidayName());
        calendarRepository.save(holidayCalendar);
    }

    /**
     * Soft deletes a Holiday Scheme.
     */
    @Override
    public void deleteHolidayScheme(UUID schemeId, String loggedInUser) {
        log.info("Soft deleting Holiday Scheme with ID: {}", schemeId);

        User user = userRepository.findByCompanyEmail(loggedInUser).orElseThrow(() ->
                new UserNotFoundException("User Not Found!"));

        if (!EnumConstants.Role.ADMIN.name().equals(user.getRole().name()))
            throw new RuntimeException("Only admin has access");

        HolidayScheme holidayScheme = schemeRepository.findById(schemeId).orElseThrow(() ->
                new RuntimeException("Holiday Scheme is not found: " + schemeId));

        holidayScheme.setActiveStatus(false);
        log.info("Holiday Scheme marked as inactive: {}", holidayScheme.getSchemeName());
        schemeRepository.save(holidayScheme);
        if(holidayScheme.getSchemeId() != null ) {
             schemeMappingRepository.findByHolidayScheme_SchemeId(holidayScheme.getSchemeId()).forEach(
                     mapping -> schemeMappingRepository.deleteById(mapping.getId()));
        }
    }
}
