package com.watyouface.repository;
import java.util.List;

import com.watyouface.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromUserIdOrToUserId(Long fromUserId, Long toUserId);
}
