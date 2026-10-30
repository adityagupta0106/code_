package com.serviceplus.tracking.correlationmapping.entity;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="service_correlation_set", schema = SP_SCHEMA_NAME)
public class ServiceCorrelationSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;

    @Column(name ="base_service_id")
    private Long baseServiceId;

    @Column(name ="service_id")
    private Long serviceId;

    @Column(name ="set_id")
    private Long setId;

    @Column(name ="source_type")
    private String sourceType;

    @Column(name ="skey")
    private String skey;

    @Column(name ="attr_id")
    private String attrId;
    
    @Column(name ="task_id")
    private String taskId;

    @Column(name ="attr_source")
    private String attrSource;

    @Column(name ="attr_sort_order")
    private Integer attrSortOrder;

    @Column(name ="set_type")
    private String setType;

    @Column(name ="output_set_id")
    private String outputSetId;

    @Column(name ="tenant_id")
    private String tenantId;

    @Column(name ="entity_id")
    private Integer entityId;

    @Column(name ="cr_date")
    private OffsetDateTime crDate;


	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

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

	public Long getSetId() {
		return setId;
	}

	public void setSetId(Long setId) {
		this.setId = setId;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSkey() {
		return skey;
	}

	public void setSkey(String skey) {
		this.skey = skey;
	}

	public String getAttrId() {
		return attrId;
	}

	public void setAttrId(String attrId) {
		this.attrId = attrId;
	}

	public String getAttrSource() {
		return attrSource;
	}

	public void setAttrSource(String attrSource) {
		this.attrSource = attrSource;
	}

	public Integer getAttrSortOrder() {
		return attrSortOrder;
	}

	public void setAttrSortOrder(Integer attrSortOrder) {
		this.attrSortOrder = attrSortOrder;
	}

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public String getOutputSetId() {
		return outputSetId;
	}

	public void setOutputSetId(String outputSetId) {
		this.outputSetId = outputSetId;
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

	public OffsetDateTime getCrDate() {
		return crDate;
	}

	public void setCrDate(OffsetDateTime crDate) {
		this.crDate = crDate;
	}
}