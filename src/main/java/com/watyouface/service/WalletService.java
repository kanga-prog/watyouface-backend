package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.entity.Wallet;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    wallet.setBalance(0.0);
                    return walletRepository.save(wallet);
                });
    }

    public Wallet getOrCreateWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return getOrCreateWallet(user);
    }

    @Transactional
    public void transfer(User buyer, User seller, Double amount) {
        Wallet buyerWallet = getOrCreateWallet(buyer);
        Wallet sellerWallet = getOrCreateWallet(seller);

        buyerWallet.debit(amount);
        sellerWallet.credit(amount);
        // Pas besoin de save() : managed entities + transaction => flush auto
    }
}
