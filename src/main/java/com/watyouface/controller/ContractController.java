// src/main/java/com/watyouface/controller/ContractController.java

package com.watyouface.controller;

import com.watyouface.entity.Contract;
import com.watyouface.entity.User;
import com.watyouface.service.ContractService;
import com.watyouface.service.PdfService;
import com.watyouface.service.UserService;
import com.watyouface.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
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
    private JwtUtil jwtUtil;

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

    /** 🔹 Accepter ou refuser le contrat (public) */
    @PostMapping("/accept")
    public ResponseEntity<String> acceptContract(@RequestBody Map<String, Object> req) {
        try {
            Long userId = Long.valueOf(req.get("userId").toString());
            Long contractId = Long.valueOf(req.get("contractId").toString());
            boolean accepted = Boolean.parseBoolean(req.get("accepted").toString());

            String result = contractService.acceptContract(userId, contractId, accepted);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erreur lors de la validation du contrat : " + e.getMessage());
        }
    }

    /** 🔹 Télécharger le contrat actif en PDF (sécurisé) */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadContract(
            @PathVariable Long id,
            HttpServletRequest request) {

        // 🔐 1. Extraire le token depuis l'header Authorization
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = bearerToken.substring(7); // Supprime "Bearer "

        // 🔐 2. Valider le token
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }

        // 👤 3. Extraire le username du token
        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        // 👤 4. Trouver l'utilisateur par username
        Optional<User> userOpt = userService.findByUsername(username);
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