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

        return jwtUtil.generateToken(user.getUsername());
    }

    // ========================
    // Enregistrement d'un nouvel utilisateur
    // avec acceptation du contrat
    // ========================
    public String register(String username, String email, String password, boolean acceptedContract) {
        if (!acceptedContract) {
            return "Vous devez accepter les conditions générales avant de vous inscrire.";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return "Email déjà utilisé.";
        }

        Optional<Contract> activeContractOpt = contractService.getActiveContract();
        if (activeContractOpt.isEmpty()) {
            return "Aucun contrat actif n’est disponible.";
        }
        Contract activeContract = activeContractOpt.get();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setAcceptedContract(true);
        user.setAcceptedContractVersion(activeContract);

        userRepository.save(user);
        return "Inscription réussie et contrat accepté.";
    }
}
