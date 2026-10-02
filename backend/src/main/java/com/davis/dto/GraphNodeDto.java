package com.davis.dto;

import java.util.Map;

public class GraphNodeDto {
    private Map<String, Object> data;

    public GraphNodeDto() {}

    public GraphNodeDto(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
