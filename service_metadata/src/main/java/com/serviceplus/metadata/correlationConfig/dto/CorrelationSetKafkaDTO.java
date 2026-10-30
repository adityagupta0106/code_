package com.serviceplus.metadata.correlationConfig.dto;

import java.util.ArrayList;
import java.util.List;

public class CorrelationSetKafkaDTO {

    private Long serviceId;

    private Long baseServiceId;

    private String tenantId;

    private Integer entityId;

    private List<CorrelationSetDTO> sets = new ArrayList<>();

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Long baseServiceId) {
        this.baseServiceId = baseServiceId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public List<CorrelationSetDTO> getSets() {
        return sets;
    }

    public void setSets(List<CorrelationSetDTO> sets) {
        this.sets = sets;
    }
}