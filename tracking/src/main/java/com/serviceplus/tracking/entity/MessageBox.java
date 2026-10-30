package com.serviceplus.tracking.entity;

import java.util.Date;

import com.serviceplus.tracking.utility.ApplicationConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="message_box",schema=ApplicationConstants.SP_SCHEMA_NAME)
public class MessageBox {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long messageId;

    private String applicationId;

    private String applRefNo;

    private Integer serviceId;

    private Integer baseServiceId;

    private String processId;

    private String associatedTaskId;

    private String currentTaskId;

    private String currentTaskName;

    private String serviceName;

    private String holderId;

    private Integer locationId;

    private Boolean allowApplicationView;

    private Boolean allowHistoryView;

    private Boolean isRead;

    private Date readOn;

    private Boolean autoClear;

    private Boolean cleared;

    private Date clearedOn;

    private Date createdOn;

    private String tenantId;

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
		return applRefNo;
	}

	public void setApplicationRefNo(String applicationRefNo) {
		this.applRefNo = applicationRefNo;
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

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getAssociatedTaskId() {
		return associatedTaskId;
	}

	public void setAssociatedTaskId(String associatedTaskId) {
		this.associatedTaskId = associatedTaskId;
	}

	public String getCurrentTaskId() {
		return currentTaskId;
	}

	public void setCurrentTaskId(String currentTaskId) {
		this.currentTaskId = currentTaskId;
	}

	public String getCurrentTaskName() {
		return currentTaskName;
	}

	public void setCurrentTaskName(String currentTaskName) {
		this.currentTaskName = currentTaskName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public Boolean getAllowApplicationView() {
		return allowApplicationView;
	}

	public void setAllowApplicationView(Boolean allowApplicationView) {
		this.allowApplicationView = allowApplicationView;
	}

	public Boolean getAllowHistoryView() {
		return allowHistoryView;
	}

	public void setAllowHistoryView(Boolean allowHistoryView) {
		this.allowHistoryView = allowHistoryView;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public Date getReadOn() {
		return readOn;
	}

	public void setReadOn(Date readOn) {
		this.readOn = readOn;
	}

	public Boolean getAutoClear() {
		return autoClear;
	}

	public void setAutoClear(Boolean autoClear) {
		this.autoClear = autoClear;
	}

	public Boolean getCleared() {
		return cleared;
	}

	public void setCleared(Boolean cleared) {
		this.cleared = cleared;
	}

	public Date getClearedOn() {
		return clearedOn;
	}

	public void setClearedOn(Date clearedOn) {
		this.clearedOn = clearedOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
    
}
