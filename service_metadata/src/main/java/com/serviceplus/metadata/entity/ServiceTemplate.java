package com.serviceplus.metadata.entity;

import java.util.Date;

import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_template", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ServiceTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "form_id", nullable = false)
    private String formId;
    
    @Column(name = "holder_id", nullable = false)
    private String holderId;
    
    @Column(name = "task_id", nullable = false)
    private String taskId;
    
    @Column(name = "instantiable_flag")
    private String instantiableFlag;
    
    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "c_date", nullable = false)
    private Date cDate;

    @Column(name = "u_date")
    private Date uDate;

    @Column(name = "base_service_id", nullable = false)
    private Integer baseServiceId;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "minor_version", nullable = false)
    private Integer minorVersion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
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

	public String getInstantiableFlag() {
		return instantiableFlag;
	}

	public void setInstantiableFlag(String instantiableFlag) {
		this.instantiableFlag = instantiableFlag;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Integer getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}

	public String getHolderId() {
		return holderId;
	}

	public void setHolderId(String holderId) {
		this.holderId = holderId;
	}
	
    
    
}