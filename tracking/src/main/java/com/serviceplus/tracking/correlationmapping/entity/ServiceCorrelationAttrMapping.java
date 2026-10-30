package com.serviceplus.tracking.correlationmapping.entity;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

import java.util.Date;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="service_correlation_attr_mapping", schema = SP_SCHEMA_NAME)
public class ServiceCorrelationAttrMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long baseServiceId;

    private Long serviceId;

    private String taskId;

    private Long combinationId;

    private Long mappedBaseServiceId;

    private Long mappedServiceId;

    private String mappingType;

    private Long mappedSetId;

    private String outputSetId;

    private String attrId;

    private String mappedAttrId;

    private String tenantId;

    private Integer entityId;

    private Date crDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getCombinationId() {
        return combinationId;
    }

    public void setCombinationId(Long combinationId) {
        this.combinationId = combinationId;
    }

    public Long getMappedBaseServiceId() {
        return mappedBaseServiceId;
    }

    public void setMappedBaseServiceId(Long mappedBaseServiceId) {
        this.mappedBaseServiceId = mappedBaseServiceId;
    }

    public Long getMappedServiceId() {
        return mappedServiceId;
    }

    public void setMappedServiceId(Long mappedServiceId) {
        this.mappedServiceId = mappedServiceId;
    }

    public String getMappingType() {
        return mappingType;
    }

    public void setMappingType(String mappingType) {
        this.mappingType = mappingType;
    }

    public Long getMappedSetId() {
        return mappedSetId;
    }

    public void setMappedSetId(Long mappedSetId) {
        this.mappedSetId = mappedSetId;
    }

    public String getOutputSetId() {
        return outputSetId;
    }

    public void setOutputSetId(String outputSetId) {
        this.outputSetId = outputSetId;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getMappedAttrId() {
        return mappedAttrId;
    }

    public void setMappedAttrId(String mappedAttrId) {
        this.mappedAttrId = mappedAttrId;
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

    public Date getCrDate() {
        return crDate;
    }

    public void setCrDate(Date crDate) {
        this.crDate = crDate;
    }
}