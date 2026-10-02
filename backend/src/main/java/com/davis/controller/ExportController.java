package com.davis.controller;

import com.davis.dto.InvestigationPackageDto;
import com.davis.service.ExportService;
import com.davis.service.IntegrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/cases/{caseId}")
public class ExportController {

    private final ExportService exportService;
    private final IntegrityService integrityService;

    @Autowired
    public ExportController(ExportService exportService, IntegrityService integrityService) {
        this.exportService = exportService;
        this.integrityService = integrityService;
    }

    @PostMapping("/report")
    public ResponseEntity<InvestigationPackageDto> generateReportSummary(@PathVariable Long caseId) {
        return ResponseEntity.ok(exportService.buildInvestigationPackage(caseId));
    }

    @GetMapping(value = "/report/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable Long caseId) {
        byte[] pdfBytes = exportService.exportPdfReport(caseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "DAVIS-Report-Case-" + caseId + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/certificate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long caseId) {
        byte[] pdfBytes = exportService.exportIntegrityCertificate(caseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "DAVIS-Evidence-Certificate-Case-" + caseId + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(@PathVariable Long caseId) {
        String csv = exportService.exportCsv(caseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "DAVIS-Case-" + caseId + "-Data.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping(value = "/export/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> exportJson(@PathVariable Long caseId) {
        String json = exportService.exportJson(caseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "DAVIS-Investigation-Package-Case-" + caseId + ".json");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

    @PostMapping("/verify-integrity")
    public ResponseEntity<Map<String, Object>> verifyIntegrity(@PathVariable Long caseId,
                                                               @RequestBody Map<String, String> payload) {
        String data = payload.get("data");
        String expectedHash = payload.get("hash");
        return ResponseEntity.ok(integrityService.verifyIntegrity(data, expectedHash));
    }
}
