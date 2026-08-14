package com.chitfund.controller;

import com.chitfund.model.Collection;
import com.chitfund.repository.CollectionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@CrossOrigin(origins = "*")
public class CollectionController {

    private final CollectionRepository repository;

    public CollectionController(CollectionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Collection> getAll(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return repository.findByMemberMemberNameContainingIgnoreCase(search);
        }
        return repository.findAll();
    }

    @PostMapping
    public Collection create(@RequestBody Collection collection) {
        return repository.save(collection);
    }

    @PutMapping("/{id}")
    public Collection update(@PathVariable Long id, @RequestBody Collection details) {
        Collection collection = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection record not found with id: " + id));
        collection.setMember(details.getMember());
        collection.setChitGroup(details.getChitGroup());
        collection.setInstallmentAmount(details.getInstallmentAmount());
        collection.setPaymentDate(details.getPaymentDate());
        collection.setPaymentMode(details.getPaymentMode());
        collection.setPaymentStatus(details.getPaymentStatus());
        return repository.save(collection);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}