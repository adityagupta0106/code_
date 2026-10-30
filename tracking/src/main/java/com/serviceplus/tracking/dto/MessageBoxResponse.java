package com.serviceplus.tracking.dto;

import java.util.Date;

public class MessageBoxResponse {

	private Long messageId;
	
    private String applicationId;

    private String applicationRefNo;

    private Integer serviceId;

    private String serviceName;

    private String taskId;

    private String taskName;

    private String associatedTaskId;

    private Date createdOn;

    
	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationRefNo() {
		return applicationRefNo;
	}

	public void setApplicationRefNo(String applicationRefNo) {
		this.applicationRefNo = applicationRefNo;
	}

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

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getAssociatedTaskId() {
		return associatedTaskId;
	}

	public void setAssociatedTaskId(String associatedTaskId) {
		this.associatedTaskId = associatedTaskId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}    
    
}
