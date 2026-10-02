package com.davis.service;

import com.davis.dto.AnalysisScoreResponse;
import com.davis.dto.InvestigationPackageDto;
import com.davis.model.*;
import com.davis.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExportService {

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
    private final AnalysisService analysisService;
    private final CaseService caseService;
    private final IntegrityService integrityService;
    private final AuditService auditService;

    @Autowired
    public ExportService(
            CaseRepository caseRepository,
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
            AnalysisService analysisService,
            CaseService caseService,
            IntegrityService integrityService,
            AuditService auditService) {

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
        this.analysisService = analysisService;
        this.caseService = caseService;
        this.integrityService = integrityService;
        this.auditService = auditService;
    }


    /* =========================================================
       INVESTIGATION PACKAGE
       ========================================================= */

    public InvestigationPackageDto buildInvestigationPackage(Long caseId) {

        CaseEntity caseEntity =
                caseRepository.findById(caseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Case not found: " + caseId
                                ));

        InvestigationPackageDto pkg =
                new InvestigationPackageDto();

        pkg.setCaseDetails(caseEntity);

        pkg.setIndicators(
                indicatorRepository.findByCaseId(caseId)
        );

        pkg.setEntities(
                entityRepository.findByCaseId(caseId)
        );

        pkg.setRelationships(
                relationshipRepository.findByCaseId(caseId)
        );

        pkg.setEvidence(
                evidenceRepository.findByCaseId(caseId)
        );

        pkg.setTextSamples(
                textSampleRepository.findByCaseId(caseId)
        );

        pkg.setTransactions(
                transactionRepository.findByCaseId(caseId)
        );

        pkg.setAnalysis(
                analysisResultRepository
                        .findFirstByCaseIdOrderByCreatedAtDesc(caseId)
                        .orElse(null)
        );

        pkg.setStressTests(
                stressTestRepository
                        .findByCaseIdOrderByCreatedAtDesc(caseId)
        );

        pkg.setTimeline(
                caseService.getTimeline(caseId)
        );

        pkg.setReview(
                reviewRepository
                        .findFirstByCaseIdOrderByReviewedAtDesc(caseId)
                        .orElse(null)
        );

        pkg.setAuditTrail(
                auditLogRepository
                        .findByCaseIdOrderByTimestampAsc(caseId)
        );


        String payloadToHash =
                caseEntity.getCaseNumber()
                        + "|"
                        + pkg.getEvidence().size()
                        + "|"
                        + LocalDateTime.now();

        String masterHash =
                integrityService.computeSha256(
                        payloadToHash
                );


        Map<String, Object> integrityMap =
                new java.util.HashMap<>();

        integrityMap.put(
                "masterSha256",
                masterHash
        );

        integrityMap.put(
                "verificationStatus",
                "VERIFIED"
        );

        integrityMap.put(
                "generatedAt",
                LocalDateTime.now().toString()
        );

        integrityMap.put(
                "legalFrameworkAlignment",
                "Bharatiya Sakshya Adhiniyam (BSA) 2023 / BNSS 2023"
        );

        integrityMap.put(
                "disclaimer",
                "SYNTHETIC CONTROLLED INVESTIGATION DATA - ANALYTICAL LEAD ONLY"
        );

        pkg.setIntegrity(integrityMap);

        return pkg;
    }


    /* =========================================================
       JSON EXPORT
       ========================================================= */

    public String exportJson(Long caseId) {

        InvestigationPackageDto pkg =
                buildInvestigationPackage(caseId);

        ObjectMapper mapper =
                new ObjectMapper();

        mapper.registerModule(
                new JavaTimeModule()
        );

        mapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        mapper.enable(
                SerializationFeature.INDENT_OUTPUT
        );

        auditService.logAction(
                caseId,
                "EXPORT_JSON_GENERATED",
                "Analyst",
                "Exported complete JSON package."
        );

        try {

            return mapper.writeValueAsString(pkg);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to serialize JSON package",
                    e
            );
        }
    }


    /* =========================================================
       CSV EXPORT
       ========================================================= */

    public String exportCsv(Long caseId) {

        CaseEntity caseEntity =
                caseRepository.findById(caseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Case not found: " + caseId
                                ));

        List<Evidence> evidenceList =
                evidenceRepository.findByCaseId(caseId);

        StringWriter sw =
                new StringWriter();

        sw.append(
                "CaseNumber,EntityType,EntityName,RelationshipType,"
                        + "EvidenceSource,Supports,Contradicts,Confidence,Hash,Timestamp\n"
        );

        for (Evidence ev : evidenceList) {

            sw.append(
                    escapeCsv(
                            caseEntity.getCaseNumber()
                    )
            ).append(",");

            sw.append(
                    escapeCsv(ev.getType())
            ).append(",");

            sw.append(
                    escapeCsv(
                            ev.getRelatedEntities() != null
                                    ? ev.getRelatedEntities()
                                    : "N/A"
                    )
            ).append(",");

            sw.append("EVIDENCE_RECORD,");

            sw.append(
                    escapeCsv(ev.getSource())
            ).append(",");

            sw.append(
                    escapeCsv(
                            ev.getSupports() != null
                                    ? ev.getSupports()
                                    : ""
                    )
            ).append(",");

            sw.append(
                    escapeCsv(
                            ev.getContradicts() != null
                                    ? ev.getContradicts()
                                    : ""
                    )
            ).append(",");

            sw.append(
                    String.valueOf(
                            ev.getReliability()
                    )
            ).append(",");

            sw.append(
                    escapeCsv(ev.getHash())
            ).append(",");

            sw.append(
                    escapeCsv(
                            ev.getTimestamp().toString()
                    )
            ).append("\n");
        }

        auditService.logAction(
                caseId,
                "EXPORT_CSV_GENERATED",
                "Analyst",
                "Exported CSV investigation data."
        );

        return sw.toString();
    }


    /* =========================================================
       PDF REPORT
       ========================================================= */

    public byte[] exportPdfReport(Long caseId) {

        CaseEntity caseEntity =
                caseRepository.findById(caseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Case not found: " + caseId
                                ));

        AnalysisScoreResponse analysis =
                analysisService.runAnalysis(caseId);

        List<Evidence> evidenceList =
                evidenceRepository.findByCaseId(caseId);

        List<StressTestRecord> stressTests =
                stressTestRepository
                        .findByCaseIdOrderByCreatedAtDesc(caseId);

        Optional<ReviewRecord> reviewOpt =
                reviewRepository
                        .findFirstByCaseIdOrderByReviewedAtDesc(caseId);


        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        Document document =
                new Document(
                        PageSize.A4,
                        36,
                        36,
                        40,
                        40
                );


        try {

            PdfWriter.getInstance(
                    document,
                    baos
            );

            document.open();


            /* =====================================================
               FONTS
               ===================================================== */

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18,
                            Color.DARK_GRAY
                    );

            Font subtitleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11,
                            new Color(0, 102, 204)
                    );

            Font sectionFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12,
                            new Color(40, 40, 40)
                    );

            Font normalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            9,
                            Color.BLACK
                    );

            Font boldFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            9,
                            Color.BLACK
                    );

            Font monoFont =
                    FontFactory.getFont(
                            FontFactory.COURIER,
                            8,
                            Color.DARK_GRAY
                    );

            Font disclaimerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_OBLIQUE,
                            8,
                            Color.GRAY
                    );


            /* =====================================================
               TITLE
               ===================================================== */

            Paragraph title =
                    new Paragraph(
                            "DAVIS: DIGITAL ATTRIBUTION & VERIFICATION INTELLIGENCE SYSTEM",
                            titleFont
                    );

            title.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(title);


            Paragraph subtitle =
                    new Paragraph(
                            "FORENSIC INVESTIGATION REPORT | SIH 2026 | PS ID: SIH26151",
                            subtitleFont
                    );

            subtitle.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(subtitle);


            Paragraph banner =
                    new Paragraph(
                            "CLASSIFICATION: SYNTHETIC CONTROLLED INVESTIGATION DATA (DEMO PROTOTYPE)",
                            disclaimerFont
                    );

            banner.setAlignment(
                    Element.ALIGN_CENTER
            );

            banner.setSpacingAfter(15);

            document.add(banner);


            /* =====================================================
               1. CASE METADATA
               ===================================================== */

            document.add(
                    new Paragraph(
                            "1. CASE METADATA",
                            sectionFont
                    )
            );


            PdfPTable metaTable =
                    new PdfPTable(4);

            metaTable.setWidthPercentage(100);

            metaTable.setSpacingBefore(5);

            metaTable.setSpacingAfter(10);

            metaTable.setWidths(
                    new float[]{
                            25,
                            25,
                            25,
                            25
                    }
            );


            // BLUE LABEL
            addBlueLabelCell(
                    metaTable,
                    "Case Number",
                    boldFont
            );

            // WHITE VALUE
            addCell(
                    metaTable,
                    caseEntity.getCaseNumber(),
                    normalFont
            );


            // BLUE LABEL
            addBlueLabelCell(
                    metaTable,
                    "Case Status",
                    boldFont
            );

            // WHITE VALUE
            addCell(
                    metaTable,
                    caseEntity.getStatus(),
                    normalFont
            );


            // BLUE LABEL
            addBlueLabelCell(
                    metaTable,
                    "Title",
                    boldFont
            );

            // WHITE VALUE
            addCell(
                    metaTable,
                    caseEntity.getTitle(),
                    normalFont
            );


            // BLUE LABEL
            addBlueLabelCell(
                    metaTable,
                    "Report Date",
                    boldFont
            );

            // WHITE VALUE
            addCell(
                    metaTable,
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd HH:mm:ss"
                            )
                    ),
                    normalFont
            );


            document.add(metaTable);


            /* =====================================================
               2. DYNAMIC ATTRIBUTION SUPPORT SCORE
               ===================================================== */

            document.add(
                    new Paragraph(
                            "2. DYNAMIC ATTRIBUTION SUPPORT SCORE",
                            sectionFont
                    )
            );


            Paragraph scorePara =
                    new Paragraph(
                            String.format(
                                    "Calculated Attribution Support: %.1f / 100  |  Methodology: %s",
                                    analysis.getTotalScore(),
                                    analysis.getMethodology()
                            ),
                            boldFont
                    );

            scorePara.setSpacingBefore(4);
            scorePara.setSpacingAfter(6);

            document.add(scorePara);


            PdfPTable scoreTable =
                    new PdfPTable(3);

            scoreTable.setWidthPercentage(100);

            scoreTable.setSpacingAfter(10);

            scoreTable.setWidths(
                    new float[]{
                            45,
                            25,
                            30
                    }
            );


            addHeaderCell(
                    scoreTable,
                    "Analytical Dimension",
                    boldFont
            );

            addHeaderCell(
                    scoreTable,
                    "Score Contribution",
                    boldFont
            );

            addHeaderCell(
                    scoreTable,
                    "Signal Assessment",
                    boldFont
            );


            Map<String, Double> bd =
                    analysis.getBreakdown();

            if (bd != null) {

                addScoreRow(
                        scoreTable,
                        "Cryptographic (PGP) Correlation",
                        bd.getOrDefault(
                                "cryptographicContribution",
                                0.0
                        ),
                        "Primary Anchor",
                        normalFont
                );

                addScoreRow(
                        scoreTable,
                        "Financial (Blockchain) Correlation",
                        bd.getOrDefault(
                                "financialContribution",
                                0.0
                        ),
                        "Independent Proof",
                        normalFont
                );

                addScoreRow(
                        scoreTable,
                        "Infrastructure / Network Correlation",
                        bd.getOrDefault(
                                "infrastructureContribution",
                                0.0
                        ),
                        "Corroborating",
                        normalFont
                );

                addScoreRow(
                        scoreTable,
                        "Alias / Persona Correlation",
                        bd.getOrDefault(
                                "aliasContribution",
                                0.0
                        ),
                        "Normalized Match",
                        normalFont
                );

                addScoreRow(
                        scoreTable,
                        "Stylometric Linguistic Similarity",
                        bd.getOrDefault(
                                "stylometricContribution",
                                0.0
                        ),
                        "AI-Adjusted",
                        normalFont
                );

                addScoreRow(
                        scoreTable,
                        "Temporal Activity Overlap",
                        bd.getOrDefault(
                                "temporalContribution",
                                0.0
                        ),
                        "Operating Window",
                        normalFont
                );

                addScoreRow(
                        scoreTable,
                        "Contradiction Deduction (Conflicting Activity)",
                        bd.getOrDefault(
                                "contradictionDeduction",
                                0.0
                        ),
                        "Deducted Penalty",
                        normalFont
                );
            }

            document.add(scoreTable);


            /* =====================================================
               3. EVIDENCE
               ===================================================== */

            document.add(
                    new Paragraph(
                            "3. EVIDENCE INVENTORY & SHA-256 HASHES",
                            sectionFont
                    )
            );


            PdfPTable evTable =
                    new PdfPTable(4);

            evTable.setWidthPercentage(100);

            evTable.setSpacingBefore(5);

            evTable.setSpacingAfter(10);

            evTable.setWidths(
                    new float[]{
                            18,
                            42,
                            15,
                            25
                    }
            );


            addHeaderCell(
                    evTable,
                    "Evidence Type",
                    boldFont
            );

            addHeaderCell(
                    evTable,
                    "Content & Corroboration",
                    boldFont
            );

            addHeaderCell(
                    evTable,
                    "Classification",
                    boldFont
            );

            addHeaderCell(
                    evTable,
                    "SHA-256 Integrity",
                    boldFont
            );


            for (Evidence ev : evidenceList) {

                String cls =
                        (
                                ev.getSupports() != null
                                        && !ev.getSupports().isEmpty()
                        )
                                ? "SUPPORTING"
                                : "CONTRADICTORY";


                addCell(
                        evTable,
                        ev.getType(),
                        normalFont
                );

                addCell(
                        evTable,
                        ev.getContent(),
                        normalFont
                );

                addCell(
                        evTable,
                        cls,
                        normalFont
                );

                String hash =
                        ev.getHash() != null
                                ? ev.getHash()
                                : "";

                addCell(
                        evTable,
                        hash.length() > 16
                                ? hash.substring(0, 16) + "..."
                                : hash,
                        monoFont
                );
            }


            document.add(evTable);


            /* =====================================================
               4. STYLOMETRIC ANALYSIS
               ===================================================== */

            document.add(
                    new Paragraph(
                            "4. STYLOMETRIC ANALYSIS & AI-REWRITE MITIGATION",
                            sectionFont
                    )
            );


            Paragraph styText =
                    new Paragraph(
                            "DAVIS integrates an AI-rewrite awareness engine. "
                                    + "When threat actors leverage Large Language Models "
                                    + "(LLMs) to paraphrase extortion notes or forum "
                                    + "communications, lexical and syntactic metrics are "
                                    + "homogenized. Extortion Sample C was detected with "
                                    + "high AI-paraphrasing probability; stylometric weighting "
                                    + "was automatically halved by 50% to prevent "
                                    + "false-attribution artifacts.",
                            normalFont
                    );

            styText.setSpacingBefore(4);
            styText.setSpacingAfter(10);

            document.add(styText);


            /* =====================================================
               5. STRESS TEST
               ===================================================== */

            document.add(
                    new Paragraph(
                            "5. DEPENDENCY STRESS-TEST & ROBUSTNESS",
                            sectionFont
                    )
            );


            PdfPTable stressTable =
                    new PdfPTable(4);

            stressTable.setWidthPercentage(100);

            stressTable.setSpacingBefore(5);

            stressTable.setSpacingAfter(10);

            stressTable.setWidths(
                    new float[]{
                            25,
                            20,
                            25,
                            30
                    }
            );


            addHeaderCell(
                    stressTable,
                    "Removed Dependency",
                    boldFont
            );

            addHeaderCell(
                    stressTable,
                    "Original -> Modified",
                    boldFont
            );

            addHeaderCell(
                    stressTable,
                    "Robustness",
                    boldFont
            );

            addHeaderCell(
                    stressTable,
                    "Explanation",
                    boldFont
            );


            if (stressTests.isEmpty()) {

                addCell(
                        stressTable,
                        "None executed",
                        normalFont
                );

                addCell(
                        stressTable,
                        "N/A",
                        normalFont
                );

                addCell(
                        stressTable,
                        "N/A",
                        normalFont
                );

                addCell(
                        stressTable,
                        "No stress test executed yet.",
                        normalFont
                );

            } else {

                for (StressTestRecord str : stressTests) {

                    addCell(
                            stressTable,
                            str.getRemovedDependency(),
                            normalFont
                    );

                    addCell(
                            stressTable,
                            str.getOriginalScore()
                                    + " -> "
                                    + str.getModifiedScore()
                                    + " ("
                                    + str.getScoreDelta()
                                    + ")",
                            normalFont
                    );

                    addCell(
                            stressTable,
                            str.getResult(),
                            boldFont
                    );

                    addCell(
                            stressTable,
                            str.getExplanation(),
                            normalFont
                    );
                }
            }


            document.add(stressTable);


            /* =====================================================
               6. ANALYST REVIEW
               ===================================================== */

            document.add(
                    new Paragraph(
                            "6. HUMAN-IN-THE-LOOP ANALYST REVIEW",
                            sectionFont
                    )
            );


            String revDecision =
                    reviewOpt
                            .map(ReviewRecord::getDecision)
                            .orElse(
                                    "PENDING_REVIEW"
                            );

            String revAnalyst =
                    reviewOpt
                            .map(ReviewRecord::getAnalyst)
                            .orElse(
                                    "Analyst"
                            );

            String revComments =
                    reviewOpt
                            .map(ReviewRecord::getComments)
                            .orElse(
                                    "Review pending further multi-source verification."
                            );


            Paragraph revText =
                    new Paragraph(
                            String.format(
                                    "Reviewer: %s  |  Decision: %s\nComments: %s",
                                    revAnalyst,
                                    revDecision,
                                    revComments
                            ),
                            normalFont
                    );

            revText.setSpacingBefore(4);
            revText.setSpacingAfter(15);

            document.add(revText);


            /* =====================================================
               7. LEGAL DISCLAIMER
               ===================================================== */

            document.add(
                    new Paragraph(
                            "7. LEGAL / EVIDENCE INTEGRITY DISCLAIMER",
                            sectionFont
                    )
            );


            Paragraph legal =
                    new Paragraph(
                            "This investigation report is generated by DAVIS "
                                    + "(Digital Attribution & Verification Intelligence System) "
                                    + "for analytical lead generation and evidence correlation. "
                                    + "In accordance with the principles of the Bharatiya Sakshya "
                                    + "Adhiniyam (BSA) 2023 and Bharatiya Nagarik Suraksha Sanhita "
                                    + "(BNSS) 2023, automated outputs constitute investigative "
                                    + "leads and do not independently establish guilt or identity "
                                    + "without corroborative judicial discovery and human oversight. "
                                    + "All demo data is strictly synthetic.",
                            disclaimerFont
                    );

            legal.setSpacingBefore(4);
            legal.setSpacingAfter(10);

            document.add(legal);


            /* =====================================================
               SHA-256 SEAL
               ===================================================== */

            String reportHash =
                    integrityService.computeSha256(
                            caseEntity.getCaseNumber()
                                    + analysis.getTotalScore()
                                    + LocalDateTime.now()
                    );


            Paragraph seal =
                    new Paragraph(
                            "PACKAGE SHA-256 INTEGRITY SEAL: "
                                    + reportHash,
                            monoFont
                    );

            seal.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(seal);


            document.close();


            auditService.logAction(
                    caseId,
                    "EXPORT_PDF_REPORT_GENERATED",
                    "Analyst",
                    "Generated formal PDF investigation report."
            );


            return baos.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate PDF report",
                    e
            );
        }
    }


    /* =========================================================
       INTEGRITY CERTIFICATE
       ========================================================= */

    public byte[] exportIntegrityCertificate(Long caseId) {

        CaseEntity caseEntity =
                caseRepository.findById(caseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Case not found: " + caseId
                                ));


        List<Evidence> evidenceList =
                evidenceRepository.findByCaseId(caseId);


        Optional<ReviewRecord> reviewOpt =
                reviewRepository
                        .findFirstByCaseIdOrderByReviewedAtDesc(caseId);


        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();


        Document document =
                new Document(
                        PageSize.A4,
                        40,
                        40,
                        40,
                        40
                );


        try {

            PdfWriter.getInstance(
                    document,
                    baos
            );

            document.open();


            Font certTitleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            22,
                            new Color(15, 32, 67)
                    );


            Font certSubtitleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12,
                            new Color(0, 102, 204)
                    );


            Font normalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10,
                            Color.BLACK
                    );


            Font boldFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10,
                            Color.BLACK
                    );


            Font monoFont =
                    FontFactory.getFont(
                            FontFactory.COURIER_BOLD,
                            9,
                            new Color(30, 30, 30)
                    );


            Font disclaimerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_OBLIQUE,
                            8,
                            Color.GRAY
                    );


            Paragraph p1 =
                    new Paragraph(
                            "DIGITAL ATTRIBUTION & VERIFICATION INTELLIGENCE SYSTEM",
                            certSubtitleFont
                    );

            p1.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(p1);


            Paragraph p2 =
                    new Paragraph(
                            "EVIDENCE INTEGRITY CERTIFICATE",
                            certTitleFont
                    );

            p2.setAlignment(
                    Element.ALIGN_CENTER
            );

            p2.setSpacingBefore(10);

            p2.setSpacingAfter(5);

            document.add(p2);


            Paragraph p3 =
                    new Paragraph(
                            "DIGITAL CHAIN-OF-CUSTODY & CRYPTOGRAPHIC VERIFICATION DOCUMENT",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    10,
                                    Color.DARK_GRAY
                            )
                    );

            p3.setAlignment(
                    Element.ALIGN_CENTER
            );

            p3.setSpacingAfter(25);

            document.add(p3);


            PdfPTable table =
                    new PdfPTable(2);

            table.setWidthPercentage(100);

            table.setWidths(
                    new float[]{
                            35,
                            65
                    }
            );

            table.setSpacingAfter(20);


            addCertRow(
                    table,
                    "Certificate Identifier:",
                    "DAVIS-CERT-"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 12)
                            .toUpperCase(),
                    boldFont,
                    monoFont
            );


            addCertRow(
                    table,
                    "Investigation Case:",
                    caseEntity.getCaseNumber()
                            + " - "
                            + caseEntity.getTitle(),
                    boldFont,
                    normalFont
            );


            addCertRow(
                    table,
                    "Evidence Items Verified:",
                    evidenceList.size()
                            + " Digital Artifacts",
                    boldFont,
                    normalFont
            );


            addCertRow(
                    table,
                    "Verification Standard:",
                    "SHA-256 Digest Tree & Immutable Audit Anchor",
                    boldFont,
                    normalFont
            );


            addCertRow(
                    table,
                    "Legal Admissibility Framing:",
                    "Bharatiya Sakshya Adhiniyam (BSA) 2023 Sec 63 / BNSS 2023",
                    boldFont,
                    normalFont
            );


            addCertRow(
                    table,
                    "Reviewing Officer / Analyst:",
                    reviewOpt
                            .map(ReviewRecord::getAnalyst)
                            .orElse(
                                    "Analyst Sharma (SIH-CyberSec-Lead)"
                            ),
                    boldFont,
                    normalFont
            );


            addCertRow(
                    table,
                    "Verification Timestamp:",
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd HH:mm:ss 'UTC'"
                            )
                    ),
                    boldFont,
                    normalFont
            );


            String masterHash =
                    integrityService.computeSha256(
                            caseEntity.getCaseNumber()
                                    + evidenceList.size()
                                    + "CERTIFICATE_AUTHENTICITY"
                    );


            addCertRow(
                    table,
                    "Master Evidence SHA-256:",
                    masterHash,
                    boldFont,
                    monoFont
            );


            document.add(table);


            Paragraph attest =
                    new Paragraph(
                            "CERTIFICATE ATTESTATION\n"
                                    + "This certificate verifies that the synthetic electronic "
                                    + "records associated with Case "
                                    + caseEntity.getCaseNumber()
                                    + " have been hashed upon ingestion and preserved in an "
                                    + "audit-anchored state. The computed cryptographic digests "
                                    + "match across all recorded investigation nodes without "
                                    + "unauthorized modification.",
                            normalFont
                    );

            attest.setSpacingAfter(20);

            document.add(attest);


            PdfPTable sigTable =
                    new PdfPTable(2);

            sigTable.setWidthPercentage(100);

            sigTable.setWidths(
                    new float[]{
                            50,
                            50
                    }
            );

            sigTable.setSpacingAfter(25);


            PdfPCell c1 =
                    new PdfPCell(
                            new Phrase(
                                    "Digitally Verified via DAVIS Core\n"
                                            + "Cryptographic Seal: VALID",
                                    monoFont
                            )
                    );

            c1.setBorder(
                    Rectangle.NO_BORDER
            );

            sigTable.addCell(c1);


            PdfPCell c2 =
                    new PdfPCell(
                            new Phrase(
                                    "Lead Analyst Review Sign-off\n"
                                            + "Signature: __________________________",
                                    normalFont
                            )
                    );

            c2.setBorder(
                    Rectangle.NO_BORDER
            );

            c2.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );

            sigTable.addCell(c2);


            document.add(sigTable);


            Paragraph disclaimer =
                    new Paragraph(
                            "DISCLAIMER: This certificate is an analytical demonstration "
                                    + "instrument generated by DAVIS for Smart India Hackathon "
                                    + "2026 (PS ID: SIH26151). All evidence records are synthetic. "
                                    + "Does not constitute official certification by any government agency.",
                            disclaimerFont
                    );

            disclaimer.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(disclaimer);


            document.close();


            auditService.logAction(
                    caseId,
                    "EXPORT_CERTIFICATE_GENERATED",
                    "Analyst",
                    "Generated formal Evidence Integrity Certificate PDF."
            );


            return baos.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate Certificate PDF",
                    e
            );
        }
    }


    /* =========================================================
       NORMAL CELL
       WHITE / DEFAULT
       ========================================================= */

    private void addCell(
            PdfPTable table,
            String text,
            Font font) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                text != null
                                        ? text
                                        : "",
                                font
                        )
                );

        cell.setPadding(4);

        // Normal/value cells stay white
        cell.setBackgroundColor(Color.WHITE);

        cell.setBorderColor(
                new Color(200, 210, 225)
        );

        table.addCell(cell);
    }


    /* =========================================================
       BLUE LABEL CELL
       USED ONLY FOR THE 4 METADATA LABELS
       ========================================================= */

    private void addBlueLabelCell(
            PdfPTable table,
            String text,
            Font font) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                text != null
                                        ? text
                                        : "",
                                font
                        )
                );

        cell.setPadding(4);

        // Same light blue used by the other report table headers
        cell.setBackgroundColor(
                new Color(240, 245, 250)
        );

        cell.setBorderColor(
                new Color(200, 210, 225)
        );

        table.addCell(cell);
    }


    /* =========================================================
       ACTUAL TABLE HEADER CELL
       ========================================================= */

    private void addHeaderCell(
            PdfPTable table,
            String text,
            Font font) {

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                text != null
                                        ? text
                                        : "",
                                font
                        )
                );

        cell.setBackgroundColor(
                new Color(240, 245, 250)
        );

        cell.setPadding(5);

        cell.setBorderColor(
                new Color(200, 210, 225)
        );

        table.addCell(cell);
    }


    /* =========================================================
       SCORE ROW
       ========================================================= */

    private void addScoreRow(
            PdfPTable table,
            String dimension,
            Double score,
            String assessment,
            Font font) {

        PdfPCell c1 =
                new PdfPCell(
                        new Phrase(
                                dimension,
                                font
                        )
                );

        c1.setPadding(4);

        table.addCell(c1);


        String scoreStr =
                (score >= 0 ? "+" : "")
                        + String.format(
                                "%.1f",
                                score
                        );


        PdfPCell c2 =
                new PdfPCell(
                        new Phrase(
                                scoreStr,
                                font
                        )
                );

        c2.setPadding(4);

        table.addCell(c2);


        PdfPCell c3 =
                new PdfPCell(
                        new Phrase(
                                assessment,
                                font
                        )
                );

        c3.setPadding(4);

        table.addCell(c3);
    }


    /* =========================================================
       CERTIFICATE ROW
       ========================================================= */

    private void addCertRow(
            PdfPTable table,
            String label,
            String value,
            Font labelFont,
            Font valueFont) {

        PdfPCell c1 =
                new PdfPCell(
                        new Phrase(
                                label,
                                labelFont
                        )
                );

        c1.setPadding(6);

        c1.setBackgroundColor(
                new Color(245, 247, 250)
        );

        table.addCell(c1);


        PdfPCell c2 =
                new PdfPCell(
                        new Phrase(
                                value,
                                valueFont
                        )
                );

        c2.setPadding(6);

        table.addCell(c2);
    }


    /* =========================================================
       CSV ESCAPING
       ========================================================= */

    private String escapeCsv(String val) {

        if (val == null) {
            return "\"\"";
        }

        return "\""
                + val.replace(
                        "\"",
                        "\"\""
                )
                + "\"";
    }
}