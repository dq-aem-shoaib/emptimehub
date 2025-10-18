package com.EmpTimeHub.mapper;

import com.EmpTimeHub.dto.ClientDTO;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.model.AddressModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ClientMapper {

    /**
     * Converts a Client entity and its addresses into a ClientDTO.
     *
     * @param client the client entity
     * @param addresses the list of AddressModel
     * @return ClientDTO with all addresses
     */
    public ClientDTO toDTO(Client client, List<AddressModel> addresses) {
        if (client == null) return null;

        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(client, dto);

        if (client.getUser() != null) {
            dto.setUserId(client.getUser().getUserId());
        }

        dto.setAddresses(addresses); // now we set the list

        return dto;
    }
}
