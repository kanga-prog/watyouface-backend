package com.watyouface.controller;

import com.watyouface.dto.ListingDTO;
import com.watyouface.service.MarketplaceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace/listings")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    public MarketplaceController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    // ✅ READ - toutes les annonces
    @GetMapping
    public List<ListingDTO> getAll() {
        return marketplaceService.findAll();
    }

    // ✅ READ - une annonce
    @GetMapping("/{id}")
    public ListingDTO getOne(@PathVariable Long id) {
        return marketplaceService.findById(id);
    }

    // ✅ CREATE
    @PostMapping
    public ListingDTO create(@RequestBody ListingDTO dto) {
        return marketplaceService.create(dto);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ListingDTO update(
            @PathVariable Long id,
            @RequestBody ListingDTO dto
    ) {
        return marketplaceService.update(id, dto);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        marketplaceService.delete(id);
    }

    // ✅ PAY
    @PostMapping("/{id}/pay")
    public void pay(
            @PathVariable Long id,
            @RequestParam Long buyerId
    ) {
        marketplaceService.pay(id, buyerId);
    }
}
