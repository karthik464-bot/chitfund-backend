package com.chitfund.repository;

import com.chitfund.model.ChitGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChitGroupRepository extends JpaRepository<ChitGroup, Long> {
    List<ChitGroup> findByGroupNameContainingIgnoreCase(String name);
    long countByStatus(String status);
}