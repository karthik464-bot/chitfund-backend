package com.chitfund.repository;

import com.chitfund.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByChitGroupGroupNameContainingIgnoreCase(String groupName);
}