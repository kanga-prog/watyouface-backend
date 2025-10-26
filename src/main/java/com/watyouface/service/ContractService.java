package com.watyouface.service;

import com.watyouface.entity.Contract;
import com.watyouface.entity.User;
import com.watyouface.entity.UserContract;
import com.watyouface.repository.ContractRepository;
import com.watyouface.repository.UserContractRepository;
import com.watyouface.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserContractRepository userContractRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private MailService mailService;

    /** 🔹 Récupère le contrat actif */
    public Optional<Contract> getActiveContract() {
        return contractRepository.findByActiveTrue();
    }

    /** 🔹 Accepter ou refuser un contrat */
    @Transactional
    public String acceptContract(Long userId, Long contractId, boolean accepted) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Contract> contractOpt = contractRepository.findById(contractId);

        if (userOpt.isEmpty() || contractOpt.isEmpty()) {
            return "Utilisateur ou contrat introuvable.";
        }

        User user = userOpt.get();
        Contract contract = contractOpt.get();

        if (accepted) {
            // Mise à jour de l'utilisateur
            user.setAcceptedContract(true);
            user.setAcceptedContractVersion(contract);
            userRepository.save(user);

            // Historique dans UserContract
            UserContract userContract = new UserContract(user, contract, true);
            userContractRepository.save(userContract);

            return "Contrat accepté avec succès ✅";
        } else {
            user.setAcceptedContract(false);
            user.setAcceptedContractVersion(null);
            userRepository.save(user);

            UserContract userContract = new UserContract(user, contract, false);
            userContractRepository.save(userContract);

            return "Contrat refusé ❌";
        }
    }

    /** 🔹 Signature du contrat par l’utilisateur */
    @Transactional
    public void signContractForUser(User user) throws Exception {
        Optional<Contract> activeOpt = getActiveContract();
        if (activeOpt.isEmpty()) {
            throw new Exception("Aucun contrat actif.");
        }
        Contract contract = activeOpt.get();

        boolean alreadySigned = userContractRepository.existsByUserAndContract(user, contract);
        if (alreadySigned) {
            throw new Exception("Le contrat est déjà signé par cet utilisateur.");
        }

        // Génération du PDF
        byte[] pdfBytes = pdfService.generateContractPdf(contract, user).readAllBytes();

        // Envoi du PDF par e-mail
        mailService.sendContractEmail(user, pdfBytes);

        // Enregistrement de la signature
        UserContract userContract = new UserContract();
        userContract.setUser(user);
        userContract.setContract(contract);
        userContractRepository.save(userContract);
    }
}
