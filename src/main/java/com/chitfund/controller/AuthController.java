package com.chitfund.controller;

import com.chitfund.model.RefreshToken;
import com.chitfund.model.Role;
import com.chitfund.model.User;
import com.chitfund.payload.request.TokenRefreshRequest;
import com.chitfund.payload.response.TokenRefreshResponse;
import com.chitfund.repository.RoleRepository;
import com.chitfund.repository.UserRepository;
import com.chitfund.security.JwtUtils;
import com.chitfund.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils,
                          RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid username or password!"));
        }

        String roleName = (user.getRole() != null) ? user.getRole().getName() : "ROLE_MEMBER";

        // Generate short-lived Access Token
        String accessToken = jwtUtils.generateToken(user.getUsername(), roleName, user.getId());

        // Generate long-lived Refresh Token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("token", accessToken);
        response.put("refreshToken", refreshToken.getToken());
        response.put("role", roleName);
        response.put("username", user.getUsername());
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String roleName = (user.getRole() != null) ? user.getRole().getName() : "ROLE_MEMBER";
                    // Generate a fresh Access Token
                    String accessToken = jwtUtils.generateToken(user.getUsername(), roleName, user.getId());
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
                .orElseThrow(() -> new RuntimeException("ROLE_AGENT not found in database"));

        User agent = new User();
        agent.setUsername(username);
        agent.setPassword(passwordEncoder.encode(password));
        agent.setFullName(fullName);
        agent.setEmail(email);
        agent.setPhone(phone);
        agent.setRole(agentRole);

        userRepository.save(agent);

        return ResponseEntity.ok(Map.of("message", "Agent registered successfully!"));
    }
}