package com.davis.dto;

import java.util.List;
import java.util.Map;

public class AnalysisScoreResponse {
    private Double totalScore;
    private Double confidence;
    private String methodology;
    private Map<String, Double> breakdown;
    private List<Map<String, Object>> supportingEvidence;
    private List<Map<String, Object>> contradictoryEvidence;
    private Map<String, Object> stylometricAnalysis;
    private Map<String, Object> behaviouralAnalysis;
    private String explanation;
    private String robustnessRating;
    private String primaryDependency;

    public AnalysisScoreResponse() {}

    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }

    public Map<String, Double> getBreakdown() { return breakdown; }
    public void setBreakdown(Map<String, Double> breakdown) { this.breakdown = breakdown; }

    public List<Map<String, Object>> getSupportingEvidence() { return supportingEvidence; }
    public void setSupportingEvidence(List<Map<String, Object>> supportingEvidence) { this.supportingEvidence = supportingEvidence; }

    public List<Map<String, Object>> getContradictoryEvidence() { return contradictoryEvidence; }
    public void setContradictoryEvidence(List<Map<String, Object>> contradictoryEvidence) { this.contradictoryEvidence = contradictoryEvidence; }

    public Map<String, Object> getStylometricAnalysis() { return stylometricAnalysis; }
    public void setStylometricAnalysis(Map<String, Object> stylometricAnalysis) { this.stylometricAnalysis = stylometricAnalysis; }

    public Map<String, Object> getBehaviouralAnalysis() { return behaviouralAnalysis; }
    public void setBehaviouralAnalysis(Map<String, Object> behaviouralAnalysis) { this.behaviouralAnalysis = behaviouralAnalysis; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getRobustnessRating() { return robustnessRating; }
    public void setRobustnessRating(String robustnessRating) { this.robustnessRating = robustnessRating; }

    public String getPrimaryDependency() { return primaryDependency; }
    public void setPrimaryDependency(String primaryDependency) { this.primaryDependency = primaryDependency; }
}
