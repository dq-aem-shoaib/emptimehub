package com.EmpTimeHub.controller;

import com.EmpTimeHub.dto.TimeSheetResponseDto;
import com.EmpTimeHub.dto.WebResponseDTO;
import com.EmpTimeHub.entity.TimeSheet;
import com.EmpTimeHub.model.TimeSheetModel;
import com.EmpTimeHub.service.TimeSheetService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.EmpTimeHub.constants.EndpointConstants.*;

@RestController
@AllArgsConstructor
public class EmployeeController {

}
