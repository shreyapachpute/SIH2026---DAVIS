package com.davis.service;

import com.davis.dto.GraphEdgeDto;
import com.davis.dto.GraphNodeDto;
import com.davis.dto.GraphResponseDto;
import com.davis.model.DiscoveredEntity;
import com.davis.model.Relationship;
import com.davis.repository.DiscoveredEntityRepository;
import com.davis.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EntityResolutionService {

    private final DiscoveredEntityRepository entityRepository;
    private final RelationshipRepository relationshipRepository;

    @Autowired
    public EntityResolutionService(DiscoveredEntityRepository entityRepository, RelationshipRepository relationshipRepository) {
        this.entityRepository = entityRepository;
        this.relationshipRepository = relationshipRepository;
    }

    public GraphResponseDto getGraph(Long caseId) {
        List<DiscoveredEntity> entities = entityRepository.findByCaseId(caseId);
        List<Relationship> relationships = relationshipRepository.findByCaseId(caseId);

        List<GraphNodeDto> nodes = new ArrayList<>();
        for (DiscoveredEntity e : entities) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", "entity_" + e.getId());
            data.put("entityId", e.getId());
            data.put("name", e.getName());
            data.put("type", e.getType());
            data.put("normalizedValue", e.getNormalizedValue());
            data.put("confidence", e.getConfidence());
            nodes.add(new GraphNodeDto(data));
        }

        List<GraphEdgeDto> edges = new ArrayList<>();
        for (Relationship r : relationships) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", "rel_" + r.getId());
            data.put("relationshipId", r.getId());
            data.put("source", "entity_" + r.getSourceEntityId());
            data.put("target", "entity_" + r.getTargetEntityId());
            data.put("label", r.getRelationshipType());
            data.put("confidence", r.getConfidence());
            data.put("evidenceId", r.getEvidenceId());
            edges.add(new GraphEdgeDto(data));
        }

        return new GraphResponseDto(nodes, edges);
    }

    public String normalizeValue(String type, String value) {
        if (value == null) return "";
        String clean = value.trim();
        switch (type.toUpperCase()) {
            case "USERNAME":
            case "ALIAS":
                return "alias:" + clean.toLowerCase().replaceAll("[^a-z0-9_-]", "");
            case "EMAIL":
                return "email:" + clean.toLowerCase();
            case "PGP_KEY":
                return "pgp:" + clean.toLowerCase().replaceAll("\\s+", "");
            case "CRYPTO_WALLET":
            case "WALLET":
                return "wallet:" + clean.toLowerCase();
            case "DOMAIN":
                return "domain:" + clean.toLowerCase();
            case "ONION_ADDRESS":
            case "ONION_SERVICE":
                return "onion:" + clean.toLowerCase();
            default:
                return type.toLowerCase() + ":" + clean.toLowerCase();
        }
    }
}
