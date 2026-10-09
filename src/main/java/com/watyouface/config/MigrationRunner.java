package com.watyouface.config;

import com.watyouface.entity.Contract;
import com.watyouface.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MigrationRunner.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public void run(String... args) {
        log.info("Checking JPA entity mappings");

        // Lister toutes les entités détectées par JPA
        entityManager.getEntityManagerFactory().getMetamodel().getEntities()
                .forEach(entity -> log.debug("JPA entity detected: {}", entity.getName()));

        // Vérifier si un contrat actif existe déjà
        if (contractRepository.findByActiveTrue().isEmpty()) {
            Contract contract = new Contract();
            contract.setTitle("Contrat général WatYouFace");
            contract.setContent("Le contenu complet du contrat général WatYouFace...");
            contract.setVersion("1.0");
            contract.setActive(true);

            contractRepository.save(contract);
            log.info("Initial active contract created");
        } else {
            log.info("An active contract already exists");
        }
    }
}
