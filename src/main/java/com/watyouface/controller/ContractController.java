package com.watyouface.controller;

import com.watyouface.entity.Contract;
import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.JwtUtil;
import com.watyouface.service.ContractService;
import com.watyouface.service.PdfService;
import com.watyouface.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private UserRepository userRepository;

    /** 🔹 Voir le contrat actif */
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

    /** 🔹 Signer le contrat */
    @PostMapping("/sign")
    public ResponseEntity<?> signContract(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token manquant ou invalide.");
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Utilisateur introuvable.");
        }

        try {
            contractService.signContractForUser(userOpt.get());
            return ResponseEntity.ok("Contrat signé et envoyé par e-mail ✅");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }

    /** 🔹 Accepter ou refuser un contrat */
    @PostMapping("/accept")
    public ResponseEntity<String> acceptContract(@RequestBody Map<String, Object> req) {
        Long userId = Long.valueOf(req.get("userId").toString());
        Long contractId = Long.valueOf(req.get("contractId").toString());
        boolean accepted = Boolean.parseBoolean(req.get("accepted").toString());

        String result = contractService.acceptContract(userId, contractId, accepted);
        return ResponseEntity.ok(result);
    }

    /** 🔹 Télécharger le contrat actif en PDF personnalisé */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadContract(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.extractUsername(authHeader.substring(7));
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        User user = userOpt.get();
        Optional<Contract> contractOpt = contractService.getActiveContract();
        if (contractOpt.isEmpty() || !contractOpt.get().getId().equals(id)) {
            return ResponseEntity.notFound().build();
        }
        Contract contract = contractOpt.get();

        ByteArrayInputStream bis = pdfService.generateContractPdf(contract, user);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition",
                "attachment; filename=WatYouFace_Contract_" + user.getUsername() + "_v" + contract.getVersion() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
