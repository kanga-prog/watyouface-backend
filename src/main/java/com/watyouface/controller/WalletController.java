package com.watyouface.controller;

import com.watyouface.dto.WalletDTO;
import com.watyouface.entity.Wallet;
import com.watyouface.security.Authz;
import com.watyouface.service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /**
     * ✅ Recharge wallet (mode démo)
     * Body: {"amount": 100}
     */
    @PostMapping("/me/credit")
    public WalletDTO creditMe(@RequestBody Map<String, Object> body) {
        Long me = authz.me();
        Double amount = null;
        Object v = body.get("amount");
        if (v instanceof Number n) amount = n.doubleValue();
        if (amount == null) throw new IllegalArgumentException("amount requis");

        Wallet w = walletService.credit(me, amount);
        return new WalletDTO(me, w.getBalance());
    }
}
