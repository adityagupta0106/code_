package com.serviceplus.metadata.entity;

import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.serviceplus.metadata.dto.ServiceChargeDTO;
import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_charge_detail", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ServiceChargeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "charge_detail_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private ServiceChargeDTO serviceChargeJson; 

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "c_date", nullable = false)
    private Date cDate;

    @Column(name = "u_date")
    private Date uDate;

    @Column(name = "base_service_id")
    private Integer baseServiceId;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "minor_version")
    private Integer minorVersion;
    
    

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
    
    public ServiceChargeDTO getServiceChargeJson() {
		return serviceChargeJson;
	}

	public void setServiceChargeJson(ServiceChargeDTO serviceChargeJson) {
		this.serviceChargeJson = serviceChargeJson;
	}

	public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCDate() {
        return cDate;
    }

    public void setCDate(Date cDate) {
        this.cDate = cDate;
    }

    public Date getUDate() {
        return uDate;
    }

    public void setUDate(Date uDate) {
        this.uDate = uDate;
    }

    public Integer getBaseServiceId() {
        return baseServiceId;
    }

    public void setBaseServiceId(Integer baseServiceId) {
        this.baseServiceId = baseServiceId;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }
}
