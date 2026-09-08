package com.chitfund.controller;

import com.chitfund.model.Collection;
import com.chitfund.model.Member;
import com.chitfund.model.ChitGroup;
import com.chitfund.repository.CollectionRepository;
import com.chitfund.repository.MemberRepository;
import com.chitfund.repository.ChitGroupRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collections")
@CrossOrigin(origins = "*")
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final MemberRepository memberRepository;
    private final ChitGroupRepository chitGroupRepository;

    public CollectionController(CollectionRepository collectionRepository,
                                MemberRepository memberRepository,
                                ChitGroupRepository chitGroupRepository) {
        this.collectionRepository = collectionRepository;
        this.memberRepository = memberRepository;
        this.chitGroupRepository = chitGroupRepository;
    }

    @GetMapping
    public ResponseEntity<List<Collection>> getAll() {
        return ResponseEntity.ok(collectionRepository.findAll());
    }

    @GetMapping("/agent/{agentUserId}")
    public ResponseEntity<List<Collection>> getAgentCollections(@PathVariable Long agentUserId) {
        List<Member> assignedMembers = memberRepository.findByAssignedAgentId(agentUserId);
        List<Long> memberIds = assignedMembers.stream().map(Member::getId).toList();

        List<Collection> collections = collectionRepository.findByMemberIdIn(memberIds);
        return ResponseEntity.ok(collections);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            Long memberId = extractId(payload, "memberId", "member");
            Long groupId = extractId(payload, "chitGroupId", "groupId", "chitGroup");

            if (memberId == null || groupId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Member and Chit Group are required."));
            }

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

            ChitGroup chitGroup = chitGroupRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("ChitGroup not found with id: " + groupId));

            Double amount = 0.0;
            if (payload.get("amount") != null) {
                amount = Double.valueOf(payload.get("amount").toString());
            } else if (payload.get("payableAmount") != null) {
                amount = Double.valueOf(payload.get("payableAmount").toString());
            }

            LocalDate paymentDate = LocalDate.now();
            if (payload.get("paymentDate") != null && !payload.get("paymentDate").toString().isBlank()) {
                paymentDate = LocalDate.parse(payload.get("paymentDate").toString());
            }

            String paymentMode = payload.get("paymentMode") != null ? payload.get("paymentMode").toString() : "CASH";
            String status = payload.get("status") != null ? payload.get("status").toString() : "PAID";

            Collection collection = new Collection();
            collection.setMember(member);
            collection.setChitGroup(chitGroup);
            collection.setAmount(amount);
            collection.setPaymentDate(paymentDate);
            collection.setPaymentMode(paymentMode);
            collection.setStatus(status);

            Collection saved = collectionRepository.save(collection);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Error recording collection: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Collection collection = collectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Collection record not found with id: " + id));

            Long memberId = extractId(payload, "memberId", "member");
            Long groupId = extractId(payload, "chitGroupId", "groupId", "chitGroup");

            if (memberId != null) {
                Member member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
                collection.setMember(member);
            }

            if (groupId != null) {
                ChitGroup chitGroup = chitGroupRepository.findById(groupId)
                        .orElseThrow(() -> new RuntimeException("ChitGroup not found with id: " + groupId));
                collection.setChitGroup(chitGroup);
            }

            if (payload.get("amount") != null) {
                collection.setAmount(Double.valueOf(payload.get("amount").toString()));
            }
            if (payload.get("paymentDate") != null && !payload.get("paymentDate").toString().isBlank()) {
                collection.setPaymentDate(LocalDate.parse(payload.get("paymentDate").toString()));
            }
            if (payload.get("paymentMode") != null) {
                collection.setPaymentMode(payload.get("paymentMode").toString());
            }
            if (payload.get("status") != null) {
                collection.setStatus(payload.get("status").toString());
            }

            return ResponseEntity.ok(collectionRepository.save(collection));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", "Error updating collection: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        collectionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Collection record deleted successfully."));
    }

    private Long extractId(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Object val = payload.get(key);
            if (val != null) {
                if (val instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) val;
                    if (map.get("id") != null) {
                        return Long.valueOf(map.get("id").toString());
                    }
                } else {
                    try {
                        return Long.valueOf(val.toString());
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return null;
    }
}