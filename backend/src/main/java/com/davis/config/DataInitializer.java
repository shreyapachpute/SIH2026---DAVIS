package com.davis.config;

import com.davis.analysis.StylometryEngine;
import com.davis.model.*;
import com.davis.repository.*;
import com.davis.service.IntegrityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CaseRepository caseRepository;
    private final IndicatorRepository indicatorRepository;
    private final DiscoveredEntityRepository entityRepository;
    private final RelationshipRepository relationshipRepository;
    private final EvidenceRepository evidenceRepository;
    private final TextSampleRepository textSampleRepository;
    private final CryptoTransactionRepository transactionRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final StressTestRepository stressTestRepository;
    private final ReviewRepository reviewRepository;
    private final AuditLogRepository auditLogRepository;
    private final IntegrityService integrityService;

    @Autowired
    public DataInitializer(CaseRepository caseRepository,
                           IndicatorRepository indicatorRepository,
                           DiscoveredEntityRepository entityRepository,
                           RelationshipRepository relationshipRepository,
                           EvidenceRepository evidenceRepository,
                           TextSampleRepository textSampleRepository,
                           CryptoTransactionRepository transactionRepository,
                           AnalysisResultRepository analysisResultRepository,
                           StressTestRepository stressTestRepository,
                           ReviewRepository reviewRepository,
                           AuditLogRepository auditLogRepository,
                           IntegrityService integrityService) {
        this.caseRepository = caseRepository;
        this.indicatorRepository = indicatorRepository;
        this.entityRepository = entityRepository;
        this.relationshipRepository = relationshipRepository;
        this.evidenceRepository = evidenceRepository;
        this.textSampleRepository = textSampleRepository;
        this.transactionRepository = transactionRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.stressTestRepository = stressTestRepository;
        this.reviewRepository = reviewRepository;
        this.auditLogRepository = auditLogRepository;
        this.integrityService = integrityService;
    }

    @Override
    public void run(String... args) {
        if (caseRepository.count() == 0) {
            seedDemoCase();
        }
    }

    @Transactional
    public CaseEntity seedDemoCase() {
        // If demo case exists, delete its related data or return it
        CaseEntity existing = caseRepository.findByCaseNumber("DAVIS-DEMO-001").orElse(null);
        if (existing != null) {
            return existing;
        }

        // 1. Create Case
        CaseEntity demoCase = new CaseEntity(
                "DAVIS-DEMO-001",
                "Operation Falcon Trace: Dark Web Extortion & Attribution Lead",
                "Investigation into synthetic dark-web ransomware broker operating under pseudonym NightFalcon across decentralized forums and Tor onion services.",
                "ACTIVE"
        );
        demoCase = caseRepository.save(demoCase);
        Long caseId = demoCase.getId();

        // 2. Indicators
        Indicator ind1 = new Indicator(caseId, "USERNAME", "NightFalcon", "Darknet Market Extortion Post #8419", 0.95);
        Indicator ind2 = new Indicator(caseId, "EMAIL", "nightfalcon.demo@example.invalid", "Encrypted paste leak metadata", 0.88);
        Indicator ind3 = new Indicator(caseId, "PGP_KEY", "7F9A 4B2C 8E1D 0F3A 5C6B 9D2E 1A4F 3B8C 7E0D 2F1A", "Keyserver HKP query (synthetic)", 0.92);
        Indicator ind4 = new Indicator(caseId, "CRYPTO_WALLET", "bc1q9davisdemo7falcon9synthetic3trans001", "Ransom negotiation note leak", 0.96);
        Indicator ind5 = new Indicator(caseId, "DOMAIN", "falcon-ops.example.invalid", "WHOIS historical certificate telemetry", 0.82);
        Indicator ind6 = new Indicator(caseId, "ONION_ADDRESS", "falconsec7synthx3darkdemo.onion", "Tor directory index crawl", 0.90);
        indicatorRepository.save(ind1);
        indicatorRepository.save(ind2);
        indicatorRepository.save(ind3);
        indicatorRepository.save(ind4);
        indicatorRepository.save(ind5);
        indicatorRepository.save(ind6);

        // 3. Discovered Entities
        DiscoveredEntity e1 = entityRepository.save(new DiscoveredEntity(caseId, "PERSONA", "NightFalcon Primary", "persona:nightfalcon", 0.95));
        DiscoveredEntity e2 = entityRepository.save(new DiscoveredEntity(caseId, "ALIAS", "NF_27 (Clearnet)", "alias:nf_27", 0.82));
        DiscoveredEntity e3 = entityRepository.save(new DiscoveredEntity(caseId, "EMAIL", "nightfalcon.demo@example.invalid", "email:nightfalcon.demo@example.invalid", 0.90));
        DiscoveredEntity e4 = entityRepository.save(new DiscoveredEntity(caseId, "PGP_KEY", "PGP Fingerprint 7F9A...2F1A", "pgp:7f9a4b2c8e1d0f3a5c6b9d2e1a4f3b8c7e0d2f1a", 0.94));
        DiscoveredEntity e5 = entityRepository.save(new DiscoveredEntity(caseId, "WALLET", "BTC Vault (bc1q9davis...001)", "wallet:bc1q9davisdemo7falcon9synthetic3trans001", 0.96));
        DiscoveredEntity e6 = entityRepository.save(new DiscoveredEntity(caseId, "DOMAIN", "falcon-ops.example.invalid", "domain:falcon-ops.example.invalid", 0.84));
        DiscoveredEntity e7 = entityRepository.save(new DiscoveredEntity(caseId, "ONION_SERVICE", "falconsec...onion", "onion:falconsec7synthx3darkdemo.onion", 0.91));
        DiscoveredEntity e8 = entityRepository.save(new DiscoveredEntity(caseId, "FORUM_ACCOUNT", "ShadowForum UID: 10482", "forum:shadowforum_10482", 0.89));
        DiscoveredEntity e9 = entityRepository.save(new DiscoveredEntity(caseId, "ALIAS", "CypherSentinel (Conflicting)", "alias:cyphersentinel", 0.65));
        DiscoveredEntity e10 = entityRepository.save(new DiscoveredEntity(caseId, "WALLET", "Secondary Cashout bc1q9interm2", "wallet:bc1q9interm2cashout999synth", 0.88));

        // 4. Evidence (Supporting & Contradictory)
        Evidence ev1 = evidenceRepository.save(new Evidence(caseId, "CRYPTO_FORENSIC", "Synthetic Blockchain Explorer", 
                "Direct inbound transfer of 1.45 BTC from ransom deposit wallet to bc1q9davisdemo7falcon9synthetic3trans001 matching extortion demand.", 
                LocalDateTime.now().minusDays(5), integrityService.computeSha256("EVIDENCE_DEMO_01_CRYPTO"), 0.95, 
                "Financial attribution to NightFalcon", null, e1.getId() + "," + e5.getId()));

        Evidence ev2 = evidenceRepository.save(new Evidence(caseId, "PGP_KEY_SIGNATURE", "Keyserver HKP Archive", 
                "PGP public key 7F9A...2F1A associated with user-ID nightfalcon.demo@example.invalid and self-signed timestamp 2026-02-14.", 
                LocalDateTime.now().minusDays(9), integrityService.computeSha256("EVIDENCE_DEMO_02_PGP"), 0.94, 
                "Cryptographic link between persona and email", null, e1.getId() + "," + e3.getId() + "," + e4.getId()));

        Evidence ev3 = evidenceRepository.save(new Evidence(caseId, "INFRASTRUCTURE_TELEMETRY", "Synthetic Passive DNS / TLS Logs", 
                "TLS certificate CN falcon-ops.example.invalid matched onion service ephemeral host config via shared SSL serial 0x4A1F890C.", 
                LocalDateTime.now().minusDays(6), integrityService.computeSha256("EVIDENCE_DEMO_03_INFRA"), 0.88, 
                "Network bridge between clearnet and darknet infrastructure", null, e6.getId() + "," + e7.getId()));

        Evidence ev4 = evidenceRepository.save(new Evidence(caseId, "TEMPORAL_ACTIVITY", "ShadowForum & GitHub Telemetry", 
                "Burst activity between UTC 02:00-06:00 consistently observed across darknet forum posts and repository commits.", 
                LocalDateTime.now().minusDays(4), integrityService.computeSha256("EVIDENCE_DEMO_04_TEMP"), 0.85, 
                "Behavioural operating-hours overlap", null, e1.getId() + "," + e8.getId()));

        Evidence ev5 = evidenceRepository.save(new Evidence(caseId, "STYLOMETRIC_MATCH", "Extortion Notes vs Forum Posts", 
                "TF-IDF cosine similarity 78.4% with idiosyncratic double-hyphen punctuation and specialized jargon.", 
                LocalDateTime.now().minusDays(3), integrityService.computeSha256("EVIDENCE_DEMO_05_STYLO"), 0.80, 
                "Stylometric correlation between extortion text and author sample", null, e1.getId() + "," + e8.getId()));

        Evidence ev6 = evidenceRepository.save(new Evidence(caseId, "CONTRADICTORY_TEMPORAL", "Clearnet Forum SecNet Logs", 
                "Active clearnet forum posts from alias NF_27 logged at UTC 14:30 while darknet persona was concurrently in live peer chat.", 
                LocalDateTime.now().minusDays(3), integrityService.computeSha256("EVIDENCE_DEMO_06_CONTRA_TIME"), 0.87, 
                null, "Contradicts singular identity assumption between NightFalcon and NF_27 (Dual Operator Hypothesis)", e1.getId() + "," + e2.getId()));

        Evidence ev7 = evidenceRepository.save(new Evidence(caseId, "CONTRADICTORY_CRYPTOGRAPHIC", "PGP Web-of-Trust Analysis", 
                "Subkey 0x3A1C revokes primary encryption key, signed with key ID belonging to third-party researcher CypherSentinel.", 
                LocalDateTime.now().minusDays(2), integrityService.computeSha256("EVIDENCE_DEMO_07_CONTRA_PGP"), 0.76, 
                null, "Contradicts exclusive control of PGP keypair (potential false-flag or key reuse)", e4.getId() + "," + e9.getId()));

        Evidence ev8 = evidenceRepository.save(new Evidence(caseId, "AI_REWRITE_SIGNAL", "Automated Linguistic Scanner", 
                "Extortion communication sample C displays flattened vocabulary distribution and uniform syntactic depth indicative of LLM paraphrasing.", 
                LocalDateTime.now().minusDays(1), integrityService.computeSha256("EVIDENCE_DEMO_08_AI_REWRITE"), 0.82, 
                null, "Downgrades stylometric reliability of Sample C due to synthetic AI rewriting", String.valueOf(e1.getId())));

        // 5. Relationships
        relationshipRepository.save(new Relationship(caseId, e1.getId(), e2.getId(), "OWNS_ALIAS", 0.72, ev6.getId()));
        relationshipRepository.save(new Relationship(caseId, e1.getId(), e3.getId(), "REGISTERED_WITH", 0.92, ev2.getId()));
        relationshipRepository.save(new Relationship(caseId, e1.getId(), e4.getId(), "USES_PGP", 0.94, ev2.getId()));
        relationshipRepository.save(new Relationship(caseId, e1.getId(), e5.getId(), "CONTROLS_WALLET", 0.95, ev1.getId()));
        relationshipRepository.save(new Relationship(caseId, e1.getId(), e7.getId(), "OPERATES_SERVICE", 0.89, ev3.getId()));
        relationshipRepository.save(new Relationship(caseId, e7.getId(), e6.getId(), "BRIDGED_TO_DOMAIN", 0.86, ev3.getId()));
        relationshipRepository.save(new Relationship(caseId, e1.getId(), e8.getId(), "POSTS_ON_FORUM", 0.91, ev4.getId()));
        relationshipRepository.save(new Relationship(caseId, e5.getId(), e10.getId(), "TRANSFERS_TO", 0.88, ev1.getId()));
        relationshipRepository.save(new Relationship(caseId, e4.getId(), e9.getId(), "SIGNED_BY_CONFLICT", 0.70, ev7.getId()));

        // 6. Text Samples
        textSampleRepository.save(new TextSample(caseId, e1.getId(), "Extortion Demand Note (Darknet)",
                "Payment of 1.45 BTC must be remitted immediately -- non-negotiable terms. Data decryption mirrors will be shredded irrevocably upon block 894000. Protocol enforces zero exceptions; delay results in public repository exposure.",
                "en", LocalDateTime.now().minusDays(10), false));

        textSampleRepository.save(new TextSample(caseId, e8.getId(), "ShadowForum Technical Discussion",
                "We strictly deploy custom payload packers -- avoiding default entropy metrics is trivial. The target infrastructure crumbled within twenty minutes once persistence was attained; no compromise on operational secrecy.",
                "en", LocalDateTime.now().minusDays(8), false));

        textSampleRepository.save(new TextSample(caseId, e1.getId(), "Paraphrased Extortion Follow-up",
                "Kindly be advised that the designated cryptocurrency funds are strictly required to proceed with the restoration of the encrypted databases. Should the compliance window lapse, comprehensive disclosure of internal proprietary records will inevitably follow.",
                "en", LocalDateTime.now().minusDays(3), true));

        // 7. Transactions
        transactionRepository.save(new CryptoTransaction(caseId, "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa_demo",
                "bc1q9davisdemo7falcon9synthetic3trans001", 1.45000000, "BTC", LocalDateTime.now().minusDays(5),
                "tx7f8a9b1c2d3e4f5061728394a5b6c7d8e9f0123456789abcdef0123456789a"));
        transactionRepository.save(new CryptoTransaction(caseId, "bc1q9davisdemo7falcon9synthetic3trans001",
                "bc1q9interm2cashout999synth", 0.72500000, "BTC", LocalDateTime.now().minusDays(4),
                "tx8b9c0d1e2f3a4b5c6d7e8f90123456789abcdef0123456789abcdef012345b"));
        transactionRepository.save(new CryptoTransaction(caseId, "bc1q9davisdemo7falcon9synthetic3trans001",
                "bc1qcoldstoragefalconholding99synth", 0.71000000, "BTC", LocalDateTime.now().minusDays(4),
                "tx9c0d1e2f3a4b5c6d7e8f9a0123456789abcdef0123456789abcdef012345c"));

        // 8. Analysis Result Baseline
        analysisResultRepository.save(new AnalysisResult(caseId, 74.0, 0.88,
                "DAVIS Multi-Vector Attribution Model v2.6 (Weighted Evidence - Contradiction Penalties)",
                "{\"aliasContribution\":12.5,\"cryptographicContribution\":23.5,\"financialContribution\":19.2,\"infrastructureContribution\":13.8,\"stylometricContribution\":11.0,\"temporalContribution\":8.0,\"contradictionDeduction\":-14.0,\"totalScore\":74.0}"));

        // 9. Stress Tests Baseline
        stressTestRepository.save(new StressTestRecord(caseId, "PGP_KEY", 74.0, 50.5, -23.5, "MODERATE_ROBUSTNESS",
                "Removing PGP cryptographic correlation drops attribution support to 50.5/100. Financial and infrastructure vectors maintain secondary lead."));
        stressTestRepository.save(new StressTestRecord(caseId, "CRYPTO_WALLET", 74.0, 54.8, -19.2, "MODERATE_ROBUSTNESS",
                "Removing blockchain transaction correlation retains cryptographic and infrastructure vectors with score 54.8/100."));

        // 10. Review
        reviewRepository.save(new ReviewRecord(caseId, "Analyst Sharma (SIH-CyberSec-Lead)", "NEEDS_FURTHER_INVESTIGATION",
                "Strong cryptographic and financial correlation observed. However, temporal contradiction between NF_27 and NightFalcon live sessions suggests secondary operative or proxy operator. Recommend pivoting to subkey issuer CypherSentinel before concluding attribution lead."));

        // 11. Audit Logs
        auditLogRepository.save(new AuditLog(caseId, "CASE_CREATED", "System", LocalDateTime.now().minusDays(11),
                "Synthetic case DAVIS-DEMO-001 initialized for demonstration.", integrityService.computeSha256("LOG_1")));
        auditLogRepository.save(new AuditLog(caseId, "INDICATOR_ADDED", "Analyst Sharma", LocalDateTime.now().minusDays(11),
                "Added seed username NightFalcon from extortion post.", integrityService.computeSha256("LOG_2")));
        auditLogRepository.save(new AuditLog(caseId, "COLLECTION_SIMULATED", "System Agent", LocalDateTime.now().minusDays(5),
                "Executed OPSEC-gated synthetic collection across darknet and clearnet sources.", integrityService.computeSha256("LOG_3")));
        auditLogRepository.save(new AuditLog(caseId, "ANALYSIS_EXECUTED", "DAVIS Core Engine", LocalDateTime.now().minusDays(1),
                "Computed Attribution Support Score: 74.0/100 with active contradiction deduction.", integrityService.computeSha256("LOG_4")));
        auditLogRepository.save(new AuditLog(caseId, "STRESS_TEST_RUN", "Analyst Sharma", LocalDateTime.now().minusHours(12),
                "Dependency stress test: PGP removed. Delta: -23.5.", integrityService.computeSha256("LOG_5")));
        auditLogRepository.save(new AuditLog(caseId, "ANALYST_REVIEW_SAVED", "Analyst Sharma", LocalDateTime.now().minusHours(5),
                "Review saved with decision NEEDS_FURTHER_INVESTIGATION.", integrityService.computeSha256("LOG_6")));

        return demoCase;
    }
}
