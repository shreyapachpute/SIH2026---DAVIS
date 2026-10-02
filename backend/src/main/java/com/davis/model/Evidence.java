package com.davis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false, length = 64)
    private String type; // CRYPTO_FORENSIC, PGP_KEY_SIGNATURE, INFRASTRUCTURE_TELEMETRY, TEMPORAL_ACTIVITY, STYLOMETRIC_MATCH, CONTRADICTORY_TEMPORAL, CONTRADICTORY_CRYPTOGRAPHIC, AI_REWRITE_SIGNAL

    @Column(nullable = false, length = 255)
    private String source;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false, length = 64)
    private String hash; // SHA-256

    private Double reliability = 0.90;

    @Column(length = 255)
    private String supports;

    @Column(length = 255)
    private String contradicts;

    @Column(name = "related_entities", length = 512)
    private String relatedEntities;

    public Evidence() {}

    public Evidence(Long caseId, String type, String source, String content, LocalDateTime timestamp, 
                    String hash, Double reliability, String supports, String contradicts, String relatedEntities) {
        this.caseId = caseId;
        this.type = type;
        this.source = source;
        this.content = content;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.hash = hash;
        this.reliability = reliability;
        this.supports = supports;
        this.contradicts = contradicts;
        this.relatedEntities = relatedEntities;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public Double getReliability() { return reliability; }
    public void setReliability(Double reliability) { this.reliability = reliability; }

    public String getSupports() { return supports; }
    public void setSupports(String supports) { this.supports = supports; }

    public String getContradicts() { return contradicts; }
    public void setContradicts(String contradicts) { this.contradicts = contradicts; }

    public String getRelatedEntities() { return relatedEntities; }
    public void setRelatedEntities(String relatedEntities) { this.relatedEntities = relatedEntities; }
}
