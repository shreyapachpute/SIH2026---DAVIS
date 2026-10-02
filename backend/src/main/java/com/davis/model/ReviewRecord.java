package com.davis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class ReviewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false, length = 128)
    private String analyst;

    @Column(nullable = false, length = 64)
    private String decision; // REVIEWED, NEEDS_FURTHER_INVESTIGATION, EVIDENCE_CONFLICT, APPROVED_LEAD

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt = LocalDateTime.now();

    public ReviewRecord() {}

    public ReviewRecord(Long caseId, String analyst, String decision, String comments) {
        this.caseId = caseId;
        this.analyst = analyst;
        this.decision = decision;
        this.comments = comments;
        this.reviewedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getAnalyst() { return analyst; }
    public void setAnalyst(String analyst) { this.analyst = analyst; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
