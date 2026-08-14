package com.chitfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "chit_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChitGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private BigDecimal chitAmount;

    @Column(nullable = false)
    private BigDecimal monthlyInstallment;

    @Column(nullable = false)
    private Integer numberOfMembers;

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, COMPLETED
}