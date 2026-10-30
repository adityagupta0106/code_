package com.serviceplus.metadata.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdoptSubmissionLocationsResponseDTO {

    private Integer serviceId;
    private Integer sduLocationId;
    private List<Integer> successfullyAdoptedLocationIds = new ArrayList<>();
    private Map<Integer, String> failedLocations = new LinkedHashMap<>();
    private long releasedPendingApplicationsCount;
    private String message;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getSduLocationId() {
        return sduLocationId;
    }

    public void setSduLocationId(Integer sduLocationId) {
        this.sduLocationId = sduLocationId;
    }

    public List<Integer> getSuccessfullyAdoptedLocationIds() {
        return successfullyAdoptedLocationIds;
    }

    public void setSuccessfullyAdoptedLocationIds(List<Integer> successfullyAdoptedLocationIds) {
        this.successfullyAdoptedLocationIds = successfullyAdoptedLocationIds;
    }

    public Map<Integer, String> getFailedLocations() {
        return failedLocations;
    }

    public void setFailedLocations(Map<Integer, String> failedLocations) {
        this.failedLocations = failedLocations;
    }

    public long getReleasedPendingApplicationsCount() {
        return releasedPendingApplicationsCount;
    }

    public void setReleasedPendingApplicationsCount(long releasedPendingApplicationsCount) {
        this.releasedPendingApplicationsCount = releasedPendingApplicationsCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
