package com.davis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false, length = 255)
    private String methodology;

    @Column(name = "breakdown_json", columnDefinition = "TEXT")
    private String breakdownJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public AnalysisResult() {}

    public AnalysisResult(Long caseId, Double score, Double confidence, String methodology, String breakdownJson) {
        this.caseId = caseId;
        this.score = score;
        this.confidence = confidence;
        this.methodology = methodology;
        this.breakdownJson = breakdownJson;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }

    public String getBreakdownJson() { return breakdownJson; }
    public void setBreakdownJson(String breakdownJson) { this.breakdownJson = breakdownJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
