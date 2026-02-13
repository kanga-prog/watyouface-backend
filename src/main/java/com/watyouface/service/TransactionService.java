package com.watyouface.service;

import com.watyouface.entity.Listing;
import com.watyouface.entity.Transaction;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.TransactionStatus;
import com.watyouface.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public TransactionService(
            TransactionRepository transactionRepository,
            WalletService walletService
    ) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Transactional
    public Transaction transfer(User buyer, User seller, Listing listing) {

        // 💰 Wallet transfer
        walletService.transfer(buyer, seller, listing.getPrice());

        // 🧾 Transaction persistée
        Transaction tx = new Transaction();
        tx.setFromUser(buyer);
        tx.setToUser(seller);
        tx.setAmount(listing.getPrice());
        tx.setListing(listing);
        tx.setStatus(TransactionStatus.COMPLETED);

        return transactionRepository.save(tx);
    }
}
