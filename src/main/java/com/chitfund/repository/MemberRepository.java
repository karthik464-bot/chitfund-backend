package com.chitfund.repository;

import com.chitfund.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByMemberNameContainingIgnoreCase(String memberName);
    List<Member> findByNameContainingIgnoreCase(String name);
}