package com.davis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stress_tests")
public class StressTestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "removed_dependency", nullable = false, length = 128)
    private String removedDependency;

    @Column(name = "original_score", nullable = false)
    private Double originalScore;

    @Column(name = "modified_score", nullable = false)
    private Double modifiedScore;

    @Column(name = "score_delta", nullable = false)
    private Double scoreDelta;

    @Column(nullable = false, length = 64)
    private String result; // HIGH_ROBUSTNESS, MODERATE_ROBUSTNESS, LOW_ROBUSTNESS, CRITICAL_DEPENDENCY

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public StressTestRecord() {}

    public StressTestRecord(Long caseId, String removedDependency, Double originalScore, Double modifiedScore, Double scoreDelta, String result, String explanation) {
        this.caseId = caseId;
        this.removedDependency = removedDependency;
        this.originalScore = originalScore;
        this.modifiedScore = modifiedScore;
        this.scoreDelta = scoreDelta;
        this.result = result;
        this.explanation = explanation;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getRemovedDependency() { return removedDependency; }
    public void setRemovedDependency(String removedDependency) { this.removedDependency = removedDependency; }

    public Double getOriginalScore() { return originalScore; }
    public void setOriginalScore(Double originalScore) { this.originalScore = originalScore; }

    public Double getModifiedScore() { return modifiedScore; }
    public void setModifiedScore(Double modifiedScore) { this.modifiedScore = modifiedScore; }

    public Double getScoreDelta() { return scoreDelta; }
    public void setScoreDelta(Double scoreDelta) { this.scoreDelta = scoreDelta; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
