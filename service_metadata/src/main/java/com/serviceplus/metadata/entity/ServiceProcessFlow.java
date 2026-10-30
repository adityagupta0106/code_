package com.serviceplus.metadata.entity;

import java.util.Date;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.serviceplus.metadata.dto.ProcessFlowDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_process_flow", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ServiceProcessFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "process_flow_json", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private ProcessFlowDTO processFlowJson;
    
    @Column(name = "draft_process_flow", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode processFlowObject;

    @Column(name = "clc_id", nullable = false)
    private Integer stateId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "minor_version_no", nullable = false)
    private Integer minorVersionNo;

    @Column(name = "c_date", nullable = false)
    private Date cDate;

    @Column(name = "u_date")
    private Date uDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public ProcessFlowDTO getProcessFlowJson() {
		return processFlowJson;
	}

	public void setProcessFlowJson(ProcessFlowDTO processFlowJson) {
		this.processFlowJson = processFlowJson;
	}

	public JsonNode getProcessFlowObject() {
		return processFlowObject;
	}

	public void setProcessFlowObject(JsonNode processFlowObject) {
		this.processFlowObject = processFlowObject;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Integer getMinorVersionNo() {
		return minorVersionNo;
	}

	public void setMinorVersionNo(Integer minorVersionNo) {
		this.minorVersionNo = minorVersionNo;
	}

	public Date getcDate() {
		return cDate;
	}

	public void setcDate(Date cDate) {
		this.cDate = cDate;
	}

	public Date getuDate() {
		return uDate;
	}

	public void setuDate(Date uDate) {
		this.uDate = uDate;
	}
}
