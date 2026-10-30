package com.serviceplus.metadata.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SduActivationRequestDTO {

    @NotNull(message = "serviceId is required")
    private Integer serviceId;

    @NotNull(message = "deliveryUnitLevelId is required")
    private Integer deliveryUnitLevelId;

    @NotEmpty(message = "locationIds cannot be empty")
    private List<Integer> locationIds;

    private String remarks;

    public SduActivationRequestDTO() {
    }

    public SduActivationRequestDTO(Integer serviceId, Integer deliveryUnitLevelId, List<Integer> locationIds, String remarks) {
        this.serviceId = serviceId;
        this.deliveryUnitLevelId = deliveryUnitLevelId;
        this.locationIds = locationIds;
        this.remarks = remarks;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getDeliveryUnitLevelId() {
        return deliveryUnitLevelId;
    }

    public void setDeliveryUnitLevelId(Integer deliveryUnitLevelId) {
        this.deliveryUnitLevelId = deliveryUnitLevelId;
    }

    public List<Integer> getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(List<Integer> locationIds) {
        this.locationIds = locationIds;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
