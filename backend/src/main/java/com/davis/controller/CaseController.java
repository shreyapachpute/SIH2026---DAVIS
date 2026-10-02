package com.davis.controller;

import com.davis.config.DataInitializer;
import com.davis.dto.CaseDto;
import com.davis.dto.CollectionResultDto;
import com.davis.dto.GraphResponseDto;
import com.davis.dto.TimelineEventDto;
import com.davis.model.AuditLog;
import com.davis.model.CaseEntity;
import com.davis.service.AuditService;
import com.davis.service.CaseService;
import com.davis.service.CollectionService;
import com.davis.service.EntityResolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;
    private final CollectionService collectionService;
    private final EntityResolutionService entityResolutionService;
    private final AuditService auditService;
    private final DataInitializer dataInitializer;

    @Autowired
    public CaseController(CaseService caseService,
                          CollectionService collectionService,
                          EntityResolutionService entityResolutionService,
                          AuditService auditService,
                          DataInitializer dataInitializer) {
        this.caseService = caseService;
        this.collectionService = collectionService;
        this.entityResolutionService = entityResolutionService;
        this.auditService = auditService;
        this.dataInitializer = dataInitializer;
    }

    @GetMapping
    public ResponseEntity<List<CaseDto>> listCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    @PostMapping
    public ResponseEntity<CaseDto> createCase(@RequestBody CaseDto dto) {
        return ResponseEntity.ok(caseService.createCase(dto));
    }

    @PostMapping("/demo")
    public ResponseEntity<CaseEntity> loadDemoCase() {
        CaseEntity demo = dataInitializer.seedDemoCase();
        return ResponseEntity.ok(demo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseDto> getCase(@PathVariable Long id) {
        return caseService.getCaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseDto> updateCase(@PathVariable Long id, @RequestBody CaseDto dto) {
        return ResponseEntity.ok(caseService.updateCase(id, dto));
    }

    @GetMapping("/{id}/graph")
    public ResponseEntity<GraphResponseDto> getGraph(@PathVariable Long id) {
        return ResponseEntity.ok(entityResolutionService.getGraph(id));
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<TimelineEventDto>> getTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getTimeline(id));
    }

    @PostMapping("/{id}/collect")
    public ResponseEntity<CollectionResultDto> runCollection(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.runControlledCollection(id));
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<List<AuditLog>> getAuditLogs(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getAuditLogs(id));
    }
}
