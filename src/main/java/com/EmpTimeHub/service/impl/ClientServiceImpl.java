package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.ClientDTO;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.entity.ClientPoc;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.model.AddressModel;
import com.EmpTimeHub.model.ClientModel;
import com.EmpTimeHub.repository.*;
import com.EmpTimeHub.service.AddressService;
import com.EmpTimeHub.service.ClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;
    private AddressRepository addressRepository;
    private final EmployeeRepository empRepo;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepo;
    private final AddressService addressService;
    private final ClientPocRepository clientPocRepository;

    @Transactional
    @Override
    public Client addClient(ClientModel clientModel) {
        //Generate username & password
        String username = clientModel.getCompanyName()
                .toLowerCase()
                .replaceAll("\\s+", "")
                + clientModel.getPanNumber().substring(clientModel.getPanNumber().length() - 4);
        //Generate Password
        String rawPassword = "Client@" + clientModel.getPanNumber().
                substring(clientModel.getPanNumber().length() - 4);
        //Encryption
        String encryptedPassword = passwordEncoder.encode(rawPassword);

        log.info("Client added with username: {} and Password: {}",username,rawPassword);

        //  Save User
        User user = User.builder()
                .userName(username)
                .companyEmail(clientModel.getEmail())
                .password(encryptedPassword)
                .role(EnumConstants.Role.CLIENT)
                .build();
        User savedUser = userRepository.save(user);


        //Build Client
        Client client = Client.builder()
                .user(user)
                .companyName(clientModel.getCompanyName())
                .contactNumber(clientModel.getContactNumber())
                .email(clientModel.getEmail())
                .gst(clientModel.getGst())
                .currency(clientModel.getCurrency())
                .panNumber(clientModel.getPanNumber())
                .status("ACTIVE")
                .tanNumber(clientModel.getTanNumber())
                .createdAt(LocalDateTime.now()).build();

        Client savedClient = clientRepo.save(client);
        addressService.addAddresses(EnumConstants.EntityType.CLIENT.getValue(), client.getClientId(), clientModel.getAddresses());

        // Build and attach Client POCs
        List<ClientPoc> clientPocs = clientModel.getClientPocs().stream()
                .map(pocModel -> ClientPoc.builder()
                        .name(pocModel.getName())
                        .email(pocModel.getEmail())
                        .contactNumber(pocModel.getContactNumber())
                        .designation(pocModel.getDesignation())
                        .client(client)  // <-- Important to set the relationship
                        .build())
                .collect(Collectors.toList());

        client.setPocs(clientPocs);


        return client;
    }
    @Override
    public ClientDTO getClientById(UUID clientId){
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client Not Found"));
        return convertToDto(client);
    }

    @Override
    public List<ClientDTO> getAllClient() {
        return clientRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void updateClientById(UUID clientId, ClientModel clientModel) {

        // ---------- Fetch existing client ----------
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // ---------- Basic Info ----------
        if (clientModel.getCompanyName() != null) client.setCompanyName(clientModel.getCompanyName());
        if (clientModel.getContactNumber() != null) client.setContactNumber(clientModel.getContactNumber());
        if (clientModel.getEmail() != null) client.setEmail(clientModel.getEmail());
        if (clientModel.getGst() != null) client.setGst(clientModel.getGst());
        if (clientModel.getCurrency() != null) client.setCurrency(clientModel.getCurrency());
        if (clientModel.getPanNumber() != null) client.setPanNumber(clientModel.getPanNumber());

        // ---------- Address Update ----------
        List<AddressModel> clientAddresses = addressService.getAddressesForEntity("CLIENT", client.getClientId());
        if (clientModel.getAddresses() != null && !clientModel.getAddresses().isEmpty()) {
            addressService.addAddresses(EnumConstants.EntityType.CLIENT.getValue(), clientId ,clientAddresses );
        }

        // ---------- Update timestamp ----------
        client.setUpdatedAt(LocalDateTime.now());

        // ---------- Save updated client ----------
        clientRepository.save(client);
    }

    @Override
    public void removeClientById(UUID clientId) {
        Client client = clientRepository.findById(clientId).get();
        if(client.getStatus().equals("ACTIVE")){
            client.setStatus("INACTIVE");
            clientRepository.save(client);
        }
    }

    public ClientDTO convertToDto(Client client) {
        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(client, dto);
        if (client.getUser() != null) {
            dto.setUserId(client.getUser().getUserId());
        }

        List<AddressModel> clientAddresses = addressService.getAddressesForEntity(EnumConstants.EntityType.CLIENT.getValue(), client.getClientId());

        dto.setPocs(client.getPocs());
        if(clientAddresses!= null && !clientAddresses.isEmpty()){
            dto.setAddresses(clientAddresses);
        }
        return dto;
    }
}
