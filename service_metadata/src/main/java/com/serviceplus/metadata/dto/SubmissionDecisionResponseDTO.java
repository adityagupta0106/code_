package com.serviceplus.metadata.dto;

public class SubmissionDecisionResponseDTO {

    private Integer serviceId;
    private Integer submissionLocationId;
    private String submissionLocationName;
    private Integer sduLocationId;
    private String rolloutScopeStatus;
    private String adoptionStatus;
    private String sduOperationalStatus;
    private String nonAdoptedSubmissionPolicy;
    private String decision; // NORMAL_SUBMISSION, PENDING_ADOPTION, BLOCKED
    private boolean allowed;
    private boolean pendingAdoption;
    private String reason;
    private String slaStartPolicy; // ON_SUBMISSION, ON_ADOPTION
    private String message;
    private java.util.Map<Integer, SubmissionDecisionResponseDTO> locationDecisions;

    public java.util.Map<Integer, SubmissionDecisionResponseDTO> getLocationDecisions() {
        return locationDecisions;
    }

    public void setLocationDecisions(java.util.Map<Integer, SubmissionDecisionResponseDTO> locationDecisions) {
        this.locationDecisions = locationDecisions;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

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

	public Integer getSduLocationId() {
        return sduLocationId;
    }

    public void setSduLocationId(Integer sduLocationId) {
        this.sduLocationId = sduLocationId;
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

    public String getSduOperationalStatus() {
        return sduOperationalStatus;
    }

    public void setSduOperationalStatus(String sduOperationalStatus) {
        this.sduOperationalStatus = sduOperationalStatus;
    }

    public String getNonAdoptedSubmissionPolicy() {
        return nonAdoptedSubmissionPolicy;
    }

    public void setNonAdoptedSubmissionPolicy(String nonAdoptedSubmissionPolicy) {
        this.nonAdoptedSubmissionPolicy = nonAdoptedSubmissionPolicy;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public boolean isPendingAdoption() {
        return pendingAdoption;
    }

    public void setPendingAdoption(boolean pendingAdoption) {
        this.pendingAdoption = pendingAdoption;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSlaStartPolicy() {
        return slaStartPolicy;
    }

    public void setSlaStartPolicy(String slaStartPolicy) {
        this.slaStartPolicy = slaStartPolicy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
