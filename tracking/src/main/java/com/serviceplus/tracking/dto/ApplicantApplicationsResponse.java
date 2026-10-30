package com.serviceplus.tracking.dto;

import java.util.ArrayList;
import java.util.List;

public class ApplicantApplicationsResponse {

    private long totalSubmissions;

    private long requiresResponse;

    private long payments;

    private List<ApplicationApplicantTrackingResponse.ApplicantApplicationDTO> applications = new ArrayList<>();

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public long getRequiresResponse() {
        return requiresResponse;
    }

    public void setRequiresResponse(long requiresResponse) {
        this.requiresResponse = requiresResponse;
    }

    public long getPayments() {
        return payments;
    }

    public void setPayments(long payments) {
        this.payments = payments;
    }

    public List<ApplicationApplicantTrackingResponse.ApplicantApplicationDTO> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationApplicantTrackingResponse.ApplicantApplicationDTO> applications) {
        this.applications = applications;
    }
}