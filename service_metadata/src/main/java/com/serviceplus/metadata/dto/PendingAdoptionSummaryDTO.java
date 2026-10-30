package com.serviceplus.metadata.dto;

import java.util.Date;

public class PendingAdoptionSummaryDTO {

    private Integer serviceId;
    private Integer sduLocationId;
    private Integer submissionLocationId;
    private String submissionLocationName;
    private long pendingCount;
    private Date oldestSubmissionDate;
    private Date latestSubmissionDate;

    public PendingAdoptionSummaryDTO() {
    }

    public PendingAdoptionSummaryDTO(Integer serviceId, Integer sduLocationId, Integer submissionLocationId,
                                    String submissionLocationName, long pendingCount,
                                    Date oldestSubmissionDate, Date latestSubmissionDate) {
        this.serviceId = serviceId;
        this.sduLocationId = sduLocationId;
        this.submissionLocationId = submissionLocationId;
        this.submissionLocationName = submissionLocationName;
        this.pendingCount = pendingCount;
        this.oldestSubmissionDate = oldestSubmissionDate;
        this.latestSubmissionDate = latestSubmissionDate;
    }

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

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public Date getOldestSubmissionDate() {
        return oldestSubmissionDate;
    }

    public void setOldestSubmissionDate(Date oldestSubmissionDate) {
        this.oldestSubmissionDate = oldestSubmissionDate;
    }

    public Date getLatestSubmissionDate() {
        return latestSubmissionDate;
    }

    public void setLatestSubmissionDate(Date latestSubmissionDate) {
        this.latestSubmissionDate = latestSubmissionDate;
    }
}
