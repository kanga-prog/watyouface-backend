package com.watyouface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsername(String username);
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable);
    long countByRole(Role role);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    List<User> findByDeletedAtIsNull();
    boolean existsByAvatarUrl(String avatarUrl);
}
