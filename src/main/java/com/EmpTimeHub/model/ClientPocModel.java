package com.EmpTimeHub.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class ClientPocModel implements Serializable {
    private String name;
    private String email;
    private String contactNumber;
    private String designation;
}
