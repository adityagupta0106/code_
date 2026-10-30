package com.serviceplus.metadata.mvel.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_mvel_funtion", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class MvelFunction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
	
	@Column(name = "service_id", nullable = false)
    private Integer serviceId;

	@Column(name="name")
	private String name;

	@Column(name = "mvel_function_json", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private MvelFunctionDTO mvelFunctionJson;
	
	@Column(name="created_on",nullable=false,updatable=false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name="created_by",nullable=false,updatable=false)
	private Long createdBy;

	@Column(name="modified_on")
	@UpdateTimestamp
	private Date modifiedOn;

	@Column(name="modified_by")
	private Long modifiedBy;
	
	@Column(name ="version_no")
	private Integer versionNo;
	
	@Column(name ="minor_version_no")
	private Integer minorVersionNo;
	
	@Column(name ="minor_version_created")
	private String minorVersionCreated;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MvelFunctionDTO getMvelFunctionJson() {
		return mvelFunctionJson;
	}

	public void setMvelFunctionJson(MvelFunctionDTO mvelFunctionJson) {
		this.mvelFunctionJson = mvelFunctionJson;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Integer getMinorVersionNo() {
		return minorVersionNo;
	}

	public void setMinorVersionNo(Integer minorVersionNo) {
		this.minorVersionNo = minorVersionNo;
	}

	public String getMinorVersionCreated() {
		return minorVersionCreated;
	}

	public void setMinorVersionCreated(String minorVersionCreated) {
		this.minorVersionCreated = minorVersionCreated;
	}
	
}
