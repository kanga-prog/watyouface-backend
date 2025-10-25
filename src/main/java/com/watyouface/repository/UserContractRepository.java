package com.watyouface.repository;

import com.watyouface.entity.UserContract;
import com.watyouface.entity.User;
import com.watyouface.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserContractRepository extends JpaRepository<UserContract, Long> {
    Optional<UserContract> findByUser(User user);

    boolean existsByUserAndContract(User user, Contract contract);

}
