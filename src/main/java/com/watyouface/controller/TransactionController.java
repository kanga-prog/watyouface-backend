package com.watyouface.controller;

import com.watyouface.dto.TransactionDTO;
import com.watyouface.entity.Transaction;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.security.Authz;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<TransactionDTO> all() {
        Long me = authz.me();

        List<Transaction> txs = authz.isAdmin()
                ? transactionRepository.findAll()
                : transactionRepository.findByFromUser_IdOrToUser_Id(me, me);

        return txs.stream().map(this::toDTO).toList();
    }

    private TransactionDTO toDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.id = tx.getId();
        dto.amount = tx.getAmount();
        dto.status = tx.getStatus().name();
        dto.fromUserId = tx.getFromUser().getId();
        dto.toUserId = tx.getToUser().getId();
        dto.listingId = tx.getListing().getId();
        dto.createdAt = tx.getCreatedAt();
        return dto;
    }
}
