package com.watyouface.controller;

import com.watyouface.entity.Contract;
import com.watyouface.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.InputStreamResource;
import com.watyouface.service.PdfService;


import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/active")
    public ResponseEntity<Contract> getActiveContract() {
        Contract contract = contractService.getActiveContract();
        if (contract == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contract);
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptContract(@RequestBody Map<String, Object> req) {
        Long userId = Long.valueOf(req.get("userId").toString());
        Long contractId = Long.valueOf(req.get("contractId").toString());
        boolean accepted = Boolean.parseBoolean(req.get("accepted").toString());

        String result = contractService.acceptContract(userId, contractId, accepted);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadContract(@PathVariable Long id) {
    Contract contract = contractService.getActiveContract();
    if (contract == null || !contract.getId().equals(id)) {
        return ResponseEntity.notFound().build();
    }

    ByteArrayInputStream bis = pdfService.generateContractPdf(contract);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=WatYouFace_Contract_v" + contract.getVersion() + ".pdf");

    return ResponseEntity
            .ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(new InputStreamResource(bis));
    }
}
