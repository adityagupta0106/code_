package com.serviceplus.metadata.dto;

import java.util.ArrayList;
import java.util.List;

public class SduHierarchyViewDTO {

    private Integer serviceId;
    private String serviceName;
    private String designatedOfficerJurisdiction;
    private List<SduNodeDTO> sduList = new ArrayList<>();

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDesignatedOfficerJurisdiction() {
        return designatedOfficerJurisdiction;
    }

    public void setDesignatedOfficerJurisdiction(String designatedOfficerJurisdiction) {
        this.designatedOfficerJurisdiction = designatedOfficerJurisdiction;
    }

    public List<SduNodeDTO> getSduList() {
        return sduList;
    }

    public void setSduList(List<SduNodeDTO> sduList) {
        this.sduList = sduList;
    }

    public static class SduNodeDTO {
        private Integer sduLocationId;
        private String sduLocationName;
        private Integer sduLevelId;
        private String sduLevelName;
        private String rolloutScopeStatus;
        private String operationalStatus;
        private boolean configurationComplete;
        private List<String> missingConfigurations = new ArrayList<>();
        private boolean hasAdoptedChild;
        private boolean activatable;
        private List<SubmissionLocationNodeDTO> submissionLocations = new ArrayList<>();

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

        public String getSduLevelName() {
            return sduLevelName;
        }

        public void setSduLevelName(String sduLevelName) {
            this.sduLevelName = sduLevelName;
        }

        public String getRolloutScopeStatus() {
            return rolloutScopeStatus;
        }

        public void setRolloutScopeStatus(String rolloutScopeStatus) {
            this.rolloutScopeStatus = rolloutScopeStatus;
        }

        public String getOperationalStatus() {
            return operationalStatus;
        }

        public void setOperationalStatus(String operationalStatus) {
            this.operationalStatus = operationalStatus;
        }

        public boolean isConfigurationComplete() {
            return configurationComplete;
        }

        public void setConfigurationComplete(boolean configurationComplete) {
            this.configurationComplete = configurationComplete;
        }

        public List<String> getMissingConfigurations() {
            return missingConfigurations;
        }

        public void setMissingConfigurations(List<String> missingConfigurations) {
            this.missingConfigurations = missingConfigurations;
        }

        public boolean isHasAdoptedChild() {
            return hasAdoptedChild;
        }

        public void setHasAdoptedChild(boolean hasAdoptedChild) {
            this.hasAdoptedChild = hasAdoptedChild;
        }

        public boolean isActivatable() {
            return activatable;
        }

        public void setActivatable(boolean activatable) {
            this.activatable = activatable;
        }

        public List<SubmissionLocationNodeDTO> getSubmissionLocations() {
            return submissionLocations;
        }

        public void setSubmissionLocations(List<SubmissionLocationNodeDTO> submissionLocations) {
            this.submissionLocations = submissionLocations;
        }
    }

    public static class SubmissionLocationNodeDTO {
        private Integer submissionLocationId;
        private String submissionLocationName;
        private Integer submissionLevelId;
        private String submissionLevelName;
        private String rolloutScopeStatus;
        private String adoptionStatus;
        private String operationalStatus;
        private long pendingApplicationCount;
        private boolean eligibleForAdoption;

        public Integer getSubmissionLocationId() {
            return submissionLocationId;
        }

        public void setSubmissionLocationId(Integer submissionLocationId) {
            this.submissionLocationId = submissionLocationId;
        }

        public String getSubmissionLocationName() {
            return submissionLocationName;
        }

        public void setSubmissionLocationName(String submissionLocationName) {
            this.submissionLocationName = submissionLocationName;
        }

        public Integer getSubmissionLevelId() {
            return submissionLevelId;
        }

        public void setSubmissionLevelId(Integer submissionLevelId) {
            this.submissionLevelId = submissionLevelId;
        }

        public String getSubmissionLevelName() {
            return submissionLevelName;
        }

        public void setSubmissionLevelName(String submissionLevelName) {
            this.submissionLevelName = submissionLevelName;
        }

        public String getRolloutScopeStatus() {
            return rolloutScopeStatus;
        }

        public void setRolloutScopeStatus(String rolloutScopeStatus) {
            this.rolloutScopeStatus = rolloutScopeStatus;
        }

        public String getAdoptionStatus() {
            return adoptionStatus;
        }

        public void setAdoptionStatus(String adoptionStatus) {
            this.adoptionStatus = adoptionStatus;
        }

        public String getOperationalStatus() {
            return operationalStatus;
        }

        public void setOperationalStatus(String operationalStatus) {
            this.operationalStatus = operationalStatus;
        }

        public long getPendingApplicationCount() {
            return pendingApplicationCount;
        }

        public void setPendingApplicationCount(long pendingApplicationCount) {
            this.pendingApplicationCount = pendingApplicationCount;
        }

        public boolean isEligibleForAdoption() {
            return eligibleForAdoption;
        }

        public void setEligibleForAdoption(boolean eligibleForAdoption) {
            this.eligibleForAdoption = eligibleForAdoption;
        }
    }
}
