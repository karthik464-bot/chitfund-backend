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
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberRepository memberRepo;
    private final ChitGroupRepository groupRepo;

    public MemberController(MemberRepository memberRepo, ChitGroupRepository groupRepo) {
        this.memberRepo = memberRepo;
        this.groupRepo = groupRepo;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'MEMBER', 'ROLE_ADMIN', 'ROLE_AGENT', 'ROLE_MEMBER')")
    public ResponseEntity<List<Member>> getAllMembers(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(memberRepo.findByNameContainingIgnoreCase(search));
        }
        return ResponseEntity.ok(memberRepo.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        return ResponseEntity.ok(memberRepo.save(member));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member details) {
        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));

        if (details.getName() != null) {
            member.setName(details.getName());
        }

        member.setMobileNumber(details.getMobileNumber());
        member.setEmailAddress(details.getEmailAddress());
        member.setAddress(details.getAddress());

        return ResponseEntity.ok(memberRepo.save(member));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));

        // Unlink enrolled chit groups to prevent foreign key errors
        if (member.getChitGroups() != null) {
            member.getChitGroups().clear();
            memberRepo.save(member);
        }

        memberRepo.delete(member);
        return ResponseEntity.noContent().build();
    }

    // Maps both /groups/{groupId} (React default) and /enroll/{groupId}
    @PostMapping({ "/{memberId}/groups/{groupId}", "/{memberId}/enroll/{groupId}" })
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'ROLE_ADMIN', 'ROLE_AGENT')")
    public ResponseEntity<Member> enrollGroup(@PathVariable Long memberId, @PathVariable Long groupId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        ChitGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        member.getChitGroups().add(group);
        return ResponseEntity.ok(memberRepo.save(member));
    }

    // Maps both /groups/{groupId} and /unenroll/{groupId}
    @DeleteMapping({ "/{memberId}/groups/{groupId}", "/{memberId}/unenroll/{groupId}" })
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'ROLE_ADMIN', 'ROLE_AGENT')")
    public ResponseEntity<Member> unenrollGroup(@PathVariable Long memberId, @PathVariable Long groupId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        ChitGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        member.getChitGroups().remove(group);
        return ResponseEntity.ok(memberRepo.save(member));
    }
}