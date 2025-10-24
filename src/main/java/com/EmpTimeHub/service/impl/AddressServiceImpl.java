package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.constants.EnumConstants;
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
    private final EmployeeRepository employeeRepository;



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
                    model.setAddressId(address.getAddressId());
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

    @Override
    public void updateAddresses(UUID employeeId, List<AddressModel> addressModels) {

        employeeRepository.findById(employeeId).orElseThrow(()-> new RuntimeException("Employee not found"));

        if (addressModels == null || addressModels.isEmpty()) {
            return; // nothing to update
        }

        for (AddressModel addressModel : addressModels) {
            if (addressModel.getAddressId() != null) {
                // Existing address → update
                Address existingAddress = addressRepository.findById(addressModel.getAddressId())
                        .orElseThrow(() -> new RuntimeException("Address not found"));

                entityAddressRepository.findByAddress(existingAddress)
                                .orElseThrow(() -> new RuntimeException("entity address not found"));


                updateIfNotNull(addressModel.getHouseNo(), existingAddress::setHouseNo);
                updateIfNotNull(addressModel.getStreetName(), existingAddress::setStreetName);
                updateIfNotNull(addressModel.getCity(), existingAddress::setCity);
                updateIfNotNull(addressModel.getCountry(), existingAddress::setCountry);
                updateIfNotNull(addressModel.getPincode(), existingAddress::setPincode);

                // Save updates
                existingAddress.setUpdatedAt(java.time.LocalDateTime.now());
                addressRepository.save(existingAddress);
            } else {
                // New address → add
                Address newAddress = Address.builder()
                        .houseNo(addressModel.getHouseNo())
                        .streetName(addressModel.getStreetName())
                        .city(addressModel.getCity())
                        .state(addressModel.getState())
                        .country(addressModel.getCountry())
                        .pincode(addressModel.getPincode())
                        .createdAt(java.time.LocalDateTime.now())
                        .updatedAt(java.time.LocalDateTime.now())
                        .build();

                addressRepository.save(newAddress);

                // Link employee and address (assuming you use an entity like EntityAddress)
                EntityAddress entityAddress = EntityAddress.builder()
                        .entityId(employeeId)
                        .address(newAddress)
                        .addressType(addressModel.getAddressType())
                        .entityType(EnumConstants.EntityType.EMPLOYEE.getValue())
                        .build();
                entityAddressRepository.save(entityAddress);
            }
        }
    }

    @Override
    public void deleteAddress(UUID employeeId, UUID addressId) {
        // Fetch the address
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // Fetch the entity-address mapping and verify ownership
        EntityAddress entityAddress = entityAddressRepository.findByAddress(address)
                .orElseThrow(() -> new RuntimeException("EntityAddress not found"));

        if (!entityAddress.getEntityId().equals(employeeId) ||
                !entityAddress.getEntityType().equals(EnumConstants.EntityType.EMPLOYEE.getValue())) {
            throw new RuntimeException("This address does not belong to the specified employee");
        }

        // Delete mapping first
        entityAddressRepository.delete(entityAddress);

        // Delete actual address
        addressRepository.delete(address);
    }


    private <T> void updateIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }




}

