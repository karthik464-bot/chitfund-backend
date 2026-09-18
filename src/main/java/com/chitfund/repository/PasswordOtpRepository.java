package com.chitfund.repository;

import com.chitfund.model.PasswordOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordOtpRepository extends JpaRepository<PasswordOtp, Long> {
    Optional<PasswordOtp> findTopByEmailOrderByIdDesc(String email);
    void deleteByEmail(String email);
}