package com.watyouface.service;

import com.watyouface.entity.Contract;
import com.watyouface.entity.User;
import com.watyouface.entity.UserContract;
import com.watyouface.repository.ContractRepository;
import com.watyouface.repository.UserContractRepository;
import com.watyouface.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public String acceptContract(Long userId, Long contractId, boolean accepted) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Contract> contractOpt = contractRepository.findById(contractId);

        if (userOpt.isEmpty() || contractOpt.isEmpty()) {
            return "Utilisateur ou contrat introuvable.";
        }

        User user = userOpt.get();
        Contract contract = contractOpt.get();

        UserContract userContract = new UserContract(user, contract, accepted);
        userContractRepository.save(userContract);

        return accepted ? "Contrat accepté avec succès." : "Contrat refusé.";
    }

    /** 🔹 Signature du contrat par l’utilisateur */
    public void signContractForUser(User user) throws Exception {
        Optional<Contract> activeOpt = getActiveContract();
        if (activeOpt.isEmpty()) {
            throw new Exception("Aucun contrat actif.");
        }
        Contract contract = activeOpt.get();

        // Vérifie si déjà signé
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
