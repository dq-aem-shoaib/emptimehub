package com.EmpTimeHub.service;

import com.EmpTimeHub.model.AddressModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    @Transactional
    void addAddresses(String entityType, UUID entityId, List<AddressModel> addresses);
    List<AddressModel> getAddressesForEntity(String entityType, UUID entityId);

     void updateAddresses(UUID employeeId, List<AddressModel> addressModels) ;

     void deleteAddress(UUID employeeId, UUID addressId);
}
