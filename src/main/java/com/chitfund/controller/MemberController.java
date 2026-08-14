package com.chitfund.controller;

import com.chitfund.model.ChitGroup;
import com.chitfund.model.Member;
import com.chitfund.repository.ChitGroupRepository;
import com.chitfund.repository.MemberRepository;
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
    public List<Member> getAll(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return memberRepo.findByMemberNameContainingIgnoreCase(search);
        }
        return memberRepo.findAll();
    }

    @PostMapping
    public Member create(@RequestBody Member member) {
        return memberRepo.save(member);
    }

    @PutMapping("/{id}")
    public Member update(@PathVariable Long id, @RequestBody Member details) {
        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        member.setMemberName(details.getMemberName());
        member.setMobileNumber(details.getMobileNumber());
        member.setEmailAddress(details.getEmailAddress());
        member.setAddress(details.getAddress());
        return memberRepo.save(member);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        memberRepo.deleteById(id);
    }

    // Module 5: Member Enrollment Endpoints
    @PostMapping("/{memberId}/enroll/{groupId}")
    public Member enrollGroup(@PathVariable Long memberId, @PathVariable Long groupId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        ChitGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        member.getChitGroups().add(group);
        return memberRepo.save(member);
    }

    @DeleteMapping("/{memberId}/unenroll/{groupId}")
    public Member unenrollGroup(@PathVariable Long memberId, @PathVariable Long groupId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        ChitGroup group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        member.getChitGroups().remove(group);
        return memberRepo.save(member);
    }
}