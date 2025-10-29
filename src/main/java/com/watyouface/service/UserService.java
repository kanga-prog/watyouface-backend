// UserService.java
package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService { // ✅ ajouté

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔑 Nouvelle méthode obligatoire
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé: " + username);
        }
        User user = userOpt.get();
        // Retourne un UserDetails Spring avec les infos de ton entité
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // ⚠️ doit être non null
                Collections.emptyList()
        );
    }

    // ... tes autres méthodes existantes (getAllUsers, findByUsername, etc.)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}