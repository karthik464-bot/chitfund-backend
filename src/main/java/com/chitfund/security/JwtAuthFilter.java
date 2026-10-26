package com.chitfund.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = parseJwt(request);

            if (token != null && jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                String role = jwtUtils.getRoleFromJwtToken(token);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (role != null) {
                    if (!role.startsWith("ROLE_")) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    } else {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                } else {
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}