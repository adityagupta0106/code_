package com.serviceplus.tracking.dto;

public class ApplicationTrackingRequest {

    private String applId;

    private Integer serviceId;
    private String verificationProof;

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
    public String getVerificationProof() { return verificationProof; }
    public void setVerificationProof(String verificationProof) { this.verificationProof = verificationProof; }

    private String applRefNo;

    private String applyDate;

    public String getApplId() {
        return applId;
    }

    public void setApplId(String applId) {
        this.applId = applId;
    }

    public String getApplRefNo() {
        return applRefNo;
    }

    public void setApplRefNo(String applRefNo) {
        this.applRefNo = applRefNo;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }
}
