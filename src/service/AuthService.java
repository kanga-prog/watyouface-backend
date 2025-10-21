package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Enregistrement d'un nouvel utilisateur
    public String register(String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email déjà utilisé.";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
        return "Inscription réussie.";
    }

    // Connexion -> génération du token JWT
    public String login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Email ou mot de passe invalide.";
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Email ou mot de passe invalide.";
        }

        return jwtUtil.generateToken(user.getUsername());
    }
}
