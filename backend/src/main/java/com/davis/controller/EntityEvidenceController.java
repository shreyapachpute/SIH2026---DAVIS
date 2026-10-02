package com.davis.controller;

import com.davis.model.DiscoveredEntity;
import com.davis.model.Evidence;
import com.davis.model.Relationship;
import com.davis.repository.DiscoveredEntityRepository;
import com.davis.repository.EvidenceRepository;
import com.davis.repository.RelationshipRepository;
import com.davis.service.AuditService;
import com.davis.service.IntegrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}")
public class EntityEvidenceController {

    private final DiscoveredEntityRepository entityRepository;
    private final RelationshipRepository relationshipRepository;
    private final EvidenceRepository evidenceRepository;
    private final IntegrityService integrityService;
    private final AuditService auditService;

    @Autowired
    public EntityEvidenceController(DiscoveredEntityRepository entityRepository,
                                    RelationshipRepository relationshipRepository,
                                    EvidenceRepository evidenceRepository,
                                    IntegrityService integrityService,
                                    AuditService auditService) {
        this.entityRepository = entityRepository;
        this.relationshipRepository = relationshipRepository;
        this.evidenceRepository = evidenceRepository;
        this.integrityService = integrityService;
        this.auditService = auditService;
    }

    @GetMapping("/entities")
    public ResponseEntity<List<DiscoveredEntity>> getEntities(@PathVariable Long caseId) {
        return ResponseEntity.ok(entityRepository.findByCaseId(caseId));
    }

    @GetMapping("/relationships")
    public ResponseEntity<List<Relationship>> getRelationships(@PathVariable Long caseId) {
        return ResponseEntity.ok(relationshipRepository.findByCaseId(caseId));
    }

    @GetMapping("/evidence")
    public ResponseEntity<List<Evidence>> getEvidence(@PathVariable Long caseId) {
        return ResponseEntity.ok(evidenceRepository.findByCaseIdOrderByTimestampAsc(caseId));
    }

    @PostMapping("/evidence")
    public ResponseEntity<Evidence> addEvidence(@PathVariable Long caseId, @RequestBody Evidence ev) {
        ev.setCaseId(caseId);
        if (ev.getTimestamp() == null) ev.setTimestamp(LocalDateTime.now());
        if (ev.getHash() == null || ev.getHash().isEmpty()) {
            ev.setHash(integrityService.computeSha256(ev.getType() + ev.getContent() + LocalDateTime.now()));
        }
        Evidence saved = evidenceRepository.save(ev);
        auditService.logAction(caseId, "EVIDENCE_ADDED", "Analyst", "Added evidence: " + ev.getType() + " (Hash: " + ev.getHash().substring(0, 16) + "...)");
        return ResponseEntity.ok(saved);
    }
}
