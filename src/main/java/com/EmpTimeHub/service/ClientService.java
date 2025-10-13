package com.EmpTimeHub.service;

import com.EmpTimeHub.dto.ClientDTO;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.model.ClientModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    @Transactional
    Client addClient(ClientModel clientModel);

    ClientDTO getClientById(UUID clientId);

    List<ClientDTO> getAllClient();

    void updateClientById(UUID clientId, ClientModel clientModel);

    void removeClientById(UUID clientId);
}
