package com.watyouface.repository;

import com.watyouface.entity.Wallet;
import com.watyouface.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
    Optional<Wallet> findByUser_Id(Long userId);
}
