package com.EmpTimeHub.constants;

public class EnumConstants {

    public enum Role {
        ADMIN,
        EMPLOYEE,
        CLIENT,
        MANAGER
    }
    // Enums for leave type and status
    public enum LeaveType {
        PAID, UNPAID, SICK, CASUAL
    }

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum WorkRequest{
        SUBMITED,APPROVED,PENDING,REJECTED;
    }

    public enum HolidayType {
        PUBLIC, RELIGIOUS, COMPANY
    }

    public enum PaymentStatus {
        PAID,
        UNPAID
    }
    public enum Designation { INTERN, TRAINEE, ASSOCIATE_ENGINEER, SOFTWARE_ENGINEER,
        SENIOR_SOFTWARE_ENGINEER, LEAD_ENGINEER, TEAM_LEAD, TECHNICAL_ARCHITECT,
        PROJECT_MANAGER, DELIVERY_MANAGER, DIRECTOR, VP_ENGINEERING, CTO, HR, FINANCE, OPERATIONS }

    public enum Gender { MALE, FEMALE, OTHER }

    public enum MaritalStatus { SINGLE, MARRIED, DIVORCED, WIDOWED }

    public enum Status { ACTIVE, INACTIVE }

    public enum AddressTypeEnum { PERMANENT, COMMUNICATION, OFFICE, EMERGENCY }


}