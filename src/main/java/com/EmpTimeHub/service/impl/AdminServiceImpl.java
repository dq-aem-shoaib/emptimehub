package com.EmpTimeHub.service.impl;

import com.EmpTimeHub.repository.AdminRepository;
import com.EmpTimeHub.service.AdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    /**
     * Retrieves the full names of all admins in the system.
     *
     * @return List of admin full names.
     */
    @Override
    public List<String> getAllAdminNames() {
        log.info("Fetching all admin names from the repository");

        List<String> names = adminRepository.findAll()
                .stream()
                .map(admin -> admin.getFullName())
                .collect(Collectors.toList());

        log.debug("Fetched {} admin names: {}", names.size(), names);
        return names;
    }
}
