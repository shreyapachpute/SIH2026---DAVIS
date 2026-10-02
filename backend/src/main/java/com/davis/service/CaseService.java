package com.davis.service;

import com.davis.dto.CaseDto;
import com.davis.dto.TimelineEventDto;
import com.davis.model.*;
import com.davis.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final IndicatorRepository indicatorRepository;
    private final DiscoveredEntityRepository entityRepository;
    private final RelationshipRepository relationshipRepository;
    private final EvidenceRepository evidenceRepository;
    private final CryptoTransactionRepository transactionRepository;
    private final TextSampleRepository textSampleRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final StressTestRepository stressTestRepository;
    private final ReviewRepository reviewRepository;
    private final AuditService auditService;
    private final IntegrityService integrityService;

    @Autowired
    public CaseService(CaseRepository caseRepository,
                       IndicatorRepository indicatorRepository,
                       DiscoveredEntityRepository entityRepository,
                       RelationshipRepository relationshipRepository,
                       EvidenceRepository evidenceRepository,
                       CryptoTransactionRepository transactionRepository,
                       TextSampleRepository textSampleRepository,
                       AnalysisResultRepository analysisResultRepository,
                       StressTestRepository stressTestRepository,
                       ReviewRepository reviewRepository,
                       AuditService auditService,
                       IntegrityService integrityService) {
        this.caseRepository = caseRepository;
        this.indicatorRepository = indicatorRepository;
        this.entityRepository = entityRepository;
        this.relationshipRepository = relationshipRepository;
        this.evidenceRepository = evidenceRepository;
        this.transactionRepository = transactionRepository;
        this.textSampleRepository = textSampleRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.stressTestRepository = stressTestRepository;
        this.reviewRepository = reviewRepository;
        this.auditService = auditService;
        this.integrityService = integrityService;
    }

    public List<CaseDto> getAllCases() {
        return caseRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<CaseDto> getCaseById(Long id) {
        return caseRepository.findById(id).map(this::toDto);
    }

    public Optional<CaseDto> getCaseByNumber(String caseNumber) {
        return caseRepository.findByCaseNumber(caseNumber).map(this::toDto);
    }

    @Transactional
    public CaseDto createCase(CaseDto dto) {
        String num = dto.getCaseNumber();
        if (num == null || num.trim().isEmpty()) {
            num = "DAVIS-CASE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        CaseEntity entity = new CaseEntity(num, dto.getTitle(), dto.getDescription(), 
                dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        CaseEntity saved = caseRepository.save(entity);
        auditService.logAction(saved.getId(), "CASE_CREATED", "Analyst", "Created case " + saved.getCaseNumber());
        return toDto(saved);
    }

    @Transactional
    public CaseDto updateCase(Long id, CaseDto dto) {
        CaseEntity entity = caseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + id));
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());
        CaseEntity saved = caseRepository.save(entity);
        auditService.logAction(id, "CASE_UPDATED", "Analyst", "Updated case " + saved.getCaseNumber());
        return toDto(saved);
    }

    public List<TimelineEventDto> getTimeline(Long caseId) {
        List<TimelineEventDto> timeline = new ArrayList<>();

        // Add indicators
        List<Indicator> indicators = indicatorRepository.findByCaseId(caseId);
        for (Indicator ind : indicators) {
            timeline.add(new TimelineEventDto(
                    ind.getId(),
                    ind.getCreatedAt(),
                    "INDICATOR_INGESTED",
                    "Indicator Ingested: " + ind.getType(),
                    "Ingested " + ind.getType() + " value '" + ind.getValue() + "' from " + ind.getSource(),
                    ind.getSource(),
                    ind.getValue(),
                    integrityService.computeSha256(ind.getType() + ind.getValue()),
                    "NEUTRAL"
            ));
        }

        // Add evidence
        List<Evidence> evidenceList = evidenceRepository.findByCaseId(caseId);
        for (Evidence ev : evidenceList) {
            String classification = "NEUTRAL";
            if (ev.getSupports() != null && !ev.getSupports().isEmpty()) classification = "SUPPORTING";
            if (ev.getContradicts() != null && !ev.getContradicts().isEmpty()) classification = "CONTRADICTORY";

            timeline.add(new TimelineEventDto(
                    ev.getId(),
                    ev.getTimestamp(),
                    "EVIDENCE_DISCOVERED",
                    "Evidence: " + ev.getType(),
                    ev.getContent(),
                    ev.getSource(),
                    ev.getRelatedEntities(),
                    ev.getHash(),
                    classification
            ));
        }

        // Add transactions
        List<CryptoTransaction> transactions = transactionRepository.findByCaseId(caseId);
        for (CryptoTransaction tx : transactions) {
            timeline.add(new TimelineEventDto(
                    tx.getId(),
                    tx.getTimestamp(),
                    "BLOCKCHAIN_TX",
                    "Crypto Transfer: " + tx.getAmount() + " " + tx.getAsset(),
                    "Transfer from " + tx.getWalletFrom() + " to " + tx.getWalletTo() + " (TX: " + tx.getTransactionHash() + ")",
                    "Synthetic Blockchain Explorer",
                    tx.getWalletTo(),
                    integrityService.computeSha256(tx.getTransactionHash()),
                    "SUPPORTING"
            ));
        }

        // Add reviews
        List<ReviewRecord> reviews = reviewRepository.findByCaseIdOrderByReviewedAtDesc(caseId);
        for (ReviewRecord rev : reviews) {
            timeline.add(new TimelineEventDto(
                    rev.getId(),
                    rev.getReviewedAt(),
                    "ANALYST_REVIEW",
                    "Analyst Review: " + rev.getDecision(),
                    "Decision by " + rev.getAnalyst() + ": " + rev.getComments(),
                    "Human Analyst Review",
                    "Case Decision",
                    integrityService.computeSha256(rev.getDecision() + rev.getComments()),
                    "NEUTRAL"
            ));
        }

        timeline.sort(Comparator.comparing(TimelineEventDto::getTimestamp));
        return timeline;
    }

    private CaseDto toDto(CaseEntity e) {
        return new CaseDto(e.getId(), e.getCaseNumber(), e.getTitle(), e.getDescription(), e.getStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
