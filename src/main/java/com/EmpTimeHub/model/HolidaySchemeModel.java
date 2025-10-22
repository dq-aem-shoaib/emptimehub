package com.EmpTimeHub.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HolidaySchemeModel {
    // for holidayScheme
    private UUID holidayCalendarId;
    private String schemeName;
    private String schemeDescription;
    private String city;
    private String state;
    private String schemeCountryCode;
    private boolean activeStatus;
}
