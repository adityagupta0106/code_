package com.serviceplus.tracking.entity;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "task_master", schema = SP_SCHEMA_NAME)
public class TaskMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "task_name")
    private String taskName;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "task_property", columnDefinition = "jsonb")
    private JsonNode taskProperty;

    @Column(name = "tenant_id")
    private String tenantId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }
    
    public JsonNode getTaskProperty() {
		return taskProperty;
	}

	public void setTaskProperty(JsonNode taskProperty) {
		this.taskProperty = taskProperty;
	}

	public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
