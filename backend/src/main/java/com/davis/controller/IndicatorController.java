package com.davis.controller;

import com.davis.dto.IndicatorRequest;
import com.davis.model.Indicator;
import com.davis.repository.IndicatorRepository;
import com.davis.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/indicators")
public class IndicatorController {

    private final IndicatorRepository indicatorRepository;
    private final AuditService auditService;

    @Autowired
    public IndicatorController(
            IndicatorRepository indicatorRepository,
            AuditService auditService) {

        this.indicatorRepository = indicatorRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<List<Indicator>> getIndicators(
            @PathVariable Long caseId) {

        return ResponseEntity.ok(
                indicatorRepository.findByCaseIdOrderByCreatedAtDesc(caseId)
        );
    }

    @PostMapping
    public ResponseEntity<Indicator> addIndicator(
            @PathVariable Long caseId,
            @Valid @RequestBody IndicatorRequest req) {

        Indicator ind = new Indicator(
                caseId,
                req.getType(),
                req.getValue(),
                req.getSource(),
                req.getConfidence()
        );

        Indicator saved = indicatorRepository.save(ind);

        auditService.logAction(
                caseId,
                "INDICATOR_ADDED",
                "Analyst",
                "Added indicator: "
                        + req.getType()
                        + " = "
                        + req.getValue()
                        + " (Source: "
                        + req.getSource()
                        + ")"
        );

        return ResponseEntity.ok(saved);
    }
}