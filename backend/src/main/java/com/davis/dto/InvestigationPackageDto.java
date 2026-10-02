package com.davis.dto;

import com.davis.model.*;
import java.util.List;
import java.util.Map;

public class InvestigationPackageDto {
    private CaseEntity caseDetails;
    private List<Indicator> indicators;
    private List<DiscoveredEntity> entities;
    private List<Relationship> relationships;
    private List<Evidence> evidence;
    private List<TextSample> textSamples;
    private List<CryptoTransaction> transactions;
    private AnalysisResult analysis;
    private List<StressTestRecord> stressTests;
    private List<TimelineEventDto> timeline;
    private ReviewRecord review;
    private List<AuditLog> auditTrail;
    private Map<String, Object> integrity;

    public InvestigationPackageDto() {}

    public CaseEntity getCaseDetails() { return caseDetails; }
    public void setCaseDetails(CaseEntity caseDetails) { this.caseDetails = caseDetails; }

    public List<Indicator> getIndicators() { return indicators; }
    public void setIndicators(List<Indicator> indicators) { this.indicators = indicators; }

    public List<DiscoveredEntity> getEntities() { return entities; }
    public void setEntities(List<DiscoveredEntity> entities) { this.entities = entities; }

    public List<Relationship> getRelationships() { return relationships; }
    public void setRelationships(List<Relationship> relationships) { this.relationships = relationships; }

    public List<Evidence> getEvidence() { return evidence; }
    public void setEvidence(List<Evidence> evidence) { this.evidence = evidence; }

    public List<TextSample> getTextSamples() { return textSamples; }
    public void setTextSamples(List<TextSample> textSamples) { this.textSamples = textSamples; }

    public List<CryptoTransaction> getTransactions() { return transactions; }
    public void setTransactions(List<CryptoTransaction> transactions) { this.transactions = transactions; }

    public AnalysisResult getAnalysis() { return analysis; }
    public void setAnalysis(AnalysisResult analysis) { this.analysis = analysis; }

    public List<StressTestRecord> getStressTests() { return stressTests; }
    public void setStressTests(List<StressTestRecord> stressTests) { this.stressTests = stressTests; }

    public List<TimelineEventDto> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineEventDto> timeline) { this.timeline = timeline; }

    public ReviewRecord getReview() { return review; }
    public void setReview(ReviewRecord review) { this.review = review; }

    public List<AuditLog> getAuditTrail() { return auditTrail; }
    public void setAuditTrail(List<AuditLog> auditTrail) { this.auditTrail = auditTrail; }

    public Map<String, Object> getIntegrity() { return integrity; }
    public void setIntegrity(Map<String, Object> integrity) { this.integrity = integrity; }
}
