package com.chitfund.repository;

import com.chitfund.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    List<Collection> findByMemberMemberNameContainingIgnoreCase(String memberName);

    @Query("SELECT SUM(c.installmentAmount) FROM Collection c WHERE c.paymentStatus = 'PAID'")
    Double getTotalCollections();

    @Query("SELECT SUM(c.installmentAmount) FROM Collection c WHERE c.paymentStatus = 'PENDING'")
    Double getPendingCollections();
}