package com.serviceplus.metadata.formSubscription.entity;

import java.util.Date;

import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "form_publish_log", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class FormPublishLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "service_id", nullable = false)
    private Integer serviceId;
    
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "task_id", nullable = false)
    private String taskId;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "holder_id", nullable = false)
    private String holderId;

    @Column(name = "form_id", nullable = false)
    private String formId;

    @Column(name = "publish_form_id", nullable = false)
    private String publishFormId;

    @Column(name = "version", nullable = false)
    private Integer publishVersion;

    @Column(name = "mode", nullable = false)
    private String mode;

    @Column(name = "apply", nullable = false)
    private Boolean apply;

    @Column(name = "c_date", nullable = false)
    private Date cDate;
    
    @Column(name = "u_date", nullable = false)
    private Date uDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getPublishFormId() {
		return publishFormId;
	}

	public void setPublishFormId(String publishFormId) {
		this.publishFormId = publishFormId;
	}

	public Integer getPublishVersion() {
		return publishVersion;
	}

	public void setPublishVersion(Integer publishVersion) {
		this.publishVersion = publishVersion;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Boolean getApply() {
		return apply;
	}

	public void setApply(Boolean apply) {
		this.apply = apply;
	}

	public Date getcDate() {
		return cDate;
	}

	public void setcDate(Date cDate) {
		this.cDate = cDate;
	}

	public Date getuDate() {
		return uDate;
	}

	public void setuDate(Date uDate) {
		this.uDate = uDate;
	}


}