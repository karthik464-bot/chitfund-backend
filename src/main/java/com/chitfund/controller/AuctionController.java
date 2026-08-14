package com.chitfund.controller;

import com.chitfund.model.Auction;
import com.chitfund.repository.AuctionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
@CrossOrigin(origins = "*")
public class AuctionController {

    private final AuctionRepository repository;

    public AuctionController(AuctionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Auction> getAll(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return repository.findByChitGroupGroupNameContainingIgnoreCase(search);
        }
        return repository.findAll();
    }

    @PostMapping
    public Auction create(@RequestBody Auction auction) {
        return repository.save(auction);
    }

    @PutMapping("/{id}")
    public Auction update(@PathVariable Long id, @RequestBody Auction details) {
        Auction auction = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found with id: " + id));
        auction.setChitGroup(details.getChitGroup());
        auction.setWinnerMember(details.getWinnerMember());
        auction.setBidAmount(details.getBidAmount());
        auction.setAuctionDate(details.getAuctionDate());
        return repository.save(auction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}