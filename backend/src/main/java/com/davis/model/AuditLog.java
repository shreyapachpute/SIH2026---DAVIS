package com.davis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false, length = 128)
    private String action;

    @Column(nullable = false, length = 128)
    private String actor;

    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false, length = 64)
    private String hash; // SHA-256 for immutable audit trail

    public AuditLog() {}

    public AuditLog(Long caseId, String action, String actor, LocalDateTime timestamp, String details, String hash) {
        this.caseId = caseId;
        this.action = action;
        this.actor = actor;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.details = details;
        this.hash = hash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
}
