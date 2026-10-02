package com.davis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "indicators")
public class Indicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false, length = 64)
    private String type; // USERNAME, EMAIL, PGP_KEY, CRYPTO_WALLET, ONION_ADDRESS, DOMAIN, MESSAGE

    @Column(name = "`value`", nullable = false, length = 512)
    private String value;

    @Column(length = 255)
    private String source;

    private Double confidence = 0.90;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Indicator() {}

    public Indicator(Long caseId, String type, String value, String source, Double confidence) {
        this.caseId = caseId;
        this.type = type;
        this.value = value;
        this.source = source;
        this.confidence = confidence;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
