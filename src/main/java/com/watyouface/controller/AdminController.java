package com.watyouface.controller;

import com.watyouface.dto.AdminCommentDTO;
import com.watyouface.dto.AdminListingDTO;
import com.watyouface.dto.AdminPostDTO;
import com.watyouface.dto.AdminUserDTO;
import com.watyouface.dto.WalletCreditRequest;
import com.watyouface.dto.WalletDTO;
import com.watyouface.security.Authz;
import com.watyouface.service.AdminModerationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final Authz authz;
    private final AdminModerationService adminService;

    public AdminController(Authz authz, AdminModerationService adminService) {
        this.authz = authz;
        this.adminService = adminService;
    }

    private void requireAdmin() {
        if (!authz.isAdmin()) throw new SecurityException("Admin only");
    }

    @GetMapping("/users")
    public Page<AdminUserDTO> users(@RequestParam(defaultValue = "") String query,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        requireAdmin();
        return adminService.users(query, page, size);
    }

    @GetMapping("/posts")
    public Page<AdminPostDTO> posts(@RequestParam(defaultValue = "") String query,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        requireAdmin();
        return adminService.posts(query, page, size);
    }

    @GetMapping("/comments")
    public Page<AdminCommentDTO> comments(@RequestParam(defaultValue = "") String query,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        requireAdmin();
        return adminService.comments(query, page, size);
    }

    @GetMapping("/listings")
    public Page<AdminListingDTO> listings(@RequestParam(defaultValue = "") String query,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        requireAdmin();
        return adminService.listings(query, page, size);
    }

    @PostMapping("/users/{userId}/wallet/credit")
    public WalletDTO creditWallet(@PathVariable Long userId,
                                  @Valid @RequestBody WalletCreditRequest body) {
        requireAdmin();
        return adminService.creditWallet(userId, body.getAmount());
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        requireAdmin();
        adminService.deletePost(id);
        return ResponseEntity.ok(Map.of("message", "Post supprimé"));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        requireAdmin();
        adminService.deleteComment(id);
        return ResponseEntity.ok(Map.of("message", "Commentaire supprimé"));
    }

    @DeleteMapping("/listings/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        requireAdmin();
        adminService.deleteListing(id);
        return ResponseEntity.ok(Map.of("message", "Annonce supprimée"));
    }
}
