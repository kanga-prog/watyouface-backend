package com.watyouface.controller;

import com.watyouface.entity.Contract;
import com.watyouface.service.ContractService;
import com.watyouface.service.PdfService;
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

    /** 🔹 Télécharger le contrat actif en PDF (facultatif, toujours sécurisé si connecté) */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadContract(@PathVariable Long id) {
        Optional<Contract> contractOpt = contractService.getActiveContract();
        if (contractOpt.isEmpty() || !contractOpt.get().getId().equals(id)) {
            return ResponseEntity.notFound().build();
        }

        Contract contract = contractOpt.get();
        ByteArrayInputStream bis = pdfService.generateContractPdf(contract, null);

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
