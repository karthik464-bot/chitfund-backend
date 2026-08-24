package com.chitfund.repository;

import com.chitfund.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

    List<Collection> findByMemberNameContainingIgnoreCase(String name);

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Collection c WHERE c.status = 'PAID'")
    Double getTotalCollections();

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Collection c WHERE c.status = 'PENDING'")
    Double getPendingCollections();
}