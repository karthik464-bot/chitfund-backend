package com.chitfund.controller;

import com.chitfund.model.PasswordOtp;
import com.chitfund.model.User;
import com.chitfund.repository.PasswordOtpRepository;
import com.chitfund.repository.UserRepository;
import com.chitfund.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/forgot-password")
@CrossOrigin(origins = "*")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final PasswordOtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository,
                                    PasswordOtpRepository otpRepository,
                                    EmailService emailService,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/request")
    @Transactional
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required."));
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            String otp = String.format("%06d", new SecureRandom().nextInt(1000000));
            String otpHash = passwordEncoder.encode(otp);

            otpRepository.deleteByEmail(email);
            PasswordOtp passwordOtp = new PasswordOtp(email, otpHash, LocalDateTime.now().plusMinutes(5));
            otpRepository.save(passwordOtp);

            try {
                emailService.sendOtpEmail(email, otp);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(Map.of("message", "Failed to send email: " + e.getMessage()));
            }
        }

        return ResponseEntity.ok(Map.of("message", "If an account exists with this email, an OTP has been sent."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        Optional<PasswordOtp> otpOpt = otpRepository.findTopByEmailOrderByIdDesc(email);
        if (otpOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid request or OTP expired."));
        }

        PasswordOtp passwordOtp = otpOpt.get();

        if (passwordOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "OTP has expired. Please request a new one."));
        }

        if (!passwordEncoder.matches(otp, passwordOtp.getOtpHash())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP code entered."));
        }

        passwordOtp.setVerified(true);
        otpRepository.save(passwordOtp);

        return ResponseEntity.ok(Map.of("message", "OTP verified successfully."));
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        Optional<PasswordOtp> otpOpt = otpRepository.findTopByEmailOrderByIdDesc(email);
        if (otpOpt.isEmpty() || !Boolean.TRUE.equals(otpOpt.get().getVerified())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Unauthorized. Please verify your OTP first."));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User account not found."));
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRepository.deleteByEmail(email);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully! You can now log in."));
    }
}