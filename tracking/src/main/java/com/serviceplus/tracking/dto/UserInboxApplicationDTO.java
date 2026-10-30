package com.serviceplus.tracking.dto;

import java.util.Date;


public class UserInboxApplicationDTO {
	
    private String applId;
    
    private String applRefNo;

    private Integer serviceId;

    private String taskId;
    
    private String formId;

    private String currentProcessId;

    private String userToken;
    
    private Integer userId;

    private String serviceName;

    private String taskName;

    private Date applRecievedOn;

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

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getCurrentProcessId() {
		return currentProcessId;
	}

	public void setCurrentProcessId(String currentProcessId) {
		this.currentProcessId = currentProcessId;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Date getApplRecievedOn() {
		return applRecievedOn;
	}

	public void setApplRecievedOn(Date applRecievedOn) {
		this.applRecievedOn = applRecievedOn;
	}
}
