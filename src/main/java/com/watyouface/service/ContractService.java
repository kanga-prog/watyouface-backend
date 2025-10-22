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

    /**
     * Récupère le contrat actif (celui dont active = true)
     */
    public Contract getActiveContract() {
        return contractRepository.findByActiveTrue().orElse(null);
    }

    /**
     * Permet à un utilisateur d'accepter ou refuser un contrat
     */
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
}
