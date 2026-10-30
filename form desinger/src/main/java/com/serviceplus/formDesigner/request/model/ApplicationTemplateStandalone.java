package com.serviceplus.formDesigner.request.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ApplicationTemplateStandalone implements IApplicationTemplate {
	
	@Id
	private String formId;

	private String holderId;
	private String formName;
	private Object formJsonData;
	private String status;
	private Long createdBy;

	private String tenantId;
	private Integer stateId;
	private Integer departmentId;
	private Integer locationId;
	private Date updateOn;
	private Date createdOn;

	private String instantiable;
	private String encryptionKey;
	private String encryptionKeyAtRest;

	private String prodTinyUrl;
	private String configureTinyUrl;
	private String tinyUrlToken;
	private Long clickCount = 0L;
	private Boolean isOnline;

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Object getFormJsonData() {
		return formJsonData;
	}

	public void setFormJsonData(Object formJsonData) {
		this.formJsonData = formJsonData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public Date getUpdateOn() {
		return updateOn;
	}

	public void setUpdateOn(Date updateOn) {
		this.updateOn = updateOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getInstantiable() {
		return instantiable;
	}

	public void setInstantiable(String instantiable) {
		this.instantiable = instantiable;
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

	public String getProdTinyUrl() {
		return prodTinyUrl;
	}

	public void setProdTinyUrl(String prodTinyUrl) {
		this.prodTinyUrl = prodTinyUrl;
	}

	public String getConfigureTinyUrl() {
		return configureTinyUrl;
	}

	public void setConfigureTinyUrl(String configureTinyUrl) {
		this.configureTinyUrl = configureTinyUrl;
	}

	public Long getClickCount() {
		return clickCount;
	}

	public void setClickCount(Long clickCount) {
		this.clickCount = clickCount;
	}

	public String getTinyUrlToken() {
		return tinyUrlToken;
	}

	public void setTinyUrlToken(String tinyUrlToken) {
		this.tinyUrlToken = tinyUrlToken;
	}

	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

}
