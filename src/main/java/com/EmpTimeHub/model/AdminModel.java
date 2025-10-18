package com.EmpTimeHub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.io.Serializable;

/**
 * This model is used for accepting requests for updating the admin details
 */
@Data
@AllArgsConstructor
public class AdminModel implements Serializable {

    // ---------- Basic Info ----------
    private String firstName;
    private String lastName;
    private String personalEmail;   // Employee's personal email
    private String companyEmail;    // Company-provided email
    private String contactNumber;

}