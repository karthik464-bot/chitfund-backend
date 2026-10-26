package com.chitfund.controller;

import com.chitfund.model.RefreshToken;
import com.chitfund.model.Role;
import com.chitfund.model.User;
import com.chitfund.payload.request.LoginRequest;
import com.chitfund.payload.request.SignupRequest;
import com.chitfund.payload.request.TokenRefreshRequest;
import com.chitfund.payload.response.JwtResponse;
import com.chitfund.payload.response.TokenRefreshResponse;
import com.chitfund.repository.RoleRepository;
import com.chitfund.repository.UserRepository;
import com.chitfund.security.JwtUtils;
import com.chitfund.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String identifier = loginRequest.getUsername() != null ? loginRequest.getUsername() : loginRequest.getEmail();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .or(() -> userRepository.findByEmail(userDetails.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String roleName = (user.getRole() != null) ? user.getRole().getName() : "ROLE_USER";

        String accessToken = jwtUtils.generateToken(user.getUsername(), roleName, user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return ResponseEntity.ok(new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roleName,
                Collections.singletonList(roleName)
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Email is already in use!"));
        }

        User user = new User();
        user.setName(signUpRequest.getName());
        user.setFullName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getEmail()); // fallback username mapping
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Resolve or assign relational Role entity
        String roleStr = signUpRequest.getRole() != null ? signUpRequest.getRole() : "ROLE_USER";
        if (!roleStr.startsWith("ROLE_")) {
            roleStr = "ROLE_" + roleStr.toUpperCase();
        }

        String finalRoleStr = roleStr;
        Role role = roleRepository.findByName(roleStr)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(finalRoleStr);
                    return roleRepository.save(newRole);
                });
        user.setRole(role);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String roleName = (user.getRole() != null) ? user.getRole().getName() : "ROLE_MEMBER";
                    String accessToken = jwtUtils.generateToken(user.getEmail() != null ? user.getEmail() : user.getUsername(), roleName, user.getId());
                    return ResponseEntity.ok(new TokenRefreshResponse(accessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @PostMapping("/register-agent")
    public ResponseEntity<?> registerAgent(@RequestBody Map<String, String> regRequest) {
        String username = regRequest.get("username");
        String password = regRequest.get("password");
        String fullName = regRequest.get("fullName");
        String email = regRequest.get("email");
        String phone = regRequest.get("phone");

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username is already taken!"));
        }

        Role agentRole = roleRepository.findByName("ROLE_AGENT")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_AGENT");
                    return roleRepository.save(r);
                });

        User agent = new User();
        agent.setUsername(username);
        agent.setPassword(passwordEncoder.encode(password));
        agent.setName(fullName);
        agent.setFullName(fullName);
        agent.setEmail(email);
        agent.setPhone(phone);
        agent.setRole(agentRole);

        userRepository.save(agent);

        return ResponseEntity.ok(Map.of("message", "Agent registered successfully!"));
    }
}