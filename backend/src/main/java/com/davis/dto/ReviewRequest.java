package com.davis.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewRequest {
    @NotBlank
    private String analyst;

    @NotBlank
    private String decision; // REVIEWED, NEEDS_FURTHER_INVESTIGATION, EVIDENCE_CONFLICT, APPROVED_LEAD

    private String comments;

    public ReviewRequest() {}

    public ReviewRequest(String analyst, String decision, String comments) {
        this.analyst = analyst;
        this.decision = decision;
        this.comments = comments;
    }

    public String getAnalyst() { return analyst; }
    public void setAnalyst(String analyst) { this.analyst = analyst; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
