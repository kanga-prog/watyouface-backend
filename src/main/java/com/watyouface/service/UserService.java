// UserService.java
package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 🔹 Chargement d’un utilisateur par ID (utilisé dans JwtAuthenticationFilter)
     */
    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'id: " + userId));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

   @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email); // <- email
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé: " + email);
        }

        User user = userOpt.get();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // ou username, à toi de décider
                user.getPassword(),
                Collections.emptyList()
        );
    }

    /**
     * 🔹 Méthodes utilitaires métiers
     */
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
