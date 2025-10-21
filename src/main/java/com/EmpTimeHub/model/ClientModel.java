package com.EmpTimeHub.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String tanNumber;
    // ---------- Address Info ----------

    private List<AddressModel> addresses;

    private List<ClientPocModel> clientPocs;
}
