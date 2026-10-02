package com.davis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "text_samples")
public class TextSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "sample_label", length = 128)
    private String sampleLabel;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(length = 32)
    private String language = "en";

    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "ai_rewrite_indicator")
    private Boolean aiRewriteIndicator = false;

    public TextSample() {}

    public TextSample(Long caseId, Long entityId, String sampleLabel, String text, String language, LocalDateTime timestamp, Boolean aiRewriteIndicator) {
        this.caseId = caseId;
        this.entityId = entityId;
        this.sampleLabel = sampleLabel;
        this.text = text;
        this.language = language;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.aiRewriteIndicator = aiRewriteIndicator != null ? aiRewriteIndicator : false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getSampleLabel() { return sampleLabel; }
    public void setSampleLabel(String sampleLabel) { this.sampleLabel = sampleLabel; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Boolean getAiRewriteIndicator() { return aiRewriteIndicator; }
    public void setAiRewriteIndicator(Boolean aiRewriteIndicator) { this.aiRewriteIndicator = aiRewriteIndicator; }
}
