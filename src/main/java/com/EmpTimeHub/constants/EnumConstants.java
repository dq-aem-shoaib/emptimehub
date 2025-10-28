package com.EmpTimeHub.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class EnumConstants {

    public enum Role {
        ADMIN,
        EMPLOYEE,
        CLIENT,
        MANAGER
    }
    // Enums for leave type and status
    public enum FinancialType {
        PAID,
        UNPAID
    }

    public enum LeaveCategory {
        SICK,
        CASUAL,
        PLANNED,
        UNPLANNED
    }

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED,WITHDRAWN
    }

    public enum WorkRequest{
        SUBMITTED,APPROVED,PENDING,REJECTED;
    }

    public enum HolidayType {
        PUBLIC, RELIGIOUS, REGIONAL, COMPANY_SPECIFIC
    }

    public enum RecurrenceRule{
        ANNUAL, ONE_TIME;
    }
    public enum PaymentStatus {
        PAID,
        UNPAID
    }
    public enum Designation { INTERN, TRAINEE, ASSOCIATE_ENGINEER, SOFTWARE_ENGINEER,
        SENIOR_SOFTWARE_ENGINEER, LEAD_ENGINEER, TEAM_LEAD, TECHNICAL_ARCHITECT,
        REPORTING_MANAGER, DELIVERY_MANAGER, DIRECTOR, VP_ENGINEERING, CTO, HR, FINANCE, OPERATIONS }

    public enum Gender { MALE, FEMALE, OTHER }

    public enum MaritalStatus { SINGLE, MARRIED, DIVORCED, WIDOWED }

    public enum Status { ACTIVE, INACTIVE }

    public enum AddressTypeEnum { PERMANENT, COMMUNICATION, OFFICE, EMERGENCY }


    public enum DayType {
        WORKING_DAY,
        WEEKEND,
        HOLIDAY,
        LEAVE,
        LEAVE_AND_HOLIDAY,
        LEAVE_AND_WEEKEND
    }

    public enum EmploymentType {CONTRACTOR , FREELANCER , FULLTIME }

    @AllArgsConstructor
    @Getter
    public enum EntityType {
        CLIENT("CLIENT"),
        EMPLOYEE("EMPLOYEE");
        private final String value;

    }

    public enum MailTemplateType {
        LEAVE_SUBMITTED_MANAGER,
        LEAVE_SUBMITTED_EMPLOYEE,
        LEAVE_UPDATED_MANAGER,
        LEAVE_UPDATED_EMPLOYEE,
        LEAVE_APPROVED,
        LEAVE_REJECTED;
    }


    public enum DocumentType {
        OFFER_LETTER,
        CONTRACT,
        TAX_DECLARATION_FORM,
        WORK_PERMIT,
        OTHER
    }

}