package com.serviceplus.metadata.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SduActivationResponseDTO {

    private Integer serviceId;
    private List<Integer> activatedSduLocationIds = new ArrayList<>();
    private Map<Integer, List<String>> rejectedSduLocations = new LinkedHashMap<>();
    private String message;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public List<Integer> getActivatedSduLocationIds() {
        return activatedSduLocationIds;
    }

    public void setActivatedSduLocationIds(List<Integer> activatedSduLocationIds) {
        this.activatedSduLocationIds = activatedSduLocationIds;
    }

    public Map<Integer, List<String>> getRejectedSduLocations() {
        return rejectedSduLocations;
    }

    public void setRejectedSduLocations(Map<Integer, List<String>> rejectedSduLocations) {
        this.rejectedSduLocations = rejectedSduLocations;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
