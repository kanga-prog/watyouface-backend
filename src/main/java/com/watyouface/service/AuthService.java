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
        if (userOpt.isEmpty()) return "Email ou mot de passe invalide.";

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Email ou mot de passe invalide.";
        }

        ensureActiveContractAccepted(user);

        String role = (user.getRole() != null ? user.getRole().name() : "USER");
        return jwtUtil.generateToken(user.getId(), user.getUsername(), role);
    }

    /** The authenticated account must have accepted the currently active contract. */
    public void ensureActiveContractAccepted(User user) {
        Contract activeContract = contractService.getActiveContract()
                .orElseThrow(() -> new IllegalStateException("Aucun contrat actif n’est disponible."));

        if (!user.isAcceptedContract()
                || user.getAcceptedContractVersion() == null
                || !activeContract.getId().equals(user.getAcceptedContractVersion().getId())) {
            throw new SecurityException("Veuillez accepter le contrat WatYouFace en vigueur pour vous connecter.");
        }
    }


    // ========================
    // Enregistrement : toujours sauvegarder l'utilisateur
    // ========================
    public User register(String username, String email, String password, boolean acceptedContract) {
        // Vérifications
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Cette adresse e-mail est déjà utilisée.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Ce nom d'utilisateur est déjà pris.");
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
                throw new IllegalStateException("Aucun contrat actif n’est disponible.");
            }
            user.setAcceptedContractVersion(activeContractOpt.get());
        }
        // Sinon : acceptedContractVersion reste null → OK

        return userRepository.save(user); // 👈 Toujours sauvegardé
    }
}
