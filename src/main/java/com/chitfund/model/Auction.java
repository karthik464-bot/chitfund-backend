package com.chitfund.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private ChitGroup chitGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "winner_member_id", nullable = false)
    private Member winnerMember;

    @Column(name = "bid_amount", nullable = false)
    private Double bidAmount;

    @Column(name = "auction_date", nullable = false)
    private LocalDate auctionDate;

    // Automated Auction Calculations
    @Column(name = "foreman_commission")
    private Double foremanCommission;

    @Column(name = "total_discount")
    private Double totalDiscount;

    @Column(name = "dividend_per_member")
    private Double dividendPerMember;

    @Column(name = "next_installment_amount")
    private Double nextInstallmentAmount;

    public Auction() {}

    // Primary Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChitGroup getChitGroup() { return chitGroup; }
    public void setChitGroup(ChitGroup chitGroup) { this.chitGroup = chitGroup; }

    public Member getWinnerMember() { return winnerMember; }
    public void setWinnerMember(Member winnerMember) { this.winnerMember = winnerMember; }

    public Double getBidAmount() { return bidAmount; }
    public void setBidAmount(Double bidAmount) { this.bidAmount = bidAmount; }

    // Alias methods for compatibility with winningBidAmount references
    public Double getWinningBidAmount() { return bidAmount; }
    public void setWinningBidAmount(Double winningBidAmount) { this.bidAmount = winningBidAmount; }

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