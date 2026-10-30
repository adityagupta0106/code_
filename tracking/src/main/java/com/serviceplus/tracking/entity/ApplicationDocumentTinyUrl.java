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
@Table(name = "application_document_tiny_url", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ApplicationDocumentTinyUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false)
    private String applicationId;

    @Column(name = "reference_no")
    private String referenceNo;

    @Column(name = "process_id", nullable = false)
    private String processId;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @Column(name = "tiny_url", nullable = false)
    private String tinyUrl;

    @Column(name = "url_key", nullable = false)
    private String urlKey;

    @Column(name = "access_flag", nullable = false)
    private boolean accessFlag;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "cr_date", nullable = false)
    private OffsetDateTime crDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
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

	public boolean isAccessFlag() {
		return accessFlag;
	}

	public void setAccessFlag(boolean accessFlag) {
		this.accessFlag = accessFlag;
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
