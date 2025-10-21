package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.HolidayCalendarDTO;
import com.EmpTimeHub.dto.HolidaySchemeDTO;
import com.EmpTimeHub.model.HolidayCalendarModel;
import com.EmpTimeHub.model.HolidaySchemeModel;

import java.util.List;
import java.util.UUID;

public interface HolidayCalendarManagement {
    public String createHolidayCalendar(HolidayCalendarModel model, String loggedInUser);
    public String createHolidayScheme(HolidaySchemeModel model,  String loggedInUser);
    public HolidayCalendarDTO getHolidayCalendarById(UUID holidayCalendarId);
    public HolidaySchemeDTO getHolidaySchemeById(UUID holidaySchemeId);
    public HolidayCalendarDTO assignHolidaySchemeToEmployee(HolidayCalendarModel model, UUID employeeId, String loggedInUser);
    public List<HolidayCalendarDTO> getAllHolidayCalendarsForAdmin();
    public List<HolidaySchemeDTO> getAllHolidaySchemesForAdmin();
    public void updateHolidayCalendar(UUID holidayId ,HolidayCalendarModel model, String loggedInUser);
    public void updateHolidayScheme(UUID schemeId, HolidaySchemeModel model, String loggedInUser);
    public void deleteHolidayCalendar(UUID holidayId, String loggedInUser);
    public void deleteHolidayScheme(UUID schemeId,  String loggedInUser);
}
