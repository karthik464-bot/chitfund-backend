package com.chitfund.controller;

import com.chitfund.model.ChitGroup;
import com.chitfund.repository.ChitGroupRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class ChitGroupController {

    private final ChitGroupRepository repository;

    public ChitGroupController(ChitGroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ChitGroup> getAll(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return repository.findByGroupNameContainingIgnoreCase(search);
        }
        return repository.findAll();
    }

    @PostMapping
    public ChitGroup create(@RequestBody ChitGroup group) {
        return repository.save(group);
    }

    @PutMapping("/{id}")
    public ChitGroup update(@PathVariable Long id, @RequestBody ChitGroup details) {
        ChitGroup group = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ChitGroup not found with id: " + id));
        group.setGroupName(details.getGroupName());
        group.setChitAmount(details.getChitAmount());
        group.setMonthlyInstallment(details.getMonthlyInstallment());
        group.setNumberOfMembers(details.getNumberOfMembers());
        group.setDurationMonths(details.getDurationMonths());
        group.setStartDate(details.getStartDate());
        group.setStatus(details.getStatus());
        return repository.save(group);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}