package com.serviceplus.metadata.entity;


import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serviceplus.metadata.dto.ServiceDefinitionDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "service_definition_history", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ServiceDefinitionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", nullable = false)
    private Long historyId;

    @Column(name = "modification_id", nullable = false)
    private Long modificationId;

    @Column(name = "archived_at", nullable = false)
    private Date archivedAt;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id", nullable = false)
    private Integer baseServiceId;
    
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "service_abbreviation", nullable = false)
    private String serviceAbbreviation;
    
    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "definition_json", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private ServiceDefinitionDTO definitionJson;
    
    @Column(name = "clc_id", nullable = false)
    private Integer stateId;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

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
		
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceAbbreviation() {
		return serviceAbbreviation;
	}

	public void setServiceAbbreviation(String serviceAbbreviation) {
		this.serviceAbbreviation = serviceAbbreviation;
	}

	public ServiceDefinitionDTO getDefinitionJson() {
		return definitionJson;
	}

	public void setDefinitionJson(ServiceDefinitionDTO definitionJson) {
		this.definitionJson = definitionJson;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
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
    
    

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getModificationId() {
        return modificationId;
    }

    public void setModificationId(Long modificationId) {
        this.modificationId = modificationId;
    }

    public Date getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Date archivedAt) {
        this.archivedAt = archivedAt;
    }

	public String getLogoPath() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

}
