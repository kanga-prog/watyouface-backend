package com.watyouface.controller;

import com.watyouface.repository.TransactionRepository;
import com.watyouface.security.Authz;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final Authz authz;

    public TransactionController(TransactionRepository transactionRepository, Authz authz) {
        this.transactionRepository = transactionRepository;
        this.authz = authz;
    }

    @GetMapping
    public Object all() {
        Long currentUserId = authz.me();
        boolean isAdmin = authz.isAdmin();

        if (isAdmin) {
            return transactionRepository.findAll();
        }
        return transactionRepository.findByFromUserIdOrToUserId(currentUserId, currentUserId);
    }
}
