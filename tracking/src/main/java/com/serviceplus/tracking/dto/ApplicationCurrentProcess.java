package com.serviceplus.tracking.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicationCurrentProcess {

    private String processId;

    private Integer serviceId;
    
    private Integer baseServiceId;
    
    private String formId;

    private String currentTask;

    private String currentTaskName;

    private String previousTask;

    private String previousTaskName;

    private String previousProcessId;

    private Integer actionCode;

    private String actionTaken;

    private String isParallel;

    private LocalDateTime actionOn;

    private LocalDateTime initiatedOn;

    private Long userId;

    private String userIp;

    private String tenantId;

    private String applicationId;

    private String dataId;

    private Boolean gateway;

    private Boolean applicantTask;

    private String actionName;
    
    private Boolean isPriority;

    private List<TrackingDocument> documents;
    
    private String actionRemarks;

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public String getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(String currentTask) {
		this.currentTask = currentTask;
	}

	public String getCurrentTaskName() {
		return currentTaskName;
	}

	public void setCurrentTaskName(String currentTaskName) {
		this.currentTaskName = currentTaskName;
	}

	public String getPreviousTask() {
		return previousTask;
	}

	public void setPreviousTask(String previousTask) {
		this.previousTask = previousTask;
	}

	public String getPreviousTaskName() {
		return previousTaskName;
	}

	public void setPreviousTaskName(String previousTaskName) {
		this.previousTaskName = previousTaskName;
	}

	public String getPreviousProcessId() {
		return previousProcessId;
	}

	public void setPreviousProcessId(String previousProcessId) {
		this.previousProcessId = previousProcessId;
	}

	public Integer getActionCode() {
		return actionCode;
	}

	public void setActionCode(Integer actionCode) {
		this.actionCode = actionCode;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public String getIsParallel() {
		return isParallel;
	}

	public void setIsParallel(String isParallel) {
		this.isParallel = isParallel;
	}

	public LocalDateTime getActionOn() {
		return actionOn;
	}

	public void setActionOn(LocalDateTime actionOn) {
		this.actionOn = actionOn;
	}

	public LocalDateTime getInitiatedOn() {
		return initiatedOn;
	}

	public void setInitiatedOn(LocalDateTime initiatedOn) {
		this.initiatedOn = initiatedOn;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

    public Boolean getGateway() {
        return gateway;
    }

    public void setGateway(Boolean gateway) {
        this.gateway = gateway;
    }

    @Override
    public String toString() {
        return "ApplicationCurrentProcess{" +
                "processId='" + processId + '\'' +
                ", serviceId=" + serviceId +
                ", baseServiceId=" + baseServiceId +
                ", formId='" + formId + '\'' +
                ", currentTask='" + currentTask + '\'' +
                ", currentTaskName='" + currentTaskName + '\'' +
                ", previousTask='" + previousTask + '\'' +
                ", previousTaskName='" + previousTaskName + '\'' +
                ", previousProcessId='" + previousProcessId + '\'' +
                ", actionCode=" + actionCode +
                ", actionTaken='" + actionTaken + '\'' +
                ", isParallel='" + isParallel + '\'' +
                ", actionOn=" + actionOn +
                ", initiatedOn=" + initiatedOn +
                ", userId=" + userId +
                ", userIp='" + userIp + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", dataId='" + dataId + '\'' +
                ", gateway=" + gateway +
                '}';
    }

    public Boolean getApplicantTask() {
        return applicantTask;
    }

    public void setApplicantTask(Boolean applicantTask) {
        this.applicantTask = applicantTask;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Boolean getIsPriority() {
		return isPriority;
	}

	public void setIsPriority(Boolean isPriority) {
		this.isPriority = isPriority;
	}

	public List<TrackingDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<TrackingDocument> documents) {
        this.documents = documents;
    }
    
    public String getActionRemarks() {
        return actionRemarks;
    }

    public void setActionRemarks(String actionRemarks) {
        this.actionRemarks = actionRemarks;
    }
}
