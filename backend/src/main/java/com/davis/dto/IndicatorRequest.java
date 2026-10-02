package com.davis.dto;

import jakarta.validation.constraints.NotBlank;

public class IndicatorRequest {
    @NotBlank
    private String type;

    @NotBlank
    private String value;

    private String source = "Manual Analyst Input";
    private Double confidence = 0.90;

    public IndicatorRequest() {}

    public IndicatorRequest(String type, String value, String source, Double confidence) {
        this.type = type;
        this.value = value;
        this.source = source != null ? source : "Manual Analyst Input";
        this.confidence = confidence != null ? confidence : 0.90;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
