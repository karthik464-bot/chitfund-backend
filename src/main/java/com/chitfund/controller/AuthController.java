package com.chitfund.controller;

import com.chitfund.model.Role;
import com.chitfund.model.User;
import com.chitfund.repository.RoleRepository;
import com.chitfund.repository.UserRepository;
import com.chitfund.security.JwtUtils;
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

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
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

        // Fixed: Passed user.getId() as the 3rd argument to match generateToken signature
        String token = jwtUtils.generateToken(user.getUsername(), roleName, user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", roleName);
        response.put("username", user.getUsername());
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
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