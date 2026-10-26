package com.chitfund.repository;

import com.chitfund.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Methods for your Chit Fund login & validation
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    // Methods for email-based authentication (Mam's task)
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    // Role-based lookup from your existing app
    List<User> findByRoleName(String roleName);
}