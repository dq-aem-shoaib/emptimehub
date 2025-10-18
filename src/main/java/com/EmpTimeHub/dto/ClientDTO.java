package com.EmpTimeHub.dto;

import com.EmpTimeHub.entity.ClientPoc;
import com.EmpTimeHub.model.AddressModel;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private UUID clientId;
    private UUID userId;
    private String companyName;
    private String contactNumber;
    private String email;
    private String gst;
    private String currency;
    private String panNumber;
    private String tanNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AddressModel> addresses;
    private List<ClientPoc> pocs;
}


