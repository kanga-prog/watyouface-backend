package com.watyouface.controller;

import com.watyouface.entity.User;
import com.watyouface.entity.Wallet;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.Authz;
import com.watyouface.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final Authz authz;

    public WalletController(WalletService walletService, UserRepository userRepository, Authz authz) {
        this.walletService = walletService;
        this.userRepository = userRepository;
        this.authz = authz;
    }

    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable Long userId) {
        authz.ownerOrAdmin(userId);

        User user = userRepository.findById(userId).orElseThrow();
        return walletService.getOrCreateWallet(user);
    }
}
