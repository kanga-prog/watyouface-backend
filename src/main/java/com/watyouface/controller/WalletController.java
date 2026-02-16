package com.watyouface.controller;

import com.watyouface.dto.WalletDTO;
import com.watyouface.entity.Wallet;
import com.watyouface.security.Authz;
import com.watyouface.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final Authz authz;

    public WalletController(WalletService walletService, Authz authz) {
        this.walletService = walletService;
        this.authz = authz;
    }

    // ✅ Plus simple côté front
    @GetMapping("/me")
    public WalletDTO myWallet() {
        Long me = authz.me();
        Wallet w = walletService.getOrCreateWallet(me);
        return new WalletDTO(me, w.getBalance());
    }

    // ✅ Admin ou owner
    @GetMapping("/{userId}")
    public WalletDTO getWallet(@PathVariable Long userId) {
        authz.ownerOrAdmin(userId);
        Wallet w = walletService.getOrCreateWallet(userId);
        return new WalletDTO(userId, w.getBalance());
    }
}
