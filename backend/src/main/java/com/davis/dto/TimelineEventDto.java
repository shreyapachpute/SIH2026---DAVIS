package com.davis.dto;

import java.time.LocalDateTime;

public class TimelineEventDto {
    private Long id;
    private LocalDateTime timestamp;
    private String eventType;
    private String title;
    private String description;
    private String source;
    private String relatedEntities;
    private String hash;
    private String classification; // SUPPORTING, CONTRADICTORY, NEUTRAL

    public TimelineEventDto() {}

    public TimelineEventDto(Long id, LocalDateTime timestamp, String eventType, String title, 
                            String description, String source, String relatedEntities, 
                            String hash, String classification) {
        this.id = id;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.title = title;
        this.description = description;
        this.source = source;
        this.relatedEntities = relatedEntities;
        this.hash = hash;
        this.classification = classification;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getRelatedEntities() { return relatedEntities; }
    public void setRelatedEntities(String relatedEntities) { this.relatedEntities = relatedEntities; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
}
