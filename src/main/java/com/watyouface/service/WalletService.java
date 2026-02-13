package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.entity.Wallet;
import com.watyouface.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    return walletRepository.save(wallet);
                });
    }

    @Transactional
    public void transfer(User buyer, User seller, Double amount) {
        Wallet buyerWallet = getOrCreateWallet(buyer);
        Wallet sellerWallet = getOrCreateWallet(seller);

        buyerWallet.debit(amount);
        sellerWallet.credit(amount);
    }
}
