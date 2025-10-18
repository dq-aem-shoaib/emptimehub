package com.EmpTimeHub.model;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressModel implements Serializable {

    private UUID addressId;
    private String houseNo;
    private String streetName;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String addressType;
}
