package com.watyouface.controller;

import com.watyouface.dto.ListingDTO;
import com.watyouface.security.JwtUtil;
import com.watyouface.service.MarketplaceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketplace/listings")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;
    private final JwtUtil jwtUtil;

    public MarketplaceController(MarketplaceService marketplaceService, JwtUtil jwtUtil) {
        this.marketplaceService = marketplaceService;
        this.jwtUtil = jwtUtil;
    }

    private String getAuthHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    private Long currentUserIdOr401(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return jwtUtil.getUserIdFromHeader(authHeader);
    }

    private boolean isAdmin(String authHeader) {
        return "ADMIN".equals(jwtUtil.getRoleFromHeader(authHeader));
    }

    // ✅ READ - toutes les annonces (auth requis)
    @GetMapping
    public List<ListingDTO> getAll() {
        return marketplaceService.findAll();
    }

    // ✅ READ - une annonce
    @GetMapping("/{id}")
    public ListingDTO getOne(@PathVariable Long id) {
        return marketplaceService.findById(id);
    }

    // ✅ CREATE (seller = user courant)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ListingDTO dto, HttpServletRequest request) {
        String authHeader = getAuthHeader(request);
        Long currentUserId = currentUserIdOr401(authHeader);
        if (currentUserId == null) return ResponseEntity.status(401).body("Token manquant/invalide");

        ListingDTO created = marketplaceService.createAs(dto, currentUserId);
        return ResponseEntity.ok(created);
    }

    // ✅ UPDATE (owner/admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ListingDTO dto,
                                    HttpServletRequest request) {
        String authHeader = getAuthHeader(request);
        Long currentUserId = currentUserIdOr401(authHeader);
        if (currentUserId == null) return ResponseEntity.status(401).body("Token manquant/invalide");

        boolean admin = isAdmin(authHeader);

        try {
            ListingDTO updated = marketplaceService.updateAs(id, dto, currentUserId, admin);
            return ResponseEntity.ok(updated);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // ✅ DELETE (owner/admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        String authHeader = getAuthHeader(request);
        Long currentUserId = currentUserIdOr401(authHeader);
        if (currentUserId == null) return ResponseEntity.status(401).body("Token manquant/invalide");

        boolean admin = isAdmin(authHeader);

        try {
            marketplaceService.deleteAs(id, currentUserId, admin);
            return ResponseEntity.noContent().build();
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // ✅ PAY (buyer = user courant) -> on supprime buyerId param
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id, HttpServletRequest request) {
        String authHeader = getAuthHeader(request);
        Long currentUserId = currentUserIdOr401(authHeader);
        if (currentUserId == null) return ResponseEntity.status(401).body("Token manquant/invalide");

        boolean admin = isAdmin(authHeader);

        try {
            marketplaceService.payAs(id, currentUserId, admin);
            return ResponseEntity.ok(Map.of("message", "Paiement effectué"));
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
