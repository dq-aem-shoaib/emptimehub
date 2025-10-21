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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
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


    @Override
    public String createHolidayCalendar(HolidayCalendarModel holidayModel, String loggedInUser) {
        User user = userRepository.findByCompanyEmail(loggedInUser).orElseThrow(() ->
                new UserNotFoundException("User Not Found!"));

        Admin admin = adminRepository.findByUser_UserId(user.getUserId()).orElseThrow(() ->
                new UserNotFoundException("Admin is not found with user id: " + user.getUserId()));

        if(!(user.getRole().name().equals(EnumConstants.Role.ADMIN.name())))
            throw new RuntimeException("Only admin have to access");

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

        if(holidayCalendar == null)
            return "scheme or schemeMapping is null";
        calendarRepository.save(holidayCalendar);
        return "Successfully HolidayCalendar is saved";
    }

    @Override
    public String createHolidayScheme(HolidaySchemeModel schemeModel, String loggedInUser) {
        User user = userRepository.findByCompanyEmail(loggedInUser).orElseThrow(() ->
                new UserNotFoundException("User Not Found!"));

        Admin admin = adminRepository.findByUser_UserId(user.getUserId()).orElseThrow(() ->
                new UserNotFoundException("Admin is not found with user id: " + user.getUserId()));

        if(!(user.getRole().name().equals(EnumConstants.Role.ADMIN.name())))
            throw new RuntimeException("Only admin have to access");

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
        if(scheme == null)
            return "scheme is null";

        HolidayCalendar holidayCalendar = calendarRepository.findByCountryCode(scheme.getCountryCode());
        HolidaySchemeMapping schemeMapping = HolidaySchemeMapping.builder()
                .holidayScheme(scheme)
                .holidayCalendar(holidayCalendar)
                .build();
        schemeRepository.save(scheme);
        schemeMappingRepository.save(schemeMapping);
        return "Successfully HolidayScheme and SchemeMapping are saved";
    }

    @Override
    public HolidayCalendarDTO getHolidayCalendarById(UUID holidayCalendarId){

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

    @Override
    public HolidaySchemeDTO getHolidaySchemeById(UUID holidaySchemeId){

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

    @Override
    public HolidayCalendarDTO assignHolidaySchemeToEmployee(HolidayCalendarModel model, UUID employeeId, String loggedInUser) {
        return null;
    }

    @Override
    public List<HolidayCalendarDTO> getAllHolidayCalendarsForAdmin() {

        List<HolidayCalendar> all = calendarRepository.findAll();
        List<HolidayCalendarDTO> dtos = all.stream().map((calendar) -> {
            return HolidayCalendarDTO.builder()
                    .holidayCalendarId(calendar.getHolidayId())
                    .holidayName(calendar.getHolidayName())
                    .calendarDescription(calendar.getDescription())
                    .locationRegion(calendar.getLocationRegion())
                    .isHolidayActive(calendar.isActiveStatus())
                    .holidayDate(calendar.getHolidayDate())
                    .recurrenceRule(calendar.getRecurrenceRule())
                    .createdByAdminId(calendar.getCreatedBy().getAdminId())
                    .build();
        }).collect(Collectors.toList());

        return dtos;
    }

    @Override
    public List<HolidaySchemeDTO> getAllHolidaySchemesForAdmin() {

        List<HolidayScheme> allSchemes = schemeRepository.findAll();
        List<HolidaySchemeDTO> dtos = allSchemes.stream().map((scheme) -> {

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
        return dtos;
    }

    @Override
    public void updateHolidayCalendar(UUID holidayId, HolidayCalendarModel model, String loggedInUser) {
        HolidayCalendar holidayCalendar = calendarRepository.findById(holidayId).orElseThrow(() ->
                new RuntimeException("Holiday Calendar not found with id: " + holidayId));

        if(model.getHolidayName() != null)
            holidayCalendar.setHolidayName(model.getHolidayName());
        if(model.getCalendarDescription() != null)
            holidayCalendar.setDescription(model.getCalendarDescription());
        if(model.getHolidayType() != null)
            holidayCalendar.setHolidayType(model.getHolidayType());
        if(model.getLocationRegion() != null)
            holidayCalendar.setLocationRegion(model.getLocationRegion());
        if(model.getCalendarCountryCode() != null)
            holidayCalendar.setCountryCode(model.getCalendarCountryCode());
        if(model.getRecurrenceRule() != null)
            holidayCalendar.setRecurrenceRule(model.getRecurrenceRule());
        if(model.getHolidayDate() != null)
            holidayCalendar.setHolidayDate(model.getHolidayDate());

        holidayCalendar.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    public void updateHolidayScheme(UUID schemeId, HolidaySchemeModel schemeModel, String loggedInUser) {
        HolidayScheme holidayScheme = schemeRepository.findById(schemeId).orElseThrow(() -> {
            return new RuntimeException("Holiday Scheme not found with id: " + schemeId);
        });

        if(schemeModel.getSchemeName() != null)
            holidayScheme.setSchemeName(schemeModel.getSchemeName());
        if(schemeModel.getSchemeDescription() != null)
            holidayScheme.setDescription(schemeModel.getSchemeDescription());
        if(schemeModel.getCity() != null)
            holidayScheme.setCity(schemeModel.getCity());
        if(schemeModel.getState() != null)
            holidayScheme.setState(schemeModel.getState());
        if(schemeModel.getSchemeCountryCode() != null)
            holidayScheme.setCountryCode(schemeModel.getSchemeCountryCode());

        holidayScheme.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    public void deleteHolidayCalendar(UUID holidayId, String loggedInUser) {
        HolidayCalendar holidayCalendar = calendarRepository.findById(holidayId).orElseThrow(() ->
                new RuntimeException("Holiday Calendar not found with id: " + holidayId));

        User user = userRepository.findByCompanyEmail(loggedInUser).orElseThrow(() ->
                new UserNotFoundException("User Not Found!"));

        if(!(user.getRole().name().equals(EnumConstants.Role.ADMIN.name())))
            throw new RuntimeException("Only admin have to access");

        holidayCalendar.setActiveStatus(false);
    }

    @Override
    public void deleteHolidayScheme(UUID schemeId, String loggedInUser) {
        User user = userRepository.findByCompanyEmail(loggedInUser).orElseThrow(() ->
                new UserNotFoundException("User Not Found!"));

        if(!(user.getRole().name().equals(EnumConstants.Role.ADMIN.name())))
            throw new RuntimeException("Only admin have to access");

        HolidayScheme holidayScheme = schemeRepository.findById(schemeId).orElseThrow(() ->
                new RuntimeException("Holiday Scheme is not found: " + schemeId));

        holidayScheme.setActiveStatus(false);
    }
    
}