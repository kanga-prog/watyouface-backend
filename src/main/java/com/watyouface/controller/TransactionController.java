package com.watyouface.controller;

import com.watyouface.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public Object all() {
        return transactionRepository.findAll();
    }
}
