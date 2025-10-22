package com.watyouface.config;

import com.watyouface.entity.Contract;
import com.watyouface.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class MigrationRunner implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ContractRepository contractRepository;

    @Override
    public void run(String... args) {
        System.out.println("✅ Vérification de la migration des entités dans PostgreSQL...");

        // Lister toutes les entités détectées par JPA
        entityManager.getEntityManagerFactory().getMetamodel().getEntities()
                .forEach(entity -> System.out.println("📦 Entité détectée : " + entity.getName()));

        // Vérifier si un contrat actif existe déjà
        if (contractRepository.findByActiveTrue().isEmpty()) {
            Contract contract = new Contract();
            contract.setTitle("Contrat général WatYouFace");
            contract.setContent("Le contenu complet du contrat général WatYouFace...");
            contract.setVersion("1.0");
            contract.setActive(true);

            contractRepository.save(contract);
            System.out.println("✅ Contrat initial créé et actif !");
        } else {
            System.out.println("ℹ️ Un contrat actif existe déjà, aucune création nécessaire.");
        }
    }
}
