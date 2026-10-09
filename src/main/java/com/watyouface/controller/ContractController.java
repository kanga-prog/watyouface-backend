// src/main/java/com/watyouface/controller/ContractController.java

package com.watyouface.controller;

import com.watyouface.entity.Contract;
import com.watyouface.entity.User;
import com.watyouface.service.ContractService;
import com.watyouface.service.PdfService;
import com.watyouface.service.UserService;
import com.watyouface.security.Authz;
import com.watyouface.dto.ContractAcceptanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private UserService userService;

    @Autowired
    private Authz authz;

    /** 🔹 Voir le contrat actif (public) */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveContract() {
        Optional<Contract> activeOpt = contractService.getActiveContract();
        if (activeOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun contrat actif");
        }
        Contract active = activeOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", active.getId(),
                "title", active.getTitle(),
                "version", active.getVersion(),
                "content", active.getContent()
        ));
    }

    /** Accepter ou refuser le contrat de l'utilisateur authentifié. */
    @PostMapping("/accept")
    public ResponseEntity<String> acceptContract(@Valid @RequestBody ContractAcceptanceRequest req) {
        try {
            Long userId = authz.me();
            String result = contractService.acceptContractForUser(userId, req.getContractId(), req.isAccepted());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Erreur lors de la validation du contrat : " + e.getMessage());
        }
    }
    /** 🔹 Télécharger le contrat actif en PDF (sécurisé) */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadContract(@PathVariable Long id) {
        // Identity is derived from the authenticated cookie/bearer filter principal.
        Long userId = authz.me();
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        User user = userOpt.get();

        // 📄 5. Vérifier que le contrat demandé est bien l'actif
        Optional<Contract> contractOpt = contractService.getActiveContract();
        if (contractOpt.isEmpty() || !contractOpt.get().getId().equals(id)) {
            return ResponseEntity.notFound().build();
        }
        Contract contract = contractOpt.get();

        // 🖨️ 6. Générer le PDF
        ByteArrayInputStream bis = pdfService.generateContractPdf(contract, user);
        if (bis == null) {
            return ResponseEntity.status(500).build();
        }

        // 📥 7. Préparer la réponse
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                "attachment; filename=WatYouFace_Contract_v" + contract.getVersion() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
