package com.EmpTimeHub.constants;

public class EnumConstants {

    public enum Role {
        ADMIN,
        EMPLOYEE,
        CLIENT
    }
    // Enums for leave type and status
    public enum LeaveType {
        PAID, UNPAID, SICK, CASUAL
    }

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum WorkRequest{
        SUBMITTED,APPROVED,PENDING,REJECTED;
    }

    public enum HolidayType {
        PUBLIC, RELIGIOUS, COMPANY
    }

}