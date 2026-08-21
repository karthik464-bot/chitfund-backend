package com.chitfund.config;

import com.chitfund.model.Role;
import com.chitfund.model.User;
import com.chitfund.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure Admin Account
        User admin = userRepository.findByUsername("admin").orElse(new User());
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Ensure Agent Account
        User agent = userRepository.findByUsername("agent").orElse(new User());
        agent.setUsername("agent");
        agent.setPassword(passwordEncoder.encode("agent123"));
        agent.setRole(Role.AGENT);
        userRepository.save(agent);

        // Ensure Member Account
        User member = userRepository.findByUsername("member").orElse(new User());
        member.setUsername("member");
        member.setPassword(passwordEncoder.encode("member123"));
        member.setRole(Role.MEMBER);
        userRepository.save(member);
    }
}