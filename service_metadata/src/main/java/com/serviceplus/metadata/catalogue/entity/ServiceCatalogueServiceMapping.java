package com.serviceplus.metadata.catalogue.entity;

import java.time.OffsetDateTime;

import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "service_catalogue_service_mapping", schema = ApplicationConstants.SP_SCHEMA_NAME, uniqueConstraints = {
		@UniqueConstraint(name = "service_catalogue_mapping_service_uk", columnNames = "service_id") })
public class ServiceCatalogueServiceMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "configuration_id", nullable = false)
	private Long configurationId;

	@Column(name = "service_id", nullable = false)
	private Long serviceId;

	@Column(name = "cr_on")
	private OffsetDateTime createdOn;

	@PrePersist
	public void prePersist() {

		if (createdOn == null) {
			createdOn = OffsetDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(Long configurationId) {
		this.configurationId = configurationId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public OffsetDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(OffsetDateTime createdOn) {
		this.createdOn = createdOn;
	}
}
