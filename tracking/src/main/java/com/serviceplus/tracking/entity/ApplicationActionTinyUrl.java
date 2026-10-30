package com.serviceplus.tracking.entity;

import java.time.OffsetDateTime;

import com.serviceplus.tracking.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "application_action_tiny_url", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ApplicationActionTinyUrl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "application_id", nullable = false)
	private String applicationId;
	
	@Column(name= "service_id" ,nullable = false)
	private Integer serviceId;
	
	@Column(name = "application_ref_no")
	private String applicationRefNo;

	@Column(name = "tiny_url", nullable = false)
	private String tinyUrl;

	@Column(name = "url_key", nullable = false)
	private String urlKey;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@Column(name = "cr_date", nullable = false)
	private OffsetDateTime crDate;

	public Long getId() {
		return id;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getApplicationRefNo() {
		return applicationRefNo;
	}

	public void setApplicationRefNo(String applicationRefNo) {
		this.applicationRefNo = applicationRefNo;
	}

	public String getTinyUrl() {
		return tinyUrl;
	}

	public void setTinyUrl(String tinyUrl) {
		this.tinyUrl = tinyUrl;
	}

	public String getUrlKey() {
		return urlKey;
	}

	public void setUrlKey(String urlKey) {
		this.urlKey = urlKey;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public OffsetDateTime getCrDate() {
		return crDate;
	}

	public void setCrDate(OffsetDateTime crDate) {
		this.crDate = crDate;
	}
}
