package com.davis.dto;

import java.util.List;

public class GraphResponseDto {
    private List<GraphNodeDto> nodes;
    private List<GraphEdgeDto> edges;

    public GraphResponseDto() {}

    public GraphResponseDto(List<GraphNodeDto> nodes, List<GraphEdgeDto> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<GraphNodeDto> getNodes() { return nodes; }
    public void setNodes(List<GraphNodeDto> nodes) { this.nodes = nodes; }

    public List<GraphEdgeDto> getEdges() { return edges; }
    public void setEdges(List<GraphEdgeDto> edges) { this.edges = edges; }
}
