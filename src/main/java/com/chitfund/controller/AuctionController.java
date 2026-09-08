package com.chitfund.controller;

import com.chitfund.model.Auction;
import com.chitfund.model.ChitGroup;
import com.chitfund.model.Member;
import com.chitfund.repository.AuctionRepository;
import com.chitfund.repository.ChitGroupRepository;
import com.chitfund.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auctions")
@CrossOrigin(origins = "*")
public class AuctionController {

    private final AuctionRepository auctionRepository;
    private final ChitGroupRepository chitGroupRepository;
    private final MemberRepository memberRepository;

    public AuctionController(AuctionRepository auctionRepository,
                             ChitGroupRepository chitGroupRepository,
                             MemberRepository memberRepository) {
        this.auctionRepository = auctionRepository;
        this.chitGroupRepository = chitGroupRepository;
        this.memberRepository = memberRepository;
    }

    // Fetch all auctions or search by group name
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'MEMBER')")
    public ResponseEntity<List<Auction>> getAllAuctions(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(auctionRepository.findByChitGroupGroupNameContainingIgnoreCase(search));
        }
        return ResponseEntity.ok(auctionRepository.findAll());
    }

    // Get latest auction for a specific group
    @GetMapping("/latest/group/{groupId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'MEMBER')")
    public ResponseEntity<?> getLatestAuctionByGroup(@PathVariable Long groupId) {
        Optional<Auction> latestAuction = auctionRepository.findAll().stream()
                .filter(a -> a.getChitGroup() != null && a.getChitGroup().getId().equals(groupId))
                .reduce((first, second) -> second);

        return ResponseEntity.ok(latestAuction.orElse(null));
    }

    // Record an auction with automated financial calculations and flexible payload parsing
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> recordAuction(@RequestBody Map<String, Object> payload) {
        try {
            // Extract Group ID from flat key or nested object
            Long groupId = extractId(payload, "groupId", "chitGroup");
            // Extract Winner Member ID from flat key or nested object
            Long memberId = extractId(payload, "winnerMemberId", "winnerMember");

            if (groupId == null || memberId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Chit Group and Winner Member are required."));
            }

            ChitGroup chitGroup = chitGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Chit Group not found with ID: " + groupId));

            Member winnerMember = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found with ID: " + memberId));

            // Extract Bid Amount
            Double bidAmount = 0.0;
            if (payload.get("bidAmount") != null) {
                bidAmount = Double.valueOf(payload.get("bidAmount").toString());
            } else if (payload.get("winningBidAmount") != null) {
                bidAmount = Double.valueOf(payload.get("winningBidAmount").toString());
            }

            // Extract or default Auction Date
            LocalDate auctionDate = LocalDate.now();
            if (payload.get("auctionDate") != null && !payload.get("auctionDate").toString().isBlank()) {
                auctionDate = LocalDate.parse(payload.get("auctionDate").toString());
            }

            // Automated Financial Calculation Engine
            double chitAmount = (chitGroup.getChitAmount() != null) ? chitGroup.getChitAmount() : 0.0;
            int totalMembers = (chitGroup.getNumberOfMembers() != null && chitGroup.getNumberOfMembers() > 0)
                    ? chitGroup.getNumberOfMembers() : 1;
            double commissionPercent = (chitGroup.getCommission() != null) ? chitGroup.getCommission() : 5.0;

            double foremanCommission = (chitAmount * commissionPercent) / 100.0;
            double totalDiscount = chitAmount - bidAmount;
            double netDividendPool = totalDiscount - foremanCommission;
            double dividendPerMember = netDividendPool / totalMembers;
            double baseInstallment = chitAmount / totalMembers;
            double nextInstallment = baseInstallment - dividendPerMember;

            // Construct Auction Record
            Auction auction = new Auction();
            auction.setChitGroup(chitGroup);
            auction.setWinnerMember(winnerMember);
            auction.setBidAmount(bidAmount);
            auction.setAuctionDate(auctionDate);
            auction.setForemanCommission(foremanCommission);
            auction.setTotalDiscount(totalDiscount);
            auction.setDividendPerMember(dividendPerMember);
            auction.setNextInstallmentAmount(nextInstallment);

            Auction saved = auctionRepository.save(auction);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Error recording auction: " + e.getMessage()));
        }
    }

    // Update existing auction record
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAuction(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Auction auction = auctionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Auction not found with id: " + id));

            Long groupId = extractId(payload, "groupId", "chitGroup");
            Long memberId = extractId(payload, "winnerMemberId", "winnerMember");

            if (groupId != null) {
                ChitGroup group = chitGroupRepository.findById(groupId)
                        .orElseThrow(() -> new RuntimeException("Chit Group not found"));
                auction.setChitGroup(group);
            }

            if (memberId != null) {
                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Member not found"));
                auction.setWinnerMember(member);
            }

            Double bidAmount = auction.getBidAmount();
            if (payload.get("bidAmount") != null) {
                bidAmount = Double.valueOf(payload.get("bidAmount").toString());
            } else if (payload.get("winningBidAmount") != null) {
                bidAmount = Double.valueOf(payload.get("winningBidAmount").toString());
            }
            auction.setBidAmount(bidAmount);

            if (payload.get("auctionDate") != null && !payload.get("auctionDate").toString().isBlank()) {
                auction.setAuctionDate(LocalDate.parse(payload.get("auctionDate").toString()));
            }

            // Recalculate metrics
            ChitGroup group = auction.getChitGroup();
            double chitAmount = (group.getChitAmount() != null) ? group.getChitAmount() : 0.0;
            int totalMembers = (group.getNumberOfMembers() != null && group.getNumberOfMembers() > 0) ? group.getNumberOfMembers() : 1;
            double commissionPercent = (group.getCommission() != null) ? group.getCommission() : 5.0;

            double foremanCommission = (chitAmount * commissionPercent) / 100.0;
            double totalDiscount = chitAmount - bidAmount;
            double netDividendPool = totalDiscount - foremanCommission;
            double dividendPerMember = netDividendPool / totalMembers;
            double baseInstallment = chitAmount / totalMembers;
            double nextInstallment = baseInstallment - dividendPerMember;

            auction.setForemanCommission(foremanCommission);
            auction.setTotalDiscount(totalDiscount);
            auction.setDividendPerMember(dividendPerMember);
            auction.setNextInstallmentAmount(nextInstallment);

            return ResponseEntity.ok(auctionRepository.save(auction));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Error updating auction: " + e.getMessage()));
        }
    }

    // Delete auction record
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAuction(@PathVariable Long id) {
        auctionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Auction record deleted successfully."));
    }

    // Helper method to extract ID from either flat keys or nested JSON maps
    private Long extractId(Map<String, Object> payload, String flatKey, String nestedKey) {
        if (payload.get(flatKey) != null) {
            return Long.valueOf(payload.get(flatKey).toString());
        } else if (payload.get("memberId") != null && flatKey.contains("Member")) {
            return Long.valueOf(payload.get("memberId").toString());
        } else if (payload.get(nestedKey) instanceof Map) {
            Map<?, ?> nestedMap = (Map<?, ?>) payload.get(nestedKey);
            if (nestedMap.get("id") != null) {
                return Long.valueOf(nestedMap.get("id").toString());
            }
        }
        return null;
    }
}