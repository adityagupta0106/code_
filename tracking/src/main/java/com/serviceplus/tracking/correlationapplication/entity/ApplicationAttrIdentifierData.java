package com.serviceplus.tracking.correlationapplication.entity;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(schema = SP_SCHEMA_NAME, name = "application_attr_identifier_data")
@IdClass(ApplicationAttrIdentifierData.ApplicationAttrIdentifierDataId.class)
public class ApplicationAttrIdentifierData {

	@Id
	@Column(name = "application_id")
	private String applicationId;

	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "base_service_id")
	private Long baseServiceId;

	@Id
	@Column(name = "set_id")
	private Long setId;

	@Column(name = "attr_hash")
	private Long attrHash;

	@Column(name = "attr_1_value")
	private String attr1Value;

	@Column(name = "attr_2_value")
	private String attr2Value;

	@Column(name = "attr_3_value")
	private String attr3Value;

	@Column(name = "attr_4_value")
	private String attr4Value;

	@Column(name = "attr_5_value")
	private String attr5Value;

	@Column(name = "tenant_id")
	private String tenantId;

	@Column(name = "cr_date")
	private LocalDateTime crDate;

	@Column(name = "up_date")
	private LocalDateTime upDate;

	@PrePersist
	protected void onCreate() {
	    LocalDateTime now = LocalDateTime.now();

	    if (crDate == null) {
	        crDate = now;
	    }

	    if (upDate == null) {
	        upDate = now;
	    }
	}

	@PreUpdate
	protected void onUpdate() {
	    upDate = LocalDateTime.now();
	}
	
	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public Long getSetId() {
		return setId;
	}

	public void setSetId(Long setId) {
		this.setId = setId;
	}

	public Long getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Long baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getAttrHash() {
		return attrHash;
	}

	public void setAttrHash(Long attrHash) {
		this.attrHash = attrHash;
	}

	public String getAttr1Value() {
		return attr1Value;
	}

	public void setAttr1Value(String attr1Value) {
		this.attr1Value = attr1Value;
	}

	public String getAttr2Value() {
		return attr2Value;
	}

	public void setAttr2Value(String attr2Value) {
		this.attr2Value = attr2Value;
	}

	public String getAttr3Value() {
		return attr3Value;
	}

	public void setAttr3Value(String attr3Value) {
		this.attr3Value = attr3Value;
	}

	public String getAttr4Value() {
		return attr4Value;
	}

	public void setAttr4Value(String attr4Value) {
		this.attr4Value = attr4Value;
	}

	public String getAttr5Value() {
		return attr5Value;
	}

	public void setAttr5Value(String attr5Value) {
		this.attr5Value = attr5Value;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public LocalDateTime getCrDate() {
		return crDate;
	}

	public void setCrDate(LocalDateTime crDate) {
		this.crDate = crDate;
	}

	public LocalDateTime getUpDate() {
		return upDate;
	}

	public void setUpDate(LocalDateTime upDate) {
		this.upDate = upDate;
	}

	public static class ApplicationAttrIdentifierDataId implements Serializable {

		private static final long serialVersionUID = 1L;

		private String applicationId;
		private Long setId;

		public ApplicationAttrIdentifierDataId() {
		}

		public ApplicationAttrIdentifierDataId(String applicationId, Long setId) {
			this.applicationId = applicationId;
			this.setId = setId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof ApplicationAttrIdentifierDataId)) {
				return false;
			}
			ApplicationAttrIdentifierDataId that = (ApplicationAttrIdentifierDataId) o;
			return Objects.equals(applicationId, that.applicationId) && Objects.equals(setId, that.setId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(applicationId, setId);
		}
	}

}