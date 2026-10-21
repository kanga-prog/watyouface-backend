package com.watyouface.repository;

import com.watyouface.entity.UserContract;
import com.watyouface.entity.User;
import com.watyouface.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserContractRepository extends JpaRepository<UserContract, Long> {
    Optional<UserContract> findByUser(User user);

    List<UserContract> findAllByUser_Id(Long userId);

    boolean existsByUserAndContract(User user, Contract contract);

}
