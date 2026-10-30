package com.serviceplus.tracking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserInboxKafkaDTO {
	
	private String serviceName;
	private String applicationRefNo;	
	private List<OfficeDetailsDTO> officeDetails;
    private Long appliedBy;
    private String beneficiaryName;
    private LocalDateTime applyDate;
    private Integer loggedInUserLocation;
    private Long loggedInUserId;
    private boolean completeClosure;
    private String actionTinyUrl;


    private List<ApplicationCurrentProcess> processList;
    private List<ApplicantTaskDetails> applicantTaskDetails;
    
    
    
    
    private String callbackSourceProcessId;

    public String getCallbackSourceProcessId() {
        return callbackSourceProcessId;
    }

    public void setCallbackSourceProcessId(String callbackSourceProcessId) {
        this.callbackSourceProcessId = callbackSourceProcessId;}
    
    

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getApplicationRefNo() {
		return applicationRefNo;
	}

	public void setApplicationRefNo(String applicationRefNo) {
		this.applicationRefNo = applicationRefNo;
	}

	public List<OfficeDetailsDTO> getOfficeDetails() {
		return officeDetails;
	}

	public void setOfficeDetails(List<OfficeDetailsDTO> officeDetails) {
		this.officeDetails = officeDetails;
	}

	public List<ApplicationCurrentProcess> getProcessList() {
		return processList;
	}

	public void setProcessList(List<ApplicationCurrentProcess> processList) {
		this.processList = processList;
	}

    public Long getAppliedBy() {
        return appliedBy;
    }

    public void setAppliedBy(Long appliedBy) {
        this.appliedBy = appliedBy;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public LocalDateTime getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(LocalDateTime applyDate) {
        this.applyDate = applyDate;
    }

    public Integer getLoggedInUserLocation() {
        return loggedInUserLocation;
    }

    public void setLoggedInUserLocation(Integer loggedInUserLocation) {
        this.loggedInUserLocation = loggedInUserLocation;
    }

    public Long getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(Long loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

	public boolean isCompleteClosure() {
		return completeClosure;
	}

	public void setCompleteClosure(boolean completeClosure) {
		this.completeClosure = completeClosure;
	}

    public List<ApplicantTaskDetails> getApplicantTaskDetails() {
        return applicantTaskDetails;
    }

    public void setApplicantTaskDetails(List<ApplicantTaskDetails> applicantTaskDetails) {
        this.applicantTaskDetails = applicantTaskDetails;
    }

	public String getActionTinyUrl() {
		return actionTinyUrl;
	}

	public void setActionTinyUrl(String actionTinyUrl) {
		this.actionTinyUrl = actionTinyUrl;
	}

}
