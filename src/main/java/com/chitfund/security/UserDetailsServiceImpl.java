package com.chitfund.security;

import com.chitfund.model.User;
import com.chitfund.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + usernameOrEmail));

        String roleName = "ROLE_USER";
        if (user.getRole() != null && user.getRole().getName() != null) {
            roleName = user.getRole().getName();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName.toUpperCase();
            }
        }

        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(roleName));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}