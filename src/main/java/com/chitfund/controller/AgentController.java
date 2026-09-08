package com.chitfund.controller;

import com.chitfund.model.Member;
import com.chitfund.model.User;
import com.chitfund.repository.MemberRepository;
import com.chitfund.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/agents")
@CrossOrigin(origins = "*")
public class AgentController {

    private final UserRepository userRepo;
    private final MemberRepository memberRepo;

    public AgentController(UserRepository userRepo, MemberRepository memberRepo) {
        this.userRepo = userRepo;
        this.memberRepo = memberRepo;
    }

    // Get all registered Agents
    @GetMapping
    public ResponseEntity<List<User>> getAllAgents() {
        return ResponseEntity.ok(userRepo.findByRoleName("ROLE_AGENT"));
    }

    // Assign Member to Agent
    @PostMapping("/assign")
    public ResponseEntity<?> assignAgentToMember(@RequestBody Map<String, Long> payload) {
        Long agentId = payload.get("agentId");
        Long memberId = payload.get("memberId");

        User agent = userRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setAssignedAgent(agent);
        memberRepo.save(member);

        return ResponseEntity.ok(Map.of("message", "Member assigned to Agent successfully!"));
    }

    // Get Members assigned to a specific Agent
    @GetMapping("/{agentId}/members")
    public ResponseEntity<List<Member>> getAgentMembers(@PathVariable Long agentId) {
        return ResponseEntity.ok(memberRepo.findByAssignedAgentId(agentId));
    }
}