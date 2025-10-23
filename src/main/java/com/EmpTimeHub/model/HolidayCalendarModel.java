package com.EmpTimeHub.model;

import com.EmpTimeHub.constants.EnumConstants;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayCalendarModel {
    // for holidayCalendar
    private String holidayName;
    private String calendarDescription;
    private LocalDate holidayDate;
    private String locationRegion;
    private EnumConstants.HolidayType holidayType;
    private EnumConstants.RecurrenceRule recurrenceRule;
    private String calendarCountryCode;
    private boolean activeStatus;

}




