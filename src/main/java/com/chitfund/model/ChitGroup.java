package com.chitfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String groupName;
    private Double chitAmount;
    private Double monthlyInstallment;
    private Integer numberOfMembers;
    private Integer durationMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

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

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}