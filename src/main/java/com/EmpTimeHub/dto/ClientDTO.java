package com.EmpTimeHub.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO implements Serializable {
    private UUID clientId;
    private UUID userId;
    private UUID addressId;
    private String companyName;
    private String contactNumber;
    private String email;
    private String gst;
    private String currency;
    private String panNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String houseNo;
    private String streetName;
    private String city;
    private String state;
    private String pinCode;
    private String country;
}
