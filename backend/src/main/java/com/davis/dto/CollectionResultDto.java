package com.davis.dto;

import java.util.List;

public class CollectionResultDto {
    private String status;
    private String message;
    private String collectionScope;
    private int indicatorsProcessed;
    private int entitiesDiscovered;
    private int relationshipsCreated;
    private int evidenceRecordsCollected;
    private List<String> collectionLogs;

    public CollectionResultDto() {}

    public CollectionResultDto(String status, String message, String collectionScope, int indicatorsProcessed, 
                               int entitiesDiscovered, int relationshipsCreated, int evidenceRecordsCollected, 
                               List<String> collectionLogs) {
        this.status = status;
        this.message = message;
        this.collectionScope = collectionScope;
        this.indicatorsProcessed = indicatorsProcessed;
        this.entitiesDiscovered = entitiesDiscovered;
        this.relationshipsCreated = relationshipsCreated;
        this.evidenceRecordsCollected = evidenceRecordsCollected;
        this.collectionLogs = collectionLogs;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCollectionScope() { return collectionScope; }
    public void setCollectionScope(String collectionScope) { this.collectionScope = collectionScope; }
    public int getIndicatorsProcessed() { return indicatorsProcessed; }
    public void setIndicatorsProcessed(int indicatorsProcessed) { this.indicatorsProcessed = indicatorsProcessed; }
    public int getEntitiesDiscovered() { return entitiesDiscovered; }
    public void setEntitiesDiscovered(int entitiesDiscovered) { this.entitiesDiscovered = entitiesDiscovered; }
    public int getRelationshipsCreated() { return relationshipsCreated; }
    public void setRelationshipsCreated(int relationshipsCreated) { this.relationshipsCreated = relationshipsCreated; }
    public int getEvidenceRecordsCollected() { return evidenceRecordsCollected; }
    public void setEvidenceRecordsCollected(int evidenceRecordsCollected) { this.evidenceRecordsCollected = evidenceRecordsCollected; }
    public List<String> getCollectionLogs() { return collectionLogs; }
    public void setCollectionLogs(List<String> collectionLogs) { this.collectionLogs = collectionLogs; }
}
