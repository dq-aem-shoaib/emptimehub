package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.entity.*;
import com.EmpTimeHub.model.AddressModel;
import com.EmpTimeHub.repository.*;
import com.EmpTimeHub.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final EntityAddressRepository entityAddressRepository;



    @Transactional
    @Override
    public void addAddresses(String entityType, UUID entityId, List<AddressModel> addresses) {
        for (AddressModel model : addresses) {
            Address address = new Address();
            BeanUtils.copyProperties(model, address);
            address.setAddressId(null);
            Address savedAddress = addressRepository.save(address);

            EntityAddress entityAddress = new EntityAddress();
            entityAddress.setEntityType(entityType);
            entityAddress.setEntityId(entityId);
            entityAddress.setAddress(savedAddress);
            entityAddress.setAddressType(model.getAddressType());

            entityAddressRepository.save(entityAddress);
        }
    }

    @Override
    public List<AddressModel> getAddressesForEntity(String entityType, UUID entityId) {
        return entityAddressRepository.findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .map(entityAddress -> {
                    Address address = entityAddress.getAddress();
                    AddressModel model = new AddressModel();
                    model.setHouseNo(address.getHouseNo());
                    model.setStreetName(address.getStreetName());
                    model.setCity(address.getCity());
                    model.setState(address.getState());
                    model.setCountry(address.getCountry());
                    model.setPincode(address.getPincode());
                    model.setAddressType(entityAddress.getAddressType());
                    return model;
                })
                .collect(Collectors.toList());
    }
}

