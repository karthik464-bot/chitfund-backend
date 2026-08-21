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
@RequestMapping("/api/member")
@CrossOrigin(origins = "*")
public class MemberPortalController {

    private final ChitGroupRepository groupRepo;
    private final MemberRepository memberRepo;
    private final CollectionRepository collectionRepo;
    private final AuctionRepository auctionRepo;

    public MemberPortalController(ChitGroupRepository groupRepo,
                                  MemberRepository memberRepo,
                                  CollectionRepository collectionRepo,
                                  AuctionRepository auctionRepo) {
        this.groupRepo = groupRepo;
        this.memberRepo = memberRepo;
        this.collectionRepo = collectionRepo;
        this.auctionRepo = auctionRepo;
    }

    @GetMapping("/dashboard/{memberId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getMemberDashboard(@PathVariable Long memberId) {
        Map<String, Object> response = new HashMap<>();

        // Returns enrolled groups, past auctions, and payment receipts
        response.put("groups", groupRepo.findAll());
        response.put("auctions", auctionRepo.findAll());
        response.put("collections", collectionRepo.findAll());

        return ResponseEntity.ok(response);
    }
}