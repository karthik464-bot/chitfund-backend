package com.chitfund.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "chit_groups")
public class ChitGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "chit_amount", nullable = false)
    private Double chitAmount;

    @Column(name = "monthly_installment", nullable = false)
    private Double monthlyInstallment;

    @Column(name = "number_of_members", nullable = false)
    private Integer numberOfMembers;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(nullable = false)
    private Double commission = 5.0;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String status = "ACTIVE";

    public ChitGroup() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Double getChitAmount() { return chitAmount; }
    public void setChitAmount(Double chitAmount) { this.chitAmount = chitAmount; }

    public Double getMonthlyInstallment() { return monthlyInstallment; }
    public void setMonthlyInstallment(Double monthlyInstallment) { this.monthlyInstallment = monthlyInstallment; }

    public Integer getNumberOfMembers() { return numberOfMembers; }
    public void setNumberOfMembers(Integer numberOfMembers) { this.numberOfMembers = numberOfMembers; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

    public Double getCommission() { return commission; }
    public void setCommission(Double commission) { this.commission = commission; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}