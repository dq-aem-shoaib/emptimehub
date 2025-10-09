package com.EmpTimeHub.service.impl;


import com.EmpTimeHub.entity.User;
import com.EmpTimeHub.exceptions.customExceptions.auth.InvalidLoginException;
import com.EmpTimeHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

@Autowired
private UserRepository userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String inputKey) throws UsernameNotFoundException {

        User user = userRepo.findByUserName(inputKey)
        .or(() -> userRepo.findByCompanyEmail(inputKey))
        .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + inputKey));
    String roleName = user.getRole().name();
        return buildUserDetails(user);
    }

    @Transactional
    private UserDetails buildUserDetails(User user) {


        String userRole = userRepo.findByIdWithRole(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User role empty or null in : " + user.getUserId()))
                .getRole().name();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userRole);
        return new org.springframework.security.core.userdetails.User(
                user.getCompanyEmail(), user.getPassword(), Collections.singleton(authority));
    }

}
