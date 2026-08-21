package com.chitfund.controller;

import com.chitfund.model.ChitGroup;
import com.chitfund.model.Member;
import com.chitfund.repository.ChitGroupRepository;
import com.chitfund.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class ChitGroupController {

    private final ChitGroupRepository groupRepo;
    private final MemberRepository memberRepo;

    public ChitGroupController(ChitGroupRepository groupRepo, MemberRepository memberRepo) {
        this.groupRepo = groupRepo;
        this.memberRepo = memberRepo;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'MEMBER')")
    public ResponseEntity<List<ChitGroup>> getAllGroups(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(groupRepo.findByGroupNameContainingIgnoreCase(search));
        }
        return ResponseEntity.ok(groupRepo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<ChitGroup> createGroup(@RequestBody ChitGroup group) {
        return ResponseEntity.ok(groupRepo.save(group));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<ChitGroup> updateGroup(@PathVariable Long id, @RequestBody ChitGroup details) {
        ChitGroup group = groupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));

        group.setGroupName(details.getGroupName());
        group.setChitAmount(details.getChitAmount());
        group.setMonthlyInstallment(details.getMonthlyInstallment());
        group.setNumberOfMembers(details.getNumberOfMembers());
        group.setDurationMonths(details.getDurationMonths());
        group.setStartDate(details.getStartDate());
        group.setEndDate(details.getEndDate());
        group.setStatus(details.getStatus());

        return ResponseEntity.ok(groupRepo.save(group));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        ChitGroup group = groupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));

        // Unlink group from enrolled members prior to removal to avoid foreign key conflicts
        List<Member> members = memberRepo.findAll();
        for (Member member : members) {
            if (member.getChitGroups() != null && member.getChitGroups().contains(group)) {
                member.getChitGroups().remove(group);
                memberRepo.save(member);
            }
        }

        groupRepo.delete(group);
        return ResponseEntity.noContent().build();
    }
}