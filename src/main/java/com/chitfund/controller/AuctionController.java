package com.chitfund.controller;

import com.chitfund.model.Auction;
import com.chitfund.model.ChitGroup;
import com.chitfund.repository.AuctionRepository;
import com.chitfund.repository.ChitGroupRepository;
import com.chitfund.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auctions")
@CrossOrigin(origins = "*")
public class AuctionController {

    private final AuctionRepository auctionRepo;
    private final ChitGroupRepository groupRepo;
    private final MemberRepository memberRepo;

    public AuctionController(AuctionRepository auctionRepo,
                             ChitGroupRepository groupRepo,
                             MemberRepository memberRepo) {
        this.auctionRepo = auctionRepo;
        this.groupRepo = groupRepo;
        this.memberRepo = memberRepo;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'MEMBER')")
    public ResponseEntity<List<Auction>> getAllAuctions(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(auctionRepo.findByChitGroupGroupNameContainingIgnoreCase(search));
        }
        return ResponseEntity.ok(auctionRepo.findAll());
    }

    @GetMapping("/latest/group/{groupId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'MEMBER')")
    public ResponseEntity<?> getLatestAuctionByGroup(@PathVariable Long groupId) {
        Optional<Auction> latestAuction = auctionRepo.findAll().stream()
                .filter(a -> a.getChitGroup() != null && a.getChitGroup().getId().equals(groupId))
                .reduce((first, second) -> second);

        if (latestAuction.isPresent()) {
            return ResponseEntity.ok(latestAuction.get());
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAuction(@RequestBody Auction request) {
        ChitGroup group = groupRepo.findById(request.getChitGroup().getId())
                .orElseThrow(() -> new RuntimeException("Chit Group not found"));

        double chitAmount = (group.getChitAmount() != null) ? group.getChitAmount().doubleValue() : 0.0;
        int totalMembers = (group.getNumberOfMembers() != null && group.getNumberOfMembers() > 0) ? group.getNumberOfMembers() : 1;
        double winningBid = (request.getWinningBidAmount() != null) ? request.getWinningBidAmount() : 0.0;

        // Automated Financial Logic Engine
        double commission = chitAmount * 0.05; // 5% Foreman Commission
        double totalDiscount = chitAmount - winningBid;
        double netDividendPool = totalDiscount - commission;
        double dividendPerMember = netDividendPool / totalMembers;
        double baseInstallment = chitAmount / totalMembers;
        double nextInstallment = baseInstallment - dividendPerMember;

        request.setChitGroup(group);
        request.setWinningBidAmount(winningBid);
        request.setForemanCommission(commission);
        request.setTotalDiscount(totalDiscount);
        request.setDividendPerMember(dividendPerMember);
        request.setNextInstallmentAmount(nextInstallment);

        if (request.getAuctionDate() == null) {
            request.setAuctionDate(LocalDate.now());
        }

        Auction savedAuction = auctionRepo.save(request);
        return ResponseEntity.ok(savedAuction);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Auction> updateAuction(@PathVariable Long id, @RequestBody Auction details) {
        Auction auction = auctionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + id));

        ChitGroup group = groupRepo.findById(details.getChitGroup().getId())
                .orElseThrow(() -> new RuntimeException("Chit Group not found"));

        double chitAmount = (group.getChitAmount() != null) ? group.getChitAmount().doubleValue() : 0.0;
        int totalMembers = (group.getNumberOfMembers() != null && group.getNumberOfMembers() > 0) ? group.getNumberOfMembers() : 1;
        double winningBid = (details.getWinningBidAmount() != null) ? details.getWinningBidAmount() : 0.0;

        // Recalculate Automated Financial Metrics
        double commission = chitAmount * 0.05;
        double totalDiscount = chitAmount - winningBid;
        double netDividendPool = totalDiscount - commission;
        double dividendPerMember = netDividendPool / totalMembers;
        double baseInstallment = chitAmount / totalMembers;
        double nextInstallment = baseInstallment - dividendPerMember;

        auction.setChitGroup(group);
        auction.setWinnerMember(details.getWinnerMember());
        auction.setWinningBidAmount(winningBid);
        auction.setAuctionDate(details.getAuctionDate() != null ? details.getAuctionDate() : LocalDate.now());
        auction.setForemanCommission(commission);
        auction.setTotalDiscount(totalDiscount);
        auction.setDividendPerMember(dividendPerMember);
        auction.setNextInstallmentAmount(nextInstallment);

        return ResponseEntity.ok(auctionRepo.save(auction));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuction(@PathVariable Long id) {
        auctionRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}