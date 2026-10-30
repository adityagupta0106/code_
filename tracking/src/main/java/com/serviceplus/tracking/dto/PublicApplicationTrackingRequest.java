package com.serviceplus.tracking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class PublicApplicationTrackingRequest {

    private String applRefNo;

    private String applyDate;

    public PublicApplicationTrackingRequest() {
    }

    public PublicApplicationTrackingRequest(String applRefNo, String applyDate) {
        this.applRefNo = applRefNo;
        this.applyDate = applyDate;
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
