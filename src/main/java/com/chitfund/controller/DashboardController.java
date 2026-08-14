package com.chitfund.controller;

import com.chitfund.repository.AuctionRepository;
import com.chitfund.repository.ChitGroupRepository;
import com.chitfund.repository.CollectionRepository;
import com.chitfund.repository.MemberRepository;
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

    // Spring automatically injects the existing 4 repositories here
    public DashboardController(ChitGroupRepository groupRepo, MemberRepository memberRepo,
                               CollectionRepository collectionRepo, AuctionRepository auctionRepo) {
        this.groupRepo = groupRepo;
        this.memberRepo = memberRepo;
        this.collectionRepo = collectionRepo;
        this.auctionRepo = auctionRepo;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // Pulling counts directly from ChitGroupRepository & MemberRepository
        stats.put("totalChitGroups", groupRepo.count());
        stats.put("totalMembers", memberRepo.count());
        stats.put("activeChitGroups", groupRepo.countByStatus("ACTIVE"));
        stats.put("completedChitGroups", groupRepo.countByStatus("COMPLETED"));

        // Pulling totals directly from CollectionRepository
        Double totalColl = collectionRepo.getTotalCollections();
        Double pendingColl = collectionRepo.getPendingCollections();

        stats.put("totalMonthlyCollections", totalColl != null ? totalColl : 0.0);
        stats.put("pendingCollections", pendingColl != null ? pendingColl : 0.0);

        // Pulling recent auctions from AuctionRepository
        stats.put("recentAuctions", auctionRepo.findAll());

        return stats;
    }
}