package com.serviceplus.metadata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLJsonPGObjectJsonType;

import com.serviceplus.metadata.dto.ServiceTrackingConfigDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tracking_configuration", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ServiceTrackingConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "base_service_id", nullable = false)
    private Long baseServiceId;

    @Column(name = "cr_by", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "cr_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "up_by")
    private Long updatedBy;

    @Column(name = "up_date")
    private LocalDateTime updatedDate;

    // PostgreSQL's default Hibernate JSON descriptor binds jsonb; this column requires json.
    @Column(name = "tracking_conf_json", nullable = false, columnDefinition = "json")
    @JdbcType(PostgreSQLJsonPGObjectJsonType.class)
    private ServiceTrackingConfigDTO trackingConfigJson;

    public ServiceTrackingConfig() {
    }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public Long getBaseServiceId() { return baseServiceId; }
    public void setBaseServiceId(Long baseServiceId) { this.baseServiceId = baseServiceId; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    public ServiceTrackingConfigDTO getTrackingConfigJson() { return trackingConfigJson; }
    public void setTrackingConfigJson(ServiceTrackingConfigDTO trackingConfigJson) {
        this.trackingConfigJson = trackingConfigJson;
    }
}