package com.chitfund.config;

import com.chitfund.model.Role;
import com.chitfund.model.User;
import com.chitfund.repository.RoleRepository;
import com.chitfund.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Ensure Roles exist in Database Table
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        Role agentRole = roleRepository.findByName("ROLE_AGENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_AGENT")));

        Role memberRole = roleRepository.findByName("ROLE_MEMBER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_MEMBER")));

        // 2. Ensure Admin User
        User admin = userRepository.findByUsername("admin").orElse(new User());
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("System Admin");
        admin.setEmail("admin@chitfund.com");
        admin.setRole(adminRole);
        userRepository.save(admin);

        // 3. Ensure Agent User
        User agent = userRepository.findByUsername("agent").orElse(new User());
        agent.setUsername("agent");
        agent.setPassword(passwordEncoder.encode("agent123"));
        agent.setFullName("Default Agent");
        agent.setEmail("agent@chitfund.com");
        agent.setRole(agentRole);
        userRepository.save(agent);

        // 4. Ensure Member User
        User member = userRepository.findByUsername("member").orElse(new User());
        member.setUsername("member");
        member.setPassword(passwordEncoder.encode("member123"));
        member.setFullName("Default Member");
        member.setEmail("member@chitfund.com");
        member.setRole(memberRole);
        userRepository.save(member);
    }
}