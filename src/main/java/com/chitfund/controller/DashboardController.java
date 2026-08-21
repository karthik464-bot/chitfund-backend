package com.chitfund.controller;

import com.chitfund.repository.AuctionRepository;
import com.chitfund.repository.ChitGroupRepository;
import com.chitfund.repository.CollectionRepository;
import com.chitfund.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final ChitGroupRepository groupRepo;
    private final MemberRepository memberRepo;
    private final CollectionRepository collectionRepo;
    private final AuctionRepository auctionRepo;

    public DashboardController(ChitGroupRepository groupRepo,
                               MemberRepository memberRepo,
                               CollectionRepository collectionRepo,
                               AuctionRepository auctionRepo) {
        this.groupRepo = groupRepo;
        this.memberRepo = memberRepo;
        this.collectionRepo = collectionRepo;
        this.auctionRepo = auctionRepo;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Group and Member Counts
        long totalGroups = groupRepo.count();
        long totalMembers = memberRepo.count();

        stats.put("activeGroupsCount", totalGroups);
        stats.put("totalMembersCount", totalMembers);
        stats.put("totalChitGroups", totalGroups);
        stats.put("totalMembers", totalMembers);

        // Dynamic Collection Totals with null safety
        Double totalColl = collectionRepo.getTotalCollections();
        Double pendingColl = collectionRepo.getPendingCollections();

        double totalCollectionsAmount = (totalColl != null) ? totalColl : 0.0;
        double pendingCollectionsAmount = (pendingColl != null) ? pendingColl : 0.0;

        stats.put("totalCollectionsAmount", totalCollectionsAmount);
        stats.put("totalMonthlyCollections", totalCollectionsAmount);
        stats.put("pendingCollections", pendingCollectionsAmount);

        // Default or aggregated total scheme value
        stats.put("totalChitAmount", 0.0);

        // Recent Auctions list
        stats.put("recentAuctions", auctionRepo.findAll());

        return ResponseEntity.ok(stats);
    }
}