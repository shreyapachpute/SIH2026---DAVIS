package com.davis.model;

import jakarta.persistence.*;

@Entity
@Table(name = "relationships")
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "source_entity_id", nullable = false)
    private Long sourceEntityId;

    @Column(name = "target_entity_id", nullable = false)
    private Long targetEntityId;

    @Column(name = "relationship_type", nullable = false, length = 64)
    private String relationshipType; // OWNS_ALIAS, REGISTERED_WITH, USES_PGP, CONTROLS_WALLET, OPERATES_SERVICE, BRIDGED_TO_DOMAIN, POSTS_ON_FORUM, TRANSFERS_TO, SIGNED_BY_CONFLICT

    private Double confidence = 0.85;

    @Column(name = "evidence_id")
    private Long evidenceId;

    public Relationship() {}

    public Relationship(Long caseId, Long sourceEntityId, Long targetEntityId, String relationshipType, Double confidence, Long evidenceId) {
        this.caseId = caseId;
        this.sourceEntityId = sourceEntityId;
        this.targetEntityId = targetEntityId;
        this.relationshipType = relationshipType;
        this.confidence = confidence;
        this.evidenceId = evidenceId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public Long getSourceEntityId() { return sourceEntityId; }
    public void setSourceEntityId(Long sourceEntityId) { this.sourceEntityId = sourceEntityId; }

    public Long getTargetEntityId() { return targetEntityId; }
    public void setTargetEntityId(Long targetEntityId) { this.targetEntityId = targetEntityId; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }
}
