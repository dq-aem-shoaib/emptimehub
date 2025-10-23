package com.EmpTimeHub.dto;

import com.EmpTimeHub.constants.EnumConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {
    private UUID leaveId;
    private EnumConstants.LeaveCategory categoryType;
    private  EnumConstants.FinancialType financialType;
    private Boolean partialDay;
    private Double leaveDuration;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String context;
    private MultipartFile attachmentFile;
}
