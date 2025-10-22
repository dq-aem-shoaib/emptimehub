package com.EmpTimeHub.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
public class HolidaySchemeDTO {

    // for holidayScheme
    private UUID holidaySchemeId;
    private String schemeName;
    private String schemeDescription;
    private UUID createdByAdminId;
    private String city;
    private String state;
    private String schemeCountryCode;
    private boolean isSchemeActive;
    private LocalDateTime schemeCreateAt;
    private LocalDateTime schemeUpdateAt;

    // scheme-mapping with holidays
    private List<UUID> holidayCalendarId;

}
