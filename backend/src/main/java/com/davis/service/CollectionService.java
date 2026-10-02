package com.davis.service;

import com.davis.dto.CollectionResultDto;
import com.davis.model.CaseEntity;
import com.davis.model.CryptoTransaction;
import com.davis.model.DiscoveredEntity;
import com.davis.model.Evidence;
import com.davis.model.Indicator;
import com.davis.model.Relationship;
import com.davis.model.TextSample;
import com.davis.repository.CaseRepository;
import com.davis.repository.CryptoTransactionRepository;
import com.davis.repository.DiscoveredEntityRepository;
import com.davis.repository.EvidenceRepository;
import com.davis.repository.IndicatorRepository;
import com.davis.repository.RelationshipRepository;
import com.davis.repository.TextSampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        List<Indicator> indicators = indicatorRepository.findByCaseIdOrderByCreatedAtDesc(caseId);
        List<String> logs = new ArrayList<>();

        logs.add("[OPSEC GATE] Initializing OPSEC-gated synthetic collection sandbox. Real dark-web crawling DISABLED.");
        logs.add("[COLLECTION SCOPE] Using controlled synthetic darknet, clearnet, keyserver, and blockchain intelligence feeds.");

        if (indicators.isEmpty()) {
            logs.add("[COLLECTION STOP] No indicators are attached to this case.");
            auditService.logAction(caseId, "CONTROLLED_COLLECTION_SKIPPED", "DAVIS Collection Service",
                    "Collection requested for case " + caseEntity.getCaseNumber() + " but no indicators were supplied.");

            return new CollectionResultDto(
                    "NO_INDICATORS",
                    "Add at least one indicator before starting controlled investigation.",
                    "SYNTHETIC CONTROLLED INVESTIGATION DATA",
                    0,
                    0,
                    0,
                    0,
                    logs
            );
        }

        for (Indicator indicator : indicators) {
            logs.add("[SIMULATED FEED] Ingesting indicator: "
                    + indicator.getType() + " = " + indicator.getValue());
        }

        /*
         * IMPORTANT:
         * The previous implementation only converted each supplied indicator
         * into one entity. That is why a new case containing NightFalcon could
         * produce a graph containing only NightFalcon.
         *
         * This implementation creates a complete CASE-SCOPED synthetic
         * intelligence package. Nothing is copied from case 1 and nothing is
         * attached to another case. Every generated record uses this caseId.
         */

        List<DiscoveredEntity> existingEntities = entityRepository.findByCaseId(caseId);
        Map<String, DiscoveredEntity> entitiesByKey = new LinkedHashMap<>();
        for (DiscoveredEntity entity : existingEntities) {
            entitiesByKey.put(entity.getNormalizedValue().toLowerCase(Locale.ROOT), entity);
        }

        int entitiesCreated = 0;
        int relationshipsCreated = 0;
        int evidenceCreated = 0;

        // 1. Persist every supplied indicator as an entity if it is not already present.
        for (Indicator indicator : indicators) {
            String entityType = mapIndicatorToEntityType(indicator.getType());
            DiscoveredEntity entity = getOrCreateEntity(
                    caseId,
                    entityType,
                    indicator.getValue(),
                    indicator.getConfidence(),
                    entitiesByKey
            );
            if (entity.getId() != null && !existingEntities.contains(entity)) {
                entitiesCreated++;
            }
        }

        // 2. Choose a stable anchor indicator. Prefer USERNAME/ALIAS because
        // those produce the clearest persona graph for the SIH demonstration.
        Indicator anchorIndicator = chooseAnchorIndicator(indicators);
        String anchorValue = anchorIndicator.getValue();
        String personaName = derivePersonaName(anchorIndicator);
        String slug = slugify(personaName);
        int seed = positiveHash(anchorValue);

        // 3. Build the synthetic intelligence package around the anchor.
        DiscoveredEntity persona = getOrCreateEntity(
                caseId,
                "PERSONA",
                personaName,
                confidence(anchorIndicator.getConfidence(), 0.95),
                entitiesByKey
        );

        String aliasName = deriveAliasName(personaName, seed);
        DiscoveredEntity alias = getOrCreateEntity(
                caseId,
                "ALIAS",
                aliasName,
                0.88,
                entitiesByKey
        );

        String emailValue = findIndicatorValue(indicators, "EMAIL");
        if (emailValue == null) {
            emailValue = slug + ".intel@example.invalid";
        }
        DiscoveredEntity email = getOrCreateEntity(
                caseId,
                "EMAIL",
                emailValue,
                0.88,
                entitiesByKey
        );

        String pgpValue = findIndicatorValue(indicators, "PGP_KEY");
        if (pgpValue == null) {
            pgpValue = syntheticPgp(seed);
        }
        DiscoveredEntity pgp = getOrCreateEntity(
                caseId,
                "PGP_KEY",
                pgpValue,
                0.92,
                entitiesByKey
        );

        String walletValue = findIndicatorValue(indicators, "CRYPTO_WALLET");
        if (walletValue == null) {
            walletValue = syntheticWallet(seed);
        }
        DiscoveredEntity wallet = getOrCreateEntity(
                caseId,
                "WALLET",
                walletValue,
                0.94,
                entitiesByKey
        );

        String domainValue = findIndicatorValue(indicators, "DOMAIN");
        if (domainValue == null) {
            domainValue = slug + "-ops.example.invalid";
        }
        DiscoveredEntity domain = getOrCreateEntity(
                caseId,
                "DOMAIN",
                domainValue,
                0.84,
                entitiesByKey
        );

        String onionValue = findIndicatorValue(indicators, "ONION_ADDRESS");
        if (onionValue == null) {
            onionValue = slug + "-secure-demo.onion";
        }
        DiscoveredEntity onion = getOrCreateEntity(
                caseId,
                "ONION_SERVICE",
                onionValue,
                0.90,
                entitiesByKey
        );

        DiscoveredEntity forum = getOrCreateEntity(
                caseId,
                "FORUM_ACCOUNT",
                "ShadowForum UID:" + (10000 + (seed % 89999)),
                0.86,
                entitiesByKey
        );

        DiscoveredEntity secondaryWallet = getOrCreateEntity(
                caseId,
                "WALLET",
                syntheticSecondaryWallet(seed),
                0.82,
                entitiesByKey
        );

        DiscoveredEntity conflictingAlias = getOrCreateEntity(
                caseId,
                "ALIAS",
                "CypherSentinel-" + (seed % 100),
                0.70,
                entitiesByKey
        );

        // Recalculate how many entities were added after the package was created.
        entitiesCreated = entityRepository.findByCaseId(caseId).size() - existingEntities.size();

        // 4. Create evidence only once for this case. Re-running collection
        // must not endlessly duplicate evidence, transactions or text samples.
        List<Evidence> currentEvidence = evidenceRepository.findByCaseId(caseId);
        Map<String, Evidence> evidenceByType = new LinkedHashMap<>();
        for (Evidence evidence : currentEvidence) {
            evidenceByType.putIfAbsent(evidence.getType(), evidence);
        }

        Evidence cryptoEvidence = getOrCreateEvidence(
                caseId,
                evidenceByType,
                "CRYPTO_FORENSIC",
                "Synthetic Blockchain Explorer",
                "Controlled synthetic transaction telemetry links the primary wallet to the persona investigation package.",
                0.95,
                "Financial attribution to target persona",
                null,
                joinIds(persona, wallet, secondaryWallet)
        );

        Evidence pgpEvidence = getOrCreateEvidence(
                caseId,
                evidenceByType,
                "PGP_KEY_SIGNATURE",
                "Synthetic Keyserver HKP Archive",
                "Controlled synthetic PGP self-signature links the investigation persona with the supplied identity indicators.",
                0.94,
                "Cryptographic link between persona and identity indicators",
                null,
                joinIds(persona, email, pgp)
        );

        Evidence infraEvidence = getOrCreateEvidence(
                caseId,
                evidenceByType,
                "INFRASTRUCTURE_TELEMETRY",
                "Synthetic Passive DNS / TLS Logs",
                "Controlled synthetic telemetry links the example clearnet domain and onion service through a shared infrastructure fingerprint.",
                0.88,
                "Network bridge between clearnet and darknet infrastructure",
                null,
                joinIds(domain, onion)
        );

        Evidence temporalEvidence = getOrCreateEvidence(
                caseId,
                evidenceByType,
                "TEMPORAL_ACTIVITY",
                "Synthetic Forum Telemetry",
                "Controlled synthetic activity bursts overlap between the persona and associated forum account during the same operating window.",
                0.85,
                "Behavioural operating-hours overlap",
                null,
                joinIds(persona, forum)
        );

        Evidence stylometricEvidence = getOrCreateEvidence(
                caseId,
                evidenceByType,
                "STYLOMETRIC_MATCH",
                "Synthetic Text Correlation Engine",
                "Controlled synthetic text samples share punctuation, vocabulary, and structural features suitable for the prototype stylometric comparison.",
                0.80,
                "Stylometric correlation between supplied indicator persona and associated forum sample",
                null,
                joinIds(persona, forum)
        );

        Evidence temporalConflict = getOrCreateEvidence(
                caseId,
                evidenceByType,
                "CONTRADICTORY_TEMPORAL",
                "Synthetic Clearnet Activity Logs",
                "A controlled synthetic activity window overlaps with a separate clearnet session, introducing a multi-operator or proxy-operator hypothesis.",
                0.87,
                null,
                "Contradicts a singular-operator identity assumption",
                joinIds(persona, alias)
        );

        Evidence cryptoConflict = getOrCreateEvidence(
                caseId,
                evidenceByType,
                "CONTRADICTORY_CRYPTOGRAPHIC",
                "Synthetic PGP Web-of-Trust Analysis",
                "A controlled synthetic secondary signer is associated with a conflicting alias, introducing a possible key-reuse or false-flag hypothesis.",
                0.76,
                null,
                "Contradicts exclusive control of the PGP keypair",
                joinIds(pgp, conflictingAlias)
        );

        Evidence aiRewriteEvidence = getOrCreateEvidence(
                caseId,
                evidenceByType,
                "AI_REWRITE_SIGNAL",
                "Synthetic Linguistic Scanner",
                "A controlled synthetic follow-up text sample contains formal paraphrasing patterns that reduce the reliability of stylometric comparison for that sample.",
                0.82,
                null,
                "Downgrades stylometric reliability of the synthetic rewritten sample",
                String.valueOf(persona.getId())
        );

        evidenceCreated = evidenceRepository.findByCaseId(caseId).size() - currentEvidence.size();

        // 5. Create graph relationships only when the same relationship does not already exist.
        relationshipsCreated += getOrCreateRelationship(caseId, persona, alias, "OWNS_ALIAS", 0.72, temporalConflict.getId());
        relationshipsCreated += getOrCreateRelationship(caseId, persona, email, "REGISTERED_WITH", 0.92, pgpEvidence.getId());
        relationshipsCreated += getOrCreateRelationship(caseId, persona, pgp, "USES_PGP", 0.94, pgpEvidence.getId());
        relationshipsCreated += getOrCreateRelationship(caseId, persona, wallet, "CONTROLS_WALLET", 0.95, cryptoEvidence.getId());
        relationshipsCreated += getOrCreateRelationship(caseId, persona, onion, "OPERATES_SERVICE", 0.89, infraEvidence.getId());
        relationshipsCreated += getOrCreateRelationship(caseId, onion, domain, "BRIDGED_TO_DOMAIN", 0.86, infraEvidence.getId());
        relationshipsCreated += getOrCreateRelationship(caseId, persona, forum, "POSTS_ON_FORUM", 0.91, temporalEvidence.getId());
        relationshipsCreated += getOrCreateRelationship(caseId, wallet, secondaryWallet, "TRANSFERS_TO", 0.88, cryptoEvidence.getId());
        relationshipsCreated += getOrCreateRelationship(caseId, pgp, conflictingAlias, "SIGNED_BY_CONFLICT", 0.70, cryptoConflict.getId());

        // 6. Add text samples so the analysis engine has real case-scoped data.
        createTextSamplesIfMissing(caseId, persona, forum, slug);

        // 7. Add synthetic blockchain transactions so the financial score is data-driven.
        createTransactionsIfMissing(caseId, wallet, secondaryWallet, seed);

        logs.add("[ENTITY RESOLUTION] Created case-scoped synthetic persona, aliases, cryptographic identifiers, wallets, infrastructure and forum entities.");
        logs.add("[RELATIONSHIP GRAPH] Created/verified " + relationshipRepository.findByCaseId(caseId).size() + " case-scoped relationships.");
        logs.add("[EVIDENCE] Created/verified " + evidenceRepository.findByCaseId(caseId).size() + " synthetic evidence records.");
        logs.add("[INTEGRITY] SHA-256 integrity hashes computed for generated evidence records.");
        logs.add("[PIPELINE COMPLETE] Controlled synthetic collection finished successfully for case " + caseEntity.getCaseNumber() + ".");

        auditService.logAction(
                caseId,
                "CONTROLLED_COLLECTION_EXECUTED",
                "DAVIS Collection Service",
                "Processed " + indicators.size()
                        + " indicators; case now contains "
                        + entityRepository.findByCaseId(caseId).size()
                        + " entities, "
                        + relationshipRepository.findByCaseId(caseId).size()
                        + " relationships and "
                        + evidenceRepository.findByCaseId(caseId).size()
                        + " evidence records."
        );

        return new CollectionResultDto(
                "SUCCESS",
                "Controlled collection completed using case-scoped synthetic intelligence feeds.",
                "SYNTHETIC CONTROLLED INVESTIGATION DATA",
                indicators.size(),
                entitiesCreated,
                relationshipsCreated,
                evidenceCreated,
                logs
        );
    }

    private DiscoveredEntity getOrCreateEntity(Long caseId,
                                                String type,
                                                String name,
                                                Double confidence,
                                                Map<String, DiscoveredEntity> entitiesByKey) {
        String normalized = entityResolutionService.normalizeValue(type, name);
        String key = normalized.toLowerCase(Locale.ROOT);

        DiscoveredEntity existing = entitiesByKey.get(key);
        if (existing != null) {
            return existing;
        }

        DiscoveredEntity created = new DiscoveredEntity(
                caseId,
                type,
                name,
                normalized,
                confidence != null ? confidence : 0.85
        );
        DiscoveredEntity saved = entityRepository.save(created);
        entitiesByKey.put(key, saved);
        return saved;
    }

    private Evidence getOrCreateEvidence(Long caseId,
                                         Map<String, Evidence> evidenceByType,
                                         String type,
                                         String source,
                                         String content,
                                         double reliability,
                                         String supports,
                                         String contradicts,
                                         String relatedEntities) {
        Evidence existing = evidenceByType.get(type);
        if (existing != null) {
            return existing;
        }

        LocalDateTime timestamp = LocalDateTime.now().minusHours(1 + evidenceByType.size());
        Evidence created = new Evidence(
                caseId,
                type,
                source,
                content,
                timestamp,
                integrityService.computeSha256(type + "|" + caseId + "|" + content),
                reliability,
                supports,
                contradicts,
                relatedEntities
        );

        Evidence saved = evidenceRepository.save(created);
        evidenceByType.put(type, saved);
        return saved;
    }

    private int getOrCreateRelationship(Long caseId,
                                        DiscoveredEntity source,
                                        DiscoveredEntity target,
                                        String relationshipType,
                                        double confidence,
                                        Long evidenceId) {
        List<Relationship> relationships = relationshipRepository.findByCaseId(caseId);
        boolean exists = relationships.stream().anyMatch(r ->
                source.getId().equals(r.getSourceEntityId())
                        && target.getId().equals(r.getTargetEntityId())
                        && relationshipType.equalsIgnoreCase(r.getRelationshipType())
        );

        if (exists) {
            return 0;
        }

        relationshipRepository.save(new Relationship(
                caseId,
                source.getId(),
                target.getId(),
                relationshipType,
                confidence,
                evidenceId
        ));
        return 1;
    }

    private void createTextSamplesIfMissing(Long caseId,
                                            DiscoveredEntity persona,
                                            DiscoveredEntity forum,
                                            String slug) {
        if (!textSampleRepository.findByCaseId(caseId).isEmpty()) {
            return;
        }

        textSampleRepository.save(new TextSample(
                caseId,
                persona.getId(),
                "Synthetic Attribution Note",
                "Payment instructions must be followed immediately -- no exceptions. The encrypted material will be released if the compliance window expires. Operational secrecy remains mandatory.",
                "en",
                LocalDateTime.now().minusDays(3),
                false
        ));

        textSampleRepository.save(new TextSample(
                caseId,
                forum.getId(),
                "Synthetic Forum Technical Post",
                "We strictly deploy custom payload packers -- avoiding default entropy metrics is straightforward. Operational secrecy remains important and the target infrastructure changed quickly after persistence was attained.",
                "en",
                LocalDateTime.now().minusDays(2),
                false
        ));

        textSampleRepository.save(new TextSample(
                caseId,
                persona.getId(),
                "Synthetic Rewritten Follow-up",
                "Kindly be advised that the designated cryptocurrency funds are strictly required to proceed. Should the compliance window lapse, comprehensive disclosure of internal records will inevitably follow.",
                "en",
                LocalDateTime.now().minusDays(1),
                true
        ));
    }

    private void createTransactionsIfMissing(Long caseId,
                                             DiscoveredEntity wallet,
                                             DiscoveredEntity secondaryWallet,
                                             int seed) {
        if (!transactionRepository.findByCaseId(caseId).isEmpty()) {
            return;
        }

        String walletValue = wallet.getName();
        String secondaryValue = secondaryWallet.getName();
        String txSeed = Integer.toHexString(seed);

        transactionRepository.save(new CryptoTransaction(
                caseId,
                "synthetic-source-" + txSeed,
                walletValue,
                1.45000000,
                "BTC",
                LocalDateTime.now().minusDays(5),
                transactionHash(caseId, 1, txSeed)
        ));

        transactionRepository.save(new CryptoTransaction(
                caseId,
                walletValue,
                secondaryValue,
                0.72500000,
                "BTC",
                LocalDateTime.now().minusDays(4),
                transactionHash(caseId, 2, txSeed)
        ));

        transactionRepository.save(new CryptoTransaction(
                caseId,
                walletValue,
                "synthetic-cold-storage-" + txSeed,
                0.71000000,
                "BTC",
                LocalDateTime.now().minusDays(4),
                transactionHash(caseId, 3, txSeed)
        ));
    }

    private Indicator chooseAnchorIndicator(List<Indicator> indicators) {
        for (Indicator indicator : indicators) {
            if ("USERNAME".equalsIgnoreCase(indicator.getType())
                    || "ALIAS".equalsIgnoreCase(indicator.getType())) {
                return indicator;
            }
        }
        return indicators.get(0);
    }

    private String derivePersonaName(Indicator indicator) {
        if (indicator.getValue() == null || indicator.getValue().trim().isEmpty()) {
            return "SyntheticPersona";
        }

        String value = indicator.getValue().trim();
        if ("EMAIL".equalsIgnoreCase(indicator.getType())) {
            int at = value.indexOf('@');
            if (at > 0) {
                return value.substring(0, at);
            }
        }

        if ("USERNAME".equalsIgnoreCase(indicator.getType())
                || "ALIAS".equalsIgnoreCase(indicator.getType())) {
            return value;
        }

        return "Persona-" + Integer.toUnsignedString(value.hashCode());
    }

    private String deriveAliasName(String personaName, int seed) {
        String[] parts = personaName.trim().split("\\s+");
        String prefix;

        if (parts.length >= 2) {
            prefix = (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase(Locale.ROOT);
        } else {
            String clean = personaName.replaceAll("[^A-Za-z0-9]", "");
            if (clean.length() >= 2) {
                prefix = clean.substring(0, 2).toUpperCase(Locale.ROOT);
            } else {
                prefix = "PX";
            }
        }

        return prefix + "_" + (10 + (seed % 90));
    }

    private String findIndicatorValue(List<Indicator> indicators, String type) {
        for (Indicator indicator : indicators) {
            if (type.equalsIgnoreCase(indicator.getType())) {
                return indicator.getValue();
            }
        }
        return null;
    }

    private String mapIndicatorToEntityType(String indicatorType) {
        if (indicatorType == null) {
            return "ALIAS";
        }

        switch (indicatorType.toUpperCase(Locale.ROOT)) {
            case "USERNAME":
                return "PERSONA";
            case "EMAIL":
                return "EMAIL";
            case "PGP_KEY":
                return "PGP_KEY";
            case "CRYPTO_WALLET":
                return "WALLET";
            case "DOMAIN":
                return "DOMAIN";
            case "ONION_ADDRESS":
                return "ONION_SERVICE";
            case "ALIAS":
                return "ALIAS";
            default:
                return "ALIAS";
        }
    }

    private String slugify(String value) {
        String slug = value == null ? "persona" : value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return slug.isEmpty() ? "persona" : slug;
    }

    private int positiveHash(String value) {
        return value == null ? 1 : (int) (Integer.toUnsignedLong(value.hashCode()) % 1_000_000L);
    }

    private double confidence(Double supplied, double fallback) {
        return supplied != null ? Math.max(0.0, Math.min(1.0, supplied)) : fallback;
    }

    private String syntheticPgp(int seed) {
        String hex = String.format(Locale.ROOT, "%08X", seed);
        return "SYNTH-PGP-7F9A-" + hex + "-2F1A";
    }

    private String syntheticWallet(int seed) {
        return "bc1qdavis" + Integer.toUnsignedString(seed, 36) + "syntheticwallet";
    }

    private String syntheticSecondaryWallet(int seed) {
        return "bc1qcashout" + Integer.toUnsignedString(seed + 7919, 36) + "synthetic";
    }

    private String transactionHash(Long caseId, int sequence, String seed) {
        return integrityService.computeSha256("TX|" + caseId + "|" + sequence + "|" + seed);
    }

    private String joinIds(DiscoveredEntity... entities) {
        StringBuilder result = new StringBuilder();
        for (DiscoveredEntity entity : entities) {
            if (entity == null || entity.getId() == null) {
                continue;
            }
            if (result.length() > 0) {
                result.append(',');
            }
            result.append(entity.getId());
        }
        return result.toString();
    }
}
