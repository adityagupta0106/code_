package com.serviceplus.tracking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

@Entity
@Table(name = "service_master", schema = SP_SCHEMA_NAME)
public class ServiceMaster {

    @Id
    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "tenant_id")
    private String tenantId;

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
