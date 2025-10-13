package com.EmpTimeHub.constants;

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
    public static final String ADMIN_UNASSIGN_CLIENT = ADMIN_API+"/emp/{empId}";










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

    // Employee
    public static final String EMPLOYEE_API = WEB_API_PREFIX + "/employee";
    public static final String VIEW_ALL_TIMESHEET = EMPLOYEE_API + VIEW + "/timesheet";
    public static final String EMPLOYEE_TIMESHEET_VIEW = EMPLOYEE_API + VIEW + "/timesheet/{timesheetId}";
    public static final String EMPLOYEE_TIMESHEET_REGISTER = EMPLOYEE_API + "/register";
    public static final String EMPLOYEE_LEAVE_REQUEST=EMPLOYEE_API+"/leaveApply";
    public static final String  EMPLOYEE_LEAVE_BY_ID=EMPLOYEE_API+VIEW+"/leave"+"/{leaveId}";
    public static final String  EMPLOYEE_LEAVE_UPDATE=EMPLOYEE_API+UPDATE+"/leave";
    public static final String  EMPLOYEE_LEAVE_DELETE=EMPLOYEE_API+"/leave"+"/delete"+DELETE;
    public static final String  EMPLOYEE_LEAVE_STATUS_UPDATE=EMPLOYEE_API+"/leave"+"/updateStatus"+"/{leaveId}";
//common api for admin and employee
    public static final String COMMON_API = WEB_API_PREFIX + "/common";
    public static final String LEAVE_SUMMARY = COMMON_API + "/leave-summary";
    public static final String EMPLOYEE_TIMESHEET_UPDATE = EMPLOYEE_API + "/timesheet/update";
    public static final String EMPLOYEE_TIMESHEET_DELETE = EMPLOYEE_API + "/timesheet/delete";
}