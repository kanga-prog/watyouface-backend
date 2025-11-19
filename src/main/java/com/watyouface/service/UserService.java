package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.watyouface.media.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private AvatarService avatarService;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔹 Charger utilisateur pour JWT Authentication
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
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé: " + email);
        }

        User user = userOpt.get();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    // 🔹 Méthodes pour Avatar
    public User updateAvatar(Long userId, MultipartFile file) throws IOException {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) return null;

        User user = optUser.get();
        String url = avatarService.saveAvatar(file, userId);
        user.setAvatarUrl(url);
        return userRepository.save(user);
    }

    public User updateAvatarUrl(Long userId, String avatarUrl) {
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) return null;

        User user = optUser.get();
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }

    // 🔹 CRUD utilisateurs
    public User saveUser(User user) {
        return userRepository.save(user);
    }

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
     public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
