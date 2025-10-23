package com.EmpTimeHub.dto;

import com.EmpTimeHub.constants.EnumConstants;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
public class HolidayCalendarDTO {
    private UUID holidayCalendarId;

    // for holidayCalendar
    private String holidayName;
    private String calendarDescription;
    private LocalDate holidayDate;
    private String locationRegion;
    private boolean isHolidayActive;
    private EnumConstants.HolidayType holidayType;
    private EnumConstants.RecurrenceRule recurrenceRule;
    private String calendarCountryCode;
    private UUID createdByAdminId;

}
