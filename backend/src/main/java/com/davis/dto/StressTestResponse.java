package com.davis.dto;

import java.util.Map;

public class StressTestResponse {
    private String removedDependency;
    private Double originalScore;
    private Double modifiedScore;
    private Double scoreDelta;
    private String robustnessRating;
    private String explanation;
    private Map<String, Double> remainingBreakdown;
    private String pivotRecommendation;

    public StressTestResponse() {}

    public String getRemovedDependency() { return removedDependency; }
    public void setRemovedDependency(String removedDependency) { this.removedDependency = removedDependency; }

    public Double getOriginalScore() { return originalScore; }
    public void setOriginalScore(Double originalScore) { this.originalScore = originalScore; }

    public Double getModifiedScore() { return modifiedScore; }
    public void setModifiedScore(Double modifiedScore) { this.modifiedScore = modifiedScore; }

    public Double getScoreDelta() { return scoreDelta; }
    public void setScoreDelta(Double scoreDelta) { this.scoreDelta = scoreDelta; }

    public String getRobustnessRating() { return robustnessRating; }
    public void setRobustnessRating(String robustnessRating) { this.robustnessRating = robustnessRating; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Map<String, Double> getRemainingBreakdown() { return remainingBreakdown; }
    public void setRemainingBreakdown(Map<String, Double> remainingBreakdown) { this.remainingBreakdown = remainingBreakdown; }

    public String getPivotRecommendation() { return pivotRecommendation; }
    public void setPivotRecommendation(String pivotRecommendation) { this.pivotRecommendation = pivotRecommendation; }
}
