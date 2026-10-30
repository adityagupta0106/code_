package com.serviceplus.metadata.entity;

import java.util.Date;

import com.serviceplus.metadata.utility.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_log", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class ServiceLog {

    @Id
    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "base_service_id", nullable = false)
    private Integer baseServiceId;

    @Column(name = "service_status", nullable = false)
    private Integer serviceStatus;

    @Column(name = "tab_list", nullable = false, length = 100)
    private String tablist;

    @Column(name = "launch_flag", nullable = false, length = 1)
    private Character launchFlag;

    @Column(name = "launched_by")
    private Long launchedBy;

    @Column(name = "launch_approved_by")
    private Long launchApprovedBy;

    @Column(name = "launched_date")
    private Date launchedDate;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "minor_version_no", nullable = false)
    private Integer minorVersionNo;

    @Column(name = "c_date")
    private Date cDate;

    @Column(name = "u_date")
    private Date uDate;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(name = "launch_doc_file_id", nullable = false)
    private String launchDocFileId;

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

	public Integer getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(Integer serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getTablist() {
		return tablist;
	}

	public void setTablist(String tablist) {
		this.tablist = tablist;
	}

	public Character getLaunchFlag() {
		return launchFlag;
	}

	public void setLaunchFlag(Character launchFlag) {
		this.launchFlag = launchFlag;
	}

	public Long getLaunchedBy() {
		return launchedBy;
	}

	public void setLaunchedBy(Long launchedBy) {
		this.launchedBy = launchedBy;
	}

	public Long getLaunchApprovedBy() {
		return launchApprovedBy;
	}

	public void setLaunchApprovedBy(Long launchApprovedBy) {
		this.launchApprovedBy = launchApprovedBy;
	}

	public Date getLaunchedDate() {
		return launchedDate;
	}

	public void setLaunchedDate(Date launchedDate) {
		this.launchedDate = launchedDate;
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

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getLaunchDocFileId() {
		return launchDocFileId;
	}

	public void setLaunchDocFileId(String launchDocFileId) {
		this.launchDocFileId = launchDocFileId;
	}
    
}