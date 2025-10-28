package com.EmpTimeHub.constants;

import static com.EmpTimeHub.constants.EnumConstants.DayType.HOLIDAY;

public class EndpointConstants {

    /**
     * API Prefixes
     */
    public static final String API_VERSION_1 = "v1";
    public static final String WEB_API_PREFIX = "/web/api/" + API_VERSION_1;

    /**
     * COMMON PATHS
     */
    public static final String VIEW = "/view";
    public static final String UPDATE = "/update";
    public static final String UNIQUE_CHECK = "/uniquecheck";
    public static final String DELETE = "/{id}";

    /**
     * Authentication Paths
     */
    public static final String AUTHENTICATION_API = WEB_API_PREFIX + "/auth";
    public static final String LOGIN = AUTHENTICATION_API + "/login";


    public static final String REFRESH_TOKEN = AUTHENTICATION_API + "/refreshToken";

    /**
     * Admin related endpoints
     */
    public static final String ADMIN_API = WEB_API_PREFIX + "/admin";
    public static final String APPROVE_USER = ADMIN_API + "/approve/user/{userId}";
    public static final String ADMIN_VIEW = ADMIN_API + VIEW;
    public static final String ADMIN_UPDATE_EMP = ADMIN_API + UPDATE+"emp/{empId}";
    public static final String ADMIN_UPDATE_CLIENT = ADMIN_API + UPDATE+"client/{clientId}";
    public static final String ADMIN_DELETE = ADMIN_API + DELETE;
    public static final String ADD_EMPLOYEE = ADMIN_API + "/add/employee";
    public static final String ADD_CLIENT = ADMIN_API + "/add/client";
    public static final String ADMIN_GET_EMP = ADMIN_API + "/emp"+"/{empId}";
    public static final String ADMIN_GET_ALL_EMP = ADMIN_API + "/emp/all";
    public static final String ADMIN_GET_CLIENT = ADMIN_API + "/client"+"/{clientId}";
    public static final String ADMIN_GET_ALL_CLIENT = ADMIN_API + "/client/all";
    public static final String ADMIN_DELETE_EMP = ADMIN_API+"/{empId}";
    public static final String ADMIN_DELETE_CLIENT = ADMIN_API+"/client/{clientId}";
    public static final String ADMIN_DELETE_EMP_ADDRESS = ADMIN_API+"/delete/employee/{employeeId}/address/{addressId}";
    public static final String ADMIN_DELETE_EMP_DOCUMENT = ADMIN_API+"/delete/employee/{employeeId}/document/{documentId}";
    public static final String ADMIN_UNASSIGN_CLIENT = ADMIN_API+"/emp/{empId}";
    public static final String ADD_CLIENT_ADDRESS = ADMIN_API+"/client/address/{clientId}";

    public static final String ADMIN_NAMES=ADMIN_API+"/getAllAdminNames";
    /**
     * User related endpoints
     */
    public static final String USER_API = WEB_API_PREFIX + "/user";
    public static final String USER_REGISTER = USER_API + "/register";
    public static final String USER_VIEW = USER_API + VIEW;
    public static final String USER_UPDATE = USER_API + UPDATE;
    public static final String USER_DELETE = USER_API + DELETE;
    public static final String USER_UNIQUECHECK = USER_API + UNIQUE_CHECK;
    public static final String USER_SET_PASSWORD = USER_API + "/setPassword";


    public static final String USER_LOGOUT = USER_API + "/logout";
    public static final String USER_LOGOUT_ALL = USER_API + "/logout-all";
    public static final String USER_SESSIONS = USER_API + "/sessions";


    //super admin
    public static final String SUPER_ADMIN_API = WEB_API_PREFIX + "/superadmin";
    public static final String SUPER_ADMIN_VIEW = SUPER_ADMIN_API + VIEW;
    public static final String IMAGE_UPLOAD_URL = SUPER_ADMIN_API + "/uploadimage";
    public static final String SUPER_ADMIN_UPDATE = SUPER_ADMIN_API + "/update/superadmin";

