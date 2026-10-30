package com.serviceplus.formDesigner.request.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ApplicationTemplate implements IApplicationTemplate {

	@Id
	private String formId;
	
	private String holderId;
	private String formName;
	private Object formJsonData;
	private String status;
	private Long createdBy;
	private Integer stateId;
	private Integer departmentId;
	private Integer locationId;
	private Date updateOn;
	private Date createdOn;
	private String instantiable;
	private String encryptionKey;
	private String encryptionKeyAtRest;
	private String tenantId;
	private Integer version;
	private Integer subscriptionCount;
	private Boolean archive;
	
	public String getHolderId() {
		return holderId;
	}
	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Integer getSubscriptionCount() {
		return subscriptionCount;
	}
	public void setSubscriptionCount(Integer subscriptionCount) {
		this.subscriptionCount = subscriptionCount;
	}
	public Boolean getArchive() {
		return archive;
	}
	public void setArchive(Boolean archive) {
		this.archive = archive;
	}
	public Integer getStateId() {
		return stateId;
	}
	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	public String getInstantiable() {
		return instantiable;
	}
	public void setInstantiable(String instantiable) {
		this.instantiable = instantiable;
	}
	public Date getUpdateOn() {
		return updateOn;
	}
	public void setUpdateOn(Date updateOn) {
		this.updateOn = updateOn;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public Object getFormJsonData() {
		return formJsonData;
	}
	public void setFormJsonData(Object formJsonData) {
		this.formJsonData = formJsonData;
	}
	public Integer getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}
	public String getEncryptionKey() {
		return encryptionKey;
	}
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	public String getEncryptionKeyAtRest() {
		return encryptionKeyAtRest;
	}
	public void setEncryptionKeyAtRest(String encryptionKeyAtRest) {
		this.encryptionKeyAtRest = encryptionKeyAtRest;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
}
