package com.EmpTimeHub.repository;

import com.EmpTimeHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserName(String inputKey);

    Optional<User> findByCompanyEmail(String emailId);

    boolean existsByUserName(String superAdmin);

    @Query("SELECT u FROM User u WHERE u.userId = :id")
    Optional<User> findByIdWithRole(@Param("id") UUID id);

    @Query("SELECT u FROM User u WHERE u.companyEmail = :email")
    Optional<User> findByEmailWithUserType(@Param("email") String email);

}

