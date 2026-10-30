package com.serviceplus.metadata.dto;

import java.io.Serializable;
import java.util.List;

public class ServiceTrackingConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> trackingTypes;
    private String mobileAttributeId;
    private String emailAttributeId;
    private List<String> selectedAttributeIds;
    private Boolean displayActionTakenDate;

    public ServiceTrackingConfigDTO() {
    }

    public ServiceTrackingConfigDTO(List<String> trackingTypes, String mobileAttributeId, String emailAttributeId,
                                    List<String> selectedAttributeIds, Boolean displayActionTakenDate) {
        this.trackingTypes = trackingTypes;
        this.mobileAttributeId = mobileAttributeId;
        this.emailAttributeId = emailAttributeId;
        this.selectedAttributeIds = selectedAttributeIds;
        this.displayActionTakenDate = displayActionTakenDate;
    }

    public List<String> getTrackingTypes() {
        return trackingTypes;
    }

    public void setTrackingTypes(List<String> trackingTypes) {
        this.trackingTypes = trackingTypes;
    }

    public String getMobileAttributeId() {
        return mobileAttributeId;
    }

    public void setMobileAttributeId(String mobileAttributeId) {
        this.mobileAttributeId = mobileAttributeId;
    }

    public String getEmailAttributeId() {
        return emailAttributeId;
    }

    public void setEmailAttributeId(String emailAttributeId) {
        this.emailAttributeId = emailAttributeId;
    }

    public List<String> getSelectedAttributeIds() {
        return selectedAttributeIds;
    }

    public void setSelectedAttributeIds(List<String> selectedAttributeIds) {
        this.selectedAttributeIds = selectedAttributeIds;
    }

    public Boolean getDisplayActionTakenDate() {
        return displayActionTakenDate;
    }

    public void setDisplayActionTakenDate(Boolean displayActionTakenDate) {
        this.displayActionTakenDate = displayActionTakenDate;
    }

    @Override
    public String toString() {
        return "ServiceTrackingConfigDTO{" +
                "trackingTypes=" + trackingTypes +
                ", mobileAttributeId='" + mobileAttributeId + '\'' +
                ", emailAttributeId='" + emailAttributeId + '\'' +
                ", selectedAttributeIds=" + selectedAttributeIds +
                ", displayActionTakenDate=" + displayActionTakenDate +
                '}';
    }
}