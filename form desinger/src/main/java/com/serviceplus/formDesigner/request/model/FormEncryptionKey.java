package com.serviceplus.formDesigner.request.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FormEncryptionKey {

	@Id
	private String id;
	private String formId;
	private String taskId;
	private Integer serviceId;
	private Boolean transitEncFlag;
	private String restEncKey;
	private Date cdate;
	private Date udate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Boolean getTransitEncFlag() {
		return transitEncFlag;
	}

	public void setTransitEncFlag(Boolean transitEncFlag) {
		this.transitEncFlag = transitEncFlag;
	}

	public String getRestEncKey() {
		return restEncKey;
	}

	public void setRestEncKey(String restEncKey) {
		this.restEncKey = restEncKey;
	}

	public Date getCdate() {
		return cdate;
	}

	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}

	public Date getUdate() {
		return udate;
	}

	public void setUdate(Date udate) {
		this.udate = udate;
	}

}
