package com.chitfund.controller;

import com.chitfund.model.Collection;
import com.chitfund.model.Member;
import com.chitfund.model.ChitGroup;
import com.chitfund.repository.CollectionRepository;
import com.chitfund.repository.MemberRepository;
import com.chitfund.repository.ChitGroupRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@CrossOrigin(origins = "http://localhost:3000")
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
    public List<Collection> getAll() {
        return collectionRepository.findAll();
    }

    @PostMapping
    public Collection create(@RequestBody Collection collection) {
        if (collection.getMember() != null && collection.getMember().getId() != null) {
            Member member = memberRepository.findById(collection.getMember().getId())
                    .orElseThrow(() -> new RuntimeException("Member not found with id: " + collection.getMember().getId()));
            collection.setMember(member);
        }
        if (collection.getChitGroup() != null && collection.getChitGroup().getId() != null) {
            ChitGroup chitGroup = chitGroupRepository.findById(collection.getChitGroup().getId())
                    .orElseThrow(() -> new RuntimeException("ChitGroup not found with id: " + collection.getChitGroup().getId()));
            collection.setChitGroup(chitGroup);
        }
        return collectionRepository.save(collection);
    }

    @PutMapping("/{id}")
    public Collection update(@PathVariable Long id, @RequestBody Collection details) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection record not found with id: " + id));

        if (details.getMember() != null && details.getMember().getId() != null) {
            Member member = memberRepository.findById(details.getMember().getId())
                    .orElseThrow(() -> new RuntimeException("Member not found with id: " + details.getMember().getId()));
            collection.setMember(member);
        }
        if (details.getChitGroup() != null && details.getChitGroup().getId() != null) {
            ChitGroup chitGroup = chitGroupRepository.findById(details.getChitGroup().getId())
                    .orElseThrow(() -> new RuntimeException("ChitGroup not found with id: " + details.getChitGroup().getId()));
            collection.setChitGroup(chitGroup);
        }

        collection.setAmount(details.getAmount());
        collection.setPaymentDate(details.getPaymentDate());
        collection.setPaymentMode(details.getPaymentMode());
        collection.setStatus(details.getStatus());
        return collectionRepository.save(collection);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        collectionRepository.deleteById(id);
    }
}