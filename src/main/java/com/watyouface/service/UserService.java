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
import com.watyouface.entity.enums.Role;
import com.watyouface.exception.ResourceNotFoundException;
import com.watyouface.media.MediaReferenceCleanupService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private AccountErasureService accountErasureService;

    @Autowired
    private MediaReferenceCleanupService mediaCleanup;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔹 Charger utilisateur pour JWT Authentication
    public UserDetails loadUserById(Long userId) {
        @SuppressWarnings("null")
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'id: " + userId));
        if (user.isDeleted()) {
            throw new UsernameNotFoundException("Utilisateur supprimé");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé: " + email);
        }

        User user = userOpt.get();
        if (user.isDeleted()) {
            throw new UsernameNotFoundException("Utilisateur supprimé");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    // 🔹 Méthodes pour Avatar
    public User updateAvatar(Long userId, MultipartFile file) throws IOException {
        @SuppressWarnings("null")
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) return null;

        User user = optUser.get();
        String url = avatarService.saveAvatar(file, userId);
        user.setAvatarUrl(url);
        return userRepository.save(user);
    }

    @Transactional
    public User updateAvatarUrl(Long userId, String avatarUrl) {
        @SuppressWarnings("null")
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) return null;

        User user = optUser.get();
        String previousAvatarUrl = user.getAvatarUrl();
        user.setAvatarUrl(avatarUrl);
        User saved = userRepository.save(user);
        if (mediaCleanup != null) {
            mediaCleanup.deleteIfUnreferencedAfterCommit(java.util.stream.Stream.of(previousAvatarUrl)
                    .filter(java.util.Objects::nonNull).toList());
        }
        return saved;
    }

    // 🔹 CRUD utilisateurs
    @SuppressWarnings("null")
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findByDeletedAtIsNull();
    }

    @SuppressWarnings("null")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @SuppressWarnings("null")
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @SuppressWarnings("null")
    public void deleteUserSecured(Long id, Long actorId, boolean actorIsAdmin) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

        if (target.getRole() == Role.ADMIN) {
            if (target.getId().equals(actorId)) {
                throw new IllegalStateException("Un administrateur ne peut pas supprimer son propre compte");
            }
            if (userRepository.countByRole(Role.ADMIN) <= 1) {
                throw new IllegalStateException("Le dernier administrateur ne peut pas être supprimé");
            }
        }

        if (!actorIsAdmin && !target.getId().equals(actorId)) {
            throw new SecurityException("Interdit");
        }
        accountErasureService.erase(id);
    }
     @SuppressWarnings("null")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
