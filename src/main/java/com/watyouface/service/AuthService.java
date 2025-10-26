package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.entity.Contract;
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

    @Autowired
    private ContractService contractService;

    // ========================
    // Connexion -> génération du token JWT
    // ========================
    public String login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Email ou mot de passe invalide.";
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Email ou mot de passe invalide.";
        }

        // 🔒 Bloquer la connexion si contrat non accepté
        if (!user.isAcceptedContract()) {
            return "Veuillez accepter le contrat WatYouFace pour vous connecter.";
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    // ========================
    // Enregistrement : toujours sauvegarder l'utilisateur
    // ========================
    public User register(String username, String email, String password, boolean acceptedContract) {
        // Vérifications
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email déjà utilisé.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Nom d'utilisateur déjà pris.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAcceptedContract(acceptedContract); // false si pas encore accepté

        // Si accepté maintenant, lie le contrat actif
        if (acceptedContract) {
            Optional<Contract> activeContractOpt = contractService.getActiveContract();
            if (activeContractOpt.isEmpty()) {
                throw new RuntimeException("Aucun contrat actif n’est disponible.");
            }
            user.setAcceptedContractVersion(activeContractOpt.get());
        }
        // Sinon : acceptedContractVersion reste null → OK

        return userRepository.save(user); // 👈 Toujours sauvegardé
    }
}