    // Employee and its Timesheets
    public static final String EMPLOYEE_API = WEB_API_PREFIX + "/employee";
    public static final String VIEW_ALL_TIMESHEET = EMPLOYEE_API + VIEW + "/timesheet";
    public static final String EMPLOYEE_TIMESHEET_VIEW = EMPLOYEE_API + VIEW + "/timesheet/{timesheetId}";
    public static final String EMPLOYEE_TIMESHEET_REGISTER = EMPLOYEE_API + "/timesheet/register";
    public static final String EMPLOYEE_TIMESHEET_UPDATE = EMPLOYEE_API + "/timesheet/update";
    public static final String EMPLOYEE_TIMESHEET_DELETE = EMPLOYEE_API + "/timesheet/delete";
    public static final String EMPLOYEE_TIMESHEET_MANAGER_REQUEST = EMPLOYEE_API + "/timesheet/approvaltomanager";
    public static final String EMPLOYEE_TIMESHEET_APPROVE_BY_MANAGER = EMPLOYEE_API + "/manager/approve";
    public static final String EMPLOYEE_TIMESHEET_REJECT_BY_MANAGER = EMPLOYEE_API + "/manager/reject";
    public static final String EMPLOYEE_LEAVE_REQUEST=EMPLOYEE_API+"/leaveApply";
    public static final String  EMPLOYEE_LEAVE_BY_ID=EMPLOYEE_API+VIEW+"/leave"+"/{leaveId}";
    public static final String  EMPLOYEE_LEAVE_UPDATE=EMPLOYEE_API+UPDATE+"/leave";
    public static final String  EMPLOYEE_LEAVE_WITHDRAW=EMPLOYEE_API+"/leave"+"/withdrawn";
    public static final String  EMPLOYEE_LEAVE_AVAILABILTY=EMPLOYEE_API+"/leave"+"/checkLeaveAvailability";
    public static final String  EMPLOYEE_PENDING_LEAVES=EMPLOYEE_API+"/leave"+"/pendingLeaves";
    public static final String  EMPLOYEE_LEAVE_STATUS_UPDATE=EMPLOYEE_API+"/leave"+"/updateStatus"+"/{leaveId}";
    public static final String EMPLOYEE_WORKDAYS = EMPLOYEE_API + "/workDays";
    public static final String EMPLOYEE_APPROVED_LEAVES = EMPLOYEE_API + "/approved/leaves";

    //common api for admin and employee
    public static final String LEAVE_SUMMARY = EMPLOYEE_API + "/leave-summary";
    public static final String EMPLOYEE_VIEW = EMPLOYEE_API + VIEW ;
    public static final String EMPLOYEE_UPDATE = EMPLOYEE_API + UPDATE ;
    public static final String EMPLOYEE_ADD_ADDRESS = EMPLOYEE_API + "/add/address";
    public static final String GET_BY_DESIGNATION = EMPLOYEE_API + "/designation/{designation}";
    //common api for address
    public static final String EMPLOYEE_ADDRESS_DELETE = EMPLOYEE_API + "/address/delete";
    public static final String MAANAGER_VIEW_EMPLOYEES = EMPLOYEE_API + "/manager/employees";




    // HolidayCalender and scheme
    public static final String HOLIDAYS = WEB_API_PREFIX + "/holidays";
    public static final String HOLIDAYS_CALENDAR_REGISTER = HOLIDAYS + "/calendar/register";
    public static final String HOLIDAYS_CALENDAR_VIEW_ALL = HOLIDAYS + VIEW + "/calendar";
    public static final String HOLIDAYS_CALENDAR_VIEW_ID = HOLIDAYS + VIEW + "/calendar/{id}";
    public static final String HOLIDAYS_CALENDAR_UPDATE = HOLIDAYS  + "/calendar" + UPDATE + "/{id}";
    public static final String HOLIDAYS_CALENDAR_DELETE = HOLIDAYS + "/calendar/delete";
    public static final String HOLIDAYS_SCHEME_REGISTER = HOLIDAYS + "/scheme/register";
    public static final String HOLIDAYS_SCHEME_VIEW_ALL = HOLIDAYS + VIEW + "/scheme";
    public static final String HOLIDAYS_SCHEME_VIEW_ID = HOLIDAYS + VIEW + "/scheme/{id}";
    public static final String HOLIDAYS_SCHEME_UPDATE = HOLIDAYS  + "/scheme" + UPDATE + "/{id}";
    public static final String HOLIDAYS_SCHEME_DELETE = HOLIDAYS + "/scheme/delete";
   //  Notification Related EndPoints

    public static final String NOTIFICATION_API = WEB_API_PREFIX + "/notification";
    public static final String GET_NOTIFICATIONS = NOTIFICATION_API+"/getAllNotifications";
    public static final String READ_NOTIFICATION = NOTIFICATION_API+"/read";
    public static final String CLEAR_ALL_NOTIFICATIONS = NOTIFICATION_API+"/clearAll";
    public static final String CLEAR_NOTIFICATION = NOTIFICATION_API+"/clear";
}