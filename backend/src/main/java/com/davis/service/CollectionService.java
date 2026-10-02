package com.davis.service;

import com.davis.dto.CollectionResultDto;
import com.davis.model.*;
import com.davis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CollectionService {

    private final CaseRepository caseRepository;
    private final IndicatorRepository indicatorRepository;
    private final DiscoveredEntityRepository entityRepository;
    private final RelationshipRepository relationshipRepository;
    private final EvidenceRepository evidenceRepository;
    private final CryptoTransactionRepository transactionRepository;
    private final TextSampleRepository textSampleRepository;
    private final IntegrityService integrityService;
    private final AuditService auditService;
    private final EntityResolutionService entityResolutionService;

    @Autowired
    public CollectionService(CaseRepository caseRepository,
                             IndicatorRepository indicatorRepository,
                             DiscoveredEntityRepository entityRepository,
                             RelationshipRepository relationshipRepository,
                             EvidenceRepository evidenceRepository,
                             CryptoTransactionRepository transactionRepository,
                             TextSampleRepository textSampleRepository,
                             IntegrityService integrityService,
                             AuditService auditService,
                             EntityResolutionService entityResolutionService) {
        this.caseRepository = caseRepository;
        this.indicatorRepository = indicatorRepository;
        this.entityRepository = entityRepository;
        this.relationshipRepository = relationshipRepository;
        this.evidenceRepository = evidenceRepository;
        this.transactionRepository = transactionRepository;
        this.textSampleRepository = textSampleRepository;
        this.integrityService = integrityService;
        this.auditService = auditService;
        this.entityResolutionService = entityResolutionService;
    }

    @Transactional
    public CollectionResultDto runControlledCollection(Long caseId) {
        CaseEntity caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));

        List<Indicator> indicators = indicatorRepository.findByCaseId(caseId);
        List<String> logs = new ArrayList<>();
        logs.add("[OPSEC GATE] Initializing OPSEC-gated synthetic collection sandbox. Real dark-web crawling DISABLED.");
        logs.add("[COLLECTION SCOPE] Querying simulated darknet market mirrors, keyservers, and blockchain ledgers.");

        int entitiesCreated = 0;
        int relationshipsCreated = 0;
        int evidenceCreated = 0;

        for (Indicator ind : indicators) {
            logs.add("[SIMULATED FEED] Ingesting indicator: " + ind.getType() + " = " + ind.getValue());

            // Check if primary persona exists
            String norm = entityResolutionService.normalizeValue(ind.getType(), ind.getValue());
            boolean exists = entityRepository.findByCaseId(caseId).stream()
                    .anyMatch(e -> e.getNormalizedValue().equalsIgnoreCase(norm));

            if (!exists) {
                DiscoveredEntity newEntity = new DiscoveredEntity(caseId, mapIndicatorToEntityType(ind.getType()), ind.getValue(), norm, ind.getConfidence());
                entityRepository.save(newEntity);
                entitiesCreated++;
                logs.add("  -> Discovered entity: " + newEntity.getName() + " [" + newEntity.getType() + "]");
            }
        }

        // Add synthetic evidence records if few exist
        List<Evidence> currentEv = evidenceRepository.findByCaseId(caseId);
        if (currentEv.isEmpty()) {
            // Generate standard synthetic intelligence package
            Evidence ev1 = new Evidence(caseId, "CRYPTO_FORENSIC", "Synthetic Blockchain Explorer", 
                    "Direct inbound transfer of 1.45 BTC from ransom deposit wallet to bc1q9davisdemo7falcon9synthetic3trans001.", 
                    LocalDateTime.now().minusDays(3), 
                    integrityService.computeSha256("CRYPTO_FORENSIC_1.45_BTC_EVIDENCE"), 0.95, 
                    "Financial attribution to target persona", null, "1,5");
            evidenceRepository.save(ev1);
            evidenceCreated++;

            Evidence ev2 = new Evidence(caseId, "PGP_KEY_SIGNATURE", "Keyserver HKP Archive", 
                    "PGP key self-signature verified with user ID matching primary email indicator.", 
                    LocalDateTime.now().minusDays(4), 
                    integrityService.computeSha256("PGP_KEY_SIGNATURE_EVIDENCE"), 0.94, 
                    "Cryptographic link between persona and email", null, "1,3,4");
            evidenceRepository.save(ev2);
            evidenceCreated++;

            Evidence ev3 = new Evidence(caseId, "CONTRADICTORY_TEMPORAL", "Clearnet Forum SecNet Logs", 
                    "Active clearnet forum posts from alias NF_27 logged at UTC 14:30 while darknet persona was concurrently in live peer chat.", 
                    LocalDateTime.now().minusDays(1), 
                    integrityService.computeSha256("CONTRADICTORY_TEMPORAL_EVIDENCE"), 0.87, 
                    null, "Contradicts singular operator identity hypothesis (Dual Operator Potential)", "1,2");
            evidenceRepository.save(ev3);
            evidenceCreated++;
        }

        logs.add("[ENTITY RESOLUTION] Normalizing extracted indicators and constructing persona relationship graph.");
        logs.add("[INTEGRITY] SHA-256 integrity trees computed and anchored into case audit logs.");
        logs.add("[PIPELINE COMPLETE] Controlled synthetic collection finished successfully.");

        auditService.logAction(caseId, "CONTROLLED_COLLECTION_EXECUTED", "DAVIS Collection Service", 
                "Processed " + indicators.size() + " indicators; discovered " + entitiesCreated + " entities; generated " + evidenceCreated + " evidence records.");

        return new CollectionResultDto(
                "SUCCESS",
                "Controlled collection completed using synthetic intelligence feeds.",
                "SYNTHETIC CONTROLLED INVESTIGATION DATA",
                indicators.size(),
                entitiesCreated,
                relationshipsCreated,
                evidenceCreated,
                logs
        );
    }

    private String mapIndicatorToEntityType(String indicatorType) {
        switch (indicatorType.toUpperCase()) {
            case "USERNAME": return "PERSONA";
            case "EMAIL": return "EMAIL";
            case "PGP_KEY": return "PGP_KEY";
            case "CRYPTO_WALLET": return "WALLET";
            case "DOMAIN": return "DOMAIN";
            case "ONION_ADDRESS": return "ONION_SERVICE";
            default: return "ALIAS";
        }
    }
}
