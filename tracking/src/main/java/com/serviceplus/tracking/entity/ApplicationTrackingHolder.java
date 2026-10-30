package com.serviceplus.tracking.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "application_tracking_holder", schema = SP_SCHEMA_NAME)
public class ApplicationTrackingHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "process_id", nullable = false)
    private String processId;

    @Column(name = "holder_id", nullable = false)
    private String holderId;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "created_on", insertable = false, updatable = false)
    private OffsetDateTime createdOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
