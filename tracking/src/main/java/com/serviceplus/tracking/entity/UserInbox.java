package com.serviceplus.tracking.entity;

import java.util.Date;

import com.serviceplus.tracking.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_inbox", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class UserInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbox_id", nullable = false)
    private Long inboxId;

    @Column(name = "appl_id")
    private String applId;
    
    @Column(name = "appl_ref_no")
    private String applRefNo;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "task_id")
    private String taskId;
    
    @Column(name = "form_id")
    private String formId;

    @Column(name = "current_process_id")
    private String currentProcessId;

    @Column(name = "user_token")
    private String userToken;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "appl_recieved_on")
    private Date applRecievedOn;

    @Column(name = "last_action_on")
    private Date lastActionOn;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "location_id")
    private Integer locationId;
    
    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "is_priority")
    private Boolean isPriority;

	public Long getInboxId() {
		return inboxId;
	}

	public void setInboxId(Long inboxId) {
		this.inboxId = inboxId;
	}

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

	public Date getLastActionOn() {
		return lastActionOn;
	}

	public void setLastActionOn(Date lastActionOn) {
		this.lastActionOn = lastActionOn;
	}

	public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Boolean getIsPriority() {
		return isPriority;
	}

	public void setIsPriority(Boolean isPriority) {
		this.isPriority = isPriority;
	}
}

