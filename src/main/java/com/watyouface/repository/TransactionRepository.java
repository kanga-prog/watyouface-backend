package com.watyouface.repository;

import com.watyouface.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromUser_IdOrToUser_Id(Long fromUserId, Long toUserId);

    boolean existsByListing_Id(Long listingId);
}
