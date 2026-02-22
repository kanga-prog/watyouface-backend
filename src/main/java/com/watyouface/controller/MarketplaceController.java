package com.watyouface.controller;

import com.watyouface.dto.ListingDTO;
import com.watyouface.media.MarketplaceImageService;
import com.watyouface.security.Authz;
import com.watyouface.service.MarketplaceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketplace/listings")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;
    private final Authz authz;
    private final MarketplaceImageService marketplaceImageService;

    public MarketplaceController(MarketplaceService marketplaceService,
                                 Authz authz,
                                 MarketplaceImageService marketplaceImageService) {
        this.marketplaceService = marketplaceService;
        this.authz = authz;
        this.marketplaceImageService = marketplaceImageService;
    }

    @GetMapping
    public List<ListingDTO> getAll() {
        return marketplaceService.findAll();
    }

    @GetMapping("/{id}")
    public ListingDTO getOne(@PathVariable Long id) {
        return marketplaceService.findById(id);
    }

    @PostMapping
    public ListingDTO create(@RequestBody ListingDTO dto) {
        Long sellerId = authz.me();
        return marketplaceService.createAsSeller(dto, sellerId);
    }

    @PutMapping("/{id}")
    public ListingDTO update(@PathVariable Long id, @RequestBody ListingDTO dto) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        return marketplaceService.updateSecured(id, dto, me, isAdmin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.deleteSecured(id, me, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Annonce supprimée"));
    }

    @PostMapping("/{id}/request")
    public ResponseEntity<?> requestPurchase(@PathVariable Long id) {
        Long buyerId = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.requestPurchase(id, buyerId, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Demande envoyée"));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.accept(id, me, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Annonce acceptée"));
    }

    @PostMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@PathVariable Long id) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.refuse(id, me, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Annonce refusée"));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id) {
        Long buyerId = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.paySecured(id, buyerId, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Paiement effectué"));
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<?> ship(@PathVariable Long id) {
        Long me = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.ship(id, me, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Expédié"));
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<?> receive(@PathVariable Long id) {
        Long buyerId = authz.me();
        boolean isAdmin = authz.isAdmin();
        marketplaceService.receive(id, buyerId, isAdmin);
        return ResponseEntity.ok(Map.of("message", "Réception confirmée"));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadListingImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        String imageUrl = marketplaceImageService.saveListingImage(file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}