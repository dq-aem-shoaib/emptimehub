package com.EmpTimeHub.service;

import java.util.List;

/**
 * Service interface for admin-related operations.
 */
public interface AdminService {

    /**
     * Retrieves the full names of all admins in the system.
     *
     * @return List of admin full names as Strings.
     */
    List<String> getAllAdminNames();
}
