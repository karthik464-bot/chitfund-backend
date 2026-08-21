package com.chitfund.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private ChitGroup chitGroup;

    @ManyToOne
    @JoinColumn(name = "winner_member_id", nullable = false)
    private Member winnerMember;

    private Double winningBidAmount;
    private LocalDate auctionDate;

    // Automated Calculations
    private Double foremanCommission;
    private Double totalDiscount;
    private Double dividendPerMember;
    private Double nextInstallmentAmount;

    public Auction() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChitGroup getChitGroup() { return chitGroup; }
    public void setChitGroup(ChitGroup chitGroup) { this.chitGroup = chitGroup; }

    public Member getWinnerMember() { return winnerMember; }
    public void setWinnerMember(Member winnerMember) { this.winnerMember = winnerMember; }

    public Double getWinningBidAmount() { return winningBidAmount; }
    public void setWinningBidAmount(Double winningBidAmount) { this.winningBidAmount = winningBidAmount; }

    public LocalDate getAuctionDate() { return auctionDate; }
    public void setAuctionDate(LocalDate auctionDate) { this.auctionDate = auctionDate; }

    public Double getForemanCommission() { return foremanCommission; }
    public void setForemanCommission(Double foremanCommission) { this.foremanCommission = foremanCommission; }

    public Double getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(Double totalDiscount) { this.totalDiscount = totalDiscount; }

    public Double getDividendPerMember() { return dividendPerMember; }
    public void setDividendPerMember(Double dividendPerMember) { this.dividendPerMember = dividendPerMember; }

    public Double getNextInstallmentAmount() { return nextInstallmentAmount; }
    public void setNextInstallmentAmount(Double nextInstallmentAmount) { this.nextInstallmentAmount = nextInstallmentAmount; }
}