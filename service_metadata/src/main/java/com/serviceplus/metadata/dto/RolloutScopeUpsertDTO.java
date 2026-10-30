package com.serviceplus.metadata.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RolloutScopeUpsertDTO {

    @NotNull(message = "serviceId is required")
    private Integer serviceId;

    @NotNull(message = "deliveryUnitLevelId is required")
    private Integer deliveryUnitLevelId;

    @NotEmpty(message = "locations list cannot be empty")
    private List<RolloutLocationItem> locations;

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

    public List<RolloutLocationItem> getLocations() {
        return locations;
    }

    public void setLocations(List<RolloutLocationItem> locations) {
        this.locations = locations;
    }

    public static class RolloutLocationItem {
        @NotNull(message = "locationId is required")
        private Integer locationId;
        private String locationName;
        private Integer parentLocationId;
        @NotNull(message = "scopeStatus is required")
        private String scopeStatus; // IN_SCOPE or OUT_OF_SCOPE

        public Integer getLocationId() {
            return locationId;
        }

        public void setLocationId(Integer locationId) {
            this.locationId = locationId;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public Integer getParentLocationId() {
            return parentLocationId;
        }

        public void setParentLocationId(Integer parentLocationId) {
            this.parentLocationId = parentLocationId;
        }

        public String getScopeStatus() {
            return scopeStatus;
        }

        public void setScopeStatus(String scopeStatus) {
            this.scopeStatus = scopeStatus;
        }
    }
}
