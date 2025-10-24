package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.dto.AllowanceDTO;
import com.EmpTimeHub.dto.DeductionDTO;
import com.EmpTimeHub.dto.EmployeeSalaryDTO;
import com.EmpTimeHub.entity.Employee;
import com.EmpTimeHub.entity.EmployeeAllowance;
import com.EmpTimeHub.entity.EmployeeDeduction;
import com.EmpTimeHub.entity.EmployeeSalary;
import com.EmpTimeHub.repository.EmployeeSalaryRepository;
import com.EmpTimeHub.service.EmployeeSalaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeSalaryServiceImpl implements EmployeeSalaryService {

    private final EmployeeSalaryRepository employeeSalaryRepository;

    /**
     * Saves salary details for the given employee.
     *
     * @param dto      salary details
     * @param employee employee entity
     * @return saved EmployeeSalary entity
     */
    @Override
    public EmployeeSalary saveSalaryDetails(EmployeeSalaryDTO dto, Employee employee) {
        if (dto == null) {
            log.warn("No salary details provided for employee {}", employee.getEmployeeId());
            return null;
        }

        // Build EmployeeSalary entity
        EmployeeSalary salary = EmployeeSalary.builder()
                .employee(employee)
                .basicPay(dto.getBasicPay())
                .payType(dto.getPayType())
                .standardHours(dto.getStandardHours())
                .bankAccountNumber(dto.getBankAccountNumber())
                .ifscCode(dto.getIfscCode())
                .payClass(dto.getPayClass())
                .build();

        // Map allowances if provided
        if (dto.getAllowances() != null && !dto.getAllowances().isEmpty()) {
            salary.setAllowances(dto.getAllowances().stream()
                    .map(a -> EmployeeAllowance.builder()
                            .employeeSalary(salary)
                            .allowanceType(a.getAllowanceType())
                            .amount(a.getAmount())
                            .build())
                    .collect(Collectors.toList()));
        }

        // Map deductions if provided
        if (dto.getDeductions() != null && !dto.getDeductions().isEmpty()) {
            salary.setDeductions(dto.getDeductions().stream()
                    .map(d -> EmployeeDeduction.builder()
                            .employeeSalary(salary)
                            .deductionType(d.getDeductionType())
                            .amount(d.getAmount())
                            .build())
                    .collect(Collectors.toList()));
        }

        EmployeeSalary saved = employeeSalaryRepository.save(salary);
        log.info("Saved salary details for employee {}", employee.getEmployeeId());
        return saved;
    }

    /**
     * Retrieves salary details of a given employee.
     *
     * @param employee employee entity
     * @return EmployeeSalaryDTO with salary, allowances, and deductions
     */
    @Override
    public EmployeeSalaryDTO getSalaryDetailsForEmployee(Employee employee) {
        return employeeSalaryRepository.findByEmployeeEmployeeId(employee.getEmployeeId())
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * Updates existing salary details or creates a new record if not present.
     *
     * @param dto      salary details DTO
     * @param employee employee entity
     * @return updated EmployeeSalary entity
     */
    @Override
    public EmployeeSalary updateSalaryDetails(EmployeeSalaryDTO dto, Employee employee) {
        if (dto == null) {
            log.warn("No salary update provided for employee {}", employee.getEmployeeId());
            return null;
        }

        // Fetch existing salary or create new
        EmployeeSalary salary = employeeSalaryRepository.findByEmployeeEmployeeId(employee.getEmployeeId())
                .orElse(EmployeeSalary.builder().employee(employee).build());

        // Update basic salary fields if provided
        if (dto.getBasicPay() != null) salary.setBasicPay(dto.getBasicPay());
        if (dto.getPayType() != null) salary.setPayType(dto.getPayType());
        if (dto.getStandardHours() != null) salary.setStandardHours(dto.getStandardHours());
        if (dto.getBankAccountNumber() != null) salary.setBankAccountNumber(dto.getBankAccountNumber());
        if (dto.getIfscCode() != null) salary.setIfscCode(dto.getIfscCode());
        if (dto.getPayClass() != null) salary.setPayClass(dto.getPayClass());

        // ---------- Update Allowances ----------
        updateAllowances(salary, dto);

        // ---------- Update Deductions ----------
        updateDeductions(salary, dto);

        EmployeeSalary updatedSalary = employeeSalaryRepository.save(salary);
        log.info("Updated salary details for employee {}", employee.getEmployeeId());
        return updatedSalary;
    }

    /**
     * Converts EmployeeSalary entity to EmployeeSalaryDTO.
     *
     * @param salary EmployeeSalary entity
     * @return EmployeeSalaryDTO
     */
    private EmployeeSalaryDTO toDTO(EmployeeSalary salary) {
        return EmployeeSalaryDTO.builder()
                .basicPay(salary.getBasicPay())
                .payType(salary.getPayType())
                .standardHours(salary.getStandardHours())
                .bankAccountNumber(salary.getBankAccountNumber())
                .ifscCode(salary.getIfscCode())
                .payClass(salary.getPayClass())
                .allowances(salary.getAllowances() == null ? null :
                        salary.getAllowances().stream()
                                .map(a -> AllowanceDTO.builder()
                                        .allowanceType(a.getAllowanceType())
                                        .amount(a.getAmount())
                                        .build())
                                .collect(Collectors.toList()))
                .deductions(salary.getDeductions() == null ? null :
                        salary.getDeductions().stream()
                                .map(d -> DeductionDTO.builder()
                                        .deductionType(d.getDeductionType())
                                        .amount(d.getAmount())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }

    /**
     * Updates or adds allowances to the EmployeeSalary entity.
     *
     * @param salary EmployeeSalary entity
     * @param dto    EmployeeSalaryDTO containing allowance updates
     */
    private void updateAllowances(EmployeeSalary salary, EmployeeSalaryDTO dto) {
        if (dto.getAllowances() == null || dto.getAllowances().isEmpty()) return;

        if (salary.getAllowances() == null) {
            salary.setAllowances(new ArrayList<>());
        }

        for (AllowanceDTO a : dto.getAllowances()) {
            EmployeeAllowance existing = salary.getAllowances().stream()
                    .filter(e -> e.getAllowanceType().equalsIgnoreCase(a.getAllowanceType()))
                    .findFirst().orElse(null);

            if (existing != null) {
                existing.setAmount(a.getAmount());
            } else {
                EmployeeAllowance allowance = EmployeeAllowance.builder()
                        .employeeSalary(salary)
                        .allowanceType(a.getAllowanceType())
                        .amount(a.getAmount())
                        .build();
                salary.getAllowances().add(allowance);
            }
        }
    }

    /**
     * Updates or adds deductions to the EmployeeSalary entity.
     *
     * @param salary EmployeeSalary entity
     * @param dto    EmployeeSalaryDTO containing deduction updates
     */
    private void updateDeductions(EmployeeSalary salary, EmployeeSalaryDTO dto) {
        if (dto.getDeductions() == null || dto.getDeductions().isEmpty()) return;

        if (salary.getDeductions() == null) {
            salary.setDeductions(new ArrayList<>());
        }

        for (DeductionDTO d : dto.getDeductions()) {
            EmployeeDeduction existing = salary.getDeductions().stream()
                    .filter(e -> e.getDeductionType().equalsIgnoreCase(d.getDeductionType()))
                    .findFirst().orElse(null);

            if (existing != null) {
                existing.setAmount(d.getAmount());
            } else {
                EmployeeDeduction deduction = EmployeeDeduction.builder()
                        .employeeSalary(salary)
                        .deductionType(d.getDeductionType())
                        .amount(d.getAmount())
                        .build();
                salary.getDeductions().add(deduction);
            }
        }
    }
}
