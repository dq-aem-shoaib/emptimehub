package com.EmpTimeHub.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HolidaySchemeModel {
    // for holidayScheme
    private String schemeName;
    private String schemeDescription;
    private String city;
    private String state;
    private String schemeCountryCode;
}
