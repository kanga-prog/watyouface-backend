package com.watyouface.repository;

import com.watyouface.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // Cherche le contrat actif
    Optional<Contract> findByActiveTrue();
}
