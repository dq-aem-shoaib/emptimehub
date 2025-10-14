package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
import com.EmpTimeHub.dto.ClientDTO;
import com.EmpTimeHub.entity.Address;
import com.EmpTimeHub.entity.Client;
import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.model.ClientModel;
import com.EmpTimeHub.repository.AddressRepository;
import com.EmpTimeHub.repository.ClientRepository;
import com.EmpTimeHub.repository.EmployeeRepository;
import com.EmpTimeHub.repository.UserRepository;
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
                .role(EnumConstants.Role.EMPLOYEE)
                .build();
        User savedUser = userRepository.save(user);

        //  Build Address entity (not yet saved)
        Address address = Address.builder()
                .houseNo(clientModel.getHouseNo())
                .streetName(clientModel.getStreetName())
                .city(clientModel.getCity())
                .state(clientModel.getState())
                .country(clientModel.getCountry())
                .pincode(clientModel.getPinCode())
                .build();

        //Build Client
        Client client = Client.builder()
                .user(user)
                .address(address)
                .companyName(clientModel.getCompanyName())
                .contactNumber(clientModel.getContactNumber())
                .email(clientModel.getEmail())
                .gst(clientModel.getGst())
                .currency(clientModel.getCurrency())
                .panNumber(clientModel.getPanNumber())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now()).build();
        return clientRepo.save(client);
    }
    //helper method to convert Client to CLientDTO
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
        Address address = addressRepository.findById(client.getAddress().getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found for client: " + clientId));

        if (clientModel.getHouseNo() != null) address.setHouseNo(clientModel.getHouseNo());
        if (clientModel.getStreetName() != null) address.setStreetName(clientModel.getStreetName());
        if (clientModel.getCity() != null) address.setCity(clientModel.getCity());
        if (clientModel.getState() != null) address.setState(clientModel.getState());
        if (clientModel.getPinCode() != null) address.setPincode(clientModel.getPinCode());
        if (clientModel.getCountry() != null) address.setCountry(clientModel.getCountry());

        addressRepository.save(address);

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
        if (client.getAddress() != null) {
            dto.setAddressId(client.getAddress().getAddressId());
            dto.setHouseNo(client.getAddress().getHouseNo());
            dto.setStreetName(client.getAddress().getStreetName());
            dto.setCity(client.getAddress().getCity());
            dto.setState(client.getAddress().getState());
            dto.setPinCode(client.getAddress().getPincode());
            dto.setCountry(client.getAddress().getCountry());
        }
        return dto;
    }
}
