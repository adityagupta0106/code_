package com.serviceplus.formDesigner.formconfig.attrtypemaster.document;

import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fieldTypeMaster")
public class FieldTypeMaster {
	@Id
	private String id;

	private Map<String, Object> groupsJsonData;
	private String tenantId;
	private String createdBy;
	@CreatedDate
	private String createdon;
	@LastModifiedDate
	private String updatedOn;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getGroupsJsonData() {
		return groupsJsonData;
	}

	public void setGroupsJsonData(Map<String, Object> groupsJsonData) {
		this.groupsJsonData = groupsJsonData;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedon() {
		return createdon;
	}

	public void setCreatedon(String createdon) {
		this.createdon = createdon;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

}
