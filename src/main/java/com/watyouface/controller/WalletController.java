package com.watyouface.controller;

import com.watyouface.entity.User;
import com.watyouface.entity.Wallet;
import com.watyouface.repository.UserRepository;
import com.watyouface.service.WalletService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    public WalletController(WalletService walletService, UserRepository userRepository) {
        this.walletService = walletService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public Wallet getWallet(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return walletService.getOrCreateWallet(user);
    }
}
