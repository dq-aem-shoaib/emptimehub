package com.EmpTimeHub.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientModel {

    // ---------- Company Info ----------
    private String companyName;
    private String contactNumber;
    private String email;
    private String gst;
    private String currency;
    private String panNumber;
    // ---------- Address Info ----------
    private String houseNo;
    private String streetName;
    private String city;
    private String state;
    private String pinCode;
    private String country;
}
