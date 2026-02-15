package com.watyouface.controller;

import com.watyouface.dto.ListingDTO;
import com.watyouface.security.Authz;
import com.watyouface.service.MarketplaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketplace/listings")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;
    private final Authz authz;

    public MarketplaceController(MarketplaceService marketplaceService, Authz authz) {
        this.marketplaceService = marketplaceService;
        this.authz = authz;
    }

    // READ - toutes les annonces
    @GetMapping
    public List<ListingDTO> getAll() {
        return marketplaceService.findAll();
    }

    // READ - une annonce
    @GetMapping("/{id}")
    public ListingDTO getOne(@PathVariable Long id) {
        return marketplaceService.findById(id);
    }

    // CREATE (seller = currentUser)
    @PostMapping
    public ListingDTO create(@RequestBody ListingDTO dto) {
        Long sellerId = authz.me();
        return marketplaceService.createAsSeller(dto, sellerId);
    }

    // UPDATE (seulement seller ou admin, et seulement états modifiables)
    @PutMapping("/{id}")
    public ListingDTO update(@PathVariable Long id, @RequestBody ListingDTO dto) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        return marketplaceService.updateSecured(id, dto, me, isAdmin);
    }

    // DELETE (seller ou admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.deleteSecured(id, me, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Annonce supprimée"));
    }

    // Buyer "demande achat" => PENDING + buyer
    @PostMapping("/{id}/request")
    public ResponseEntity<?> requestPurchase(@PathVariable Long id) {
        Long buyerId = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.requestPurchase(id, buyerId, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Demande envoyée"));
    }

    // Seller accepte => ACCEPTED
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.accept(id, me, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Annonce acceptée"));
    }

    // Seller refuse => REFUSED
    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@PathVariable Long id) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.refuse(id, me, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Annonce refusée"));
    }

    // Buyer paye => PAID (seulement si ACCEPTED)
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id) {
        Long buyerId = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.paySecured(id, buyerId, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Paiement effectué"));
    }

    // (option) Seller ship => SHIPPED
    @PostMapping("/{id}/ship")
    public ResponseEntity<?> ship(@PathVariable Long id) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.ship(id, me, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Expédié"));
    }

    // (option) Buyer receive => RECEIVED
    @PostMapping("/{id}/receive")
    public ResponseEntity<?> receive(@PathVariable Long id) {
        Long buyerId = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.receive(id, buyerId, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Réception confirmée"));
    }
}
