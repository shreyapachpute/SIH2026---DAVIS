package com.davis.model;

import jakarta.persistence.*;

@Entity
@Table(name = "entities")
public class DiscoveredEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false, length = 64)
    private String type; // PERSONA, ALIAS, EMAIL, PGP_KEY, WALLET, DOMAIN, ONION_SERVICE, FORUM_ACCOUNT

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "normalized_value", nullable = false, length = 512)
    private String normalizedValue;

    private Double confidence = 0.85;

    public DiscoveredEntity() {}

    public DiscoveredEntity(Long caseId, String type, String name, String normalizedValue, Double confidence) {
        this.caseId = caseId;
        this.type = type;
        this.name = name;
        this.normalizedValue = normalizedValue;
        this.confidence = confidence;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNormalizedValue() { return normalizedValue; }
    public void setNormalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
