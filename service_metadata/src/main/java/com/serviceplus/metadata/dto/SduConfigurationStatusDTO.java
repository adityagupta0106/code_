package com.serviceplus.metadata.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SduConfigurationStatusDTO {

    private Integer serviceId;
    private Integer sduLocationId;
    private String sduLocationName;
    private Integer sduLevelId;
    private boolean sduConfigurationComplete;
    private boolean childLocationsAdopted;
    private boolean sduActivatable;
    private Integer currentActivationStatus;
    private String operationalStatus;
    private Map<String, Boolean> checklist = new LinkedHashMap<>();
    private List<String> rejectionReasons = new ArrayList<>();

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

    public String getSduLocationName() {
        return sduLocationName;
    }

    public void setSduLocationName(String sduLocationName) {
        this.sduLocationName = sduLocationName;
    }

    public Integer getSduLevelId() {
        return sduLevelId;
    }

    public void setSduLevelId(Integer sduLevelId) {
        this.sduLevelId = sduLevelId;
    }

    public boolean isSduConfigurationComplete() {
        return sduConfigurationComplete;
    }

    public void setSduConfigurationComplete(boolean sduConfigurationComplete) {
        this.sduConfigurationComplete = sduConfigurationComplete;
    }

    public boolean isChildLocationsAdopted() {
        return childLocationsAdopted;
    }

    public void setChildLocationsAdopted(boolean childLocationsAdopted) {
        this.childLocationsAdopted = childLocationsAdopted;
    }

    public boolean isSduActivatable() {
        return sduActivatable;
    }

    public void setSduActivatable(boolean sduActivatable) {
        this.sduActivatable = sduActivatable;
    }

    public Integer getCurrentActivationStatus() {
        return currentActivationStatus;
    }

    public void setCurrentActivationStatus(Integer currentActivationStatus) {
        this.currentActivationStatus = currentActivationStatus;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public Map<String, Boolean> getChecklist() {
        return checklist;
    }

    public void setChecklist(Map<String, Boolean> checklist) {
        this.checklist = checklist;
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(List<String> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }
}
