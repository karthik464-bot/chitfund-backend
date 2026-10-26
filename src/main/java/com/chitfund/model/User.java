package com.chitfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username; // Kept for your Chit Fund login/system

    private String name; // Added for Mam's task requirement

    @Column(unique = true)
    private String email; // Added for Mam's email-based login requirement

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName; // Kept from your Chit Fund app

    private String phone; // Kept from your Chit Fund app

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role; // Kept your relational Role table linkage
}