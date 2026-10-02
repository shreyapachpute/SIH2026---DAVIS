package com.davis.dto;

import jakarta.validation.constraints.NotBlank;

public class StressTestRequest {
    @NotBlank
    private String removedDependency; // PGP_KEY, CRYPTO_WALLET, STYLOMETRY, ALIAS, TEMPORAL, BEHAVIOURAL

    public StressTestRequest() {}

    public StressTestRequest(String removedDependency) {
        this.removedDependency = removedDependency;
    }

    public String getRemovedDependency() { return removedDependency; }
    public void setRemovedDependency(String removedDependency) { this.removedDependency = removedDependency; }
}
