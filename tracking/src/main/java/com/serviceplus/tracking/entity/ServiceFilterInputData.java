package com.serviceplus.tracking.entity;

import java.time.OffsetDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_filter_input_data", schema = "schm_sp")
public class ServiceFilterInputData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filter_id", nullable = false)
    private Long filterId;

    @Column(name = "base_service_id", nullable = false)
    private Long baseServiceId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "application_id", nullable = false)
    private String applicationId;

    @Column(name = "attr1_value")
    private String attr1Value;

    @Column(name = "attr2_value")
    private String attr2Value;

    @Column(name = "attr3_value")
    private String attr3Value;

    @Column(name = "attr4_value")
    private String attr4Value;

    @Column(name = "attr5_value")
    private String attr5Value;

    @Column(name = "attr6_value")
    private String attr6Value;

    @Column(name = "attr7_value")
    private String attr7Value;

    @Column(name = "attr8_value")
    private String attr8Value;

    @Column(name = "attr9_value")
    private String attr9Value;

    @Column(name = "attr10_value")
    private String attr10Value;

    @Column(name = "cr_date", nullable = false, updatable = false)
    private Date crDate;

    @Column(name = "up_date", nullable = false)
    private Date upDate;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        crDate = now;
        upDate = now;
    }

    @PreUpdate
    protected void onUpdate() {
        upDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFilterId() {
        return filterId;
    }

    public void setFilterId(Long filterId) {
        this.filterId = filterId;
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

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getAttr1Value() {
        return attr1Value;
    }

    public void setAttr1Value(String attr1Value) {
        this.attr1Value = attr1Value;
    }

    public String getAttr2Value() {
        return attr2Value;
    }

    public void setAttr2Value(String attr2Value) {
        this.attr2Value = attr2Value;
    }

    public String getAttr3Value() {
        return attr3Value;
    }

    public void setAttr3Value(String attr3Value) {
        this.attr3Value = attr3Value;
    }

    public String getAttr4Value() {
        return attr4Value;
    }

    public void setAttr4Value(String attr4Value) {
        this.attr4Value = attr4Value;
    }

    public String getAttr5Value() {
        return attr5Value;
    }

    public void setAttr5Value(String attr5Value) {
        this.attr5Value = attr5Value;
    }

    public String getAttr6Value() {
        return attr6Value;
    }

    public void setAttr6Value(String attr6Value) {
        this.attr6Value = attr6Value;
    }

    public String getAttr7Value() {
        return attr7Value;
    }

    public void setAttr7Value(String attr7Value) {
        this.attr7Value = attr7Value;
    }

    public String getAttr8Value() {
        return attr8Value;
    }

    public void setAttr8Value(String attr8Value) {
        this.attr8Value = attr8Value;
    }

    public String getAttr9Value() {
        return attr9Value;
    }

    public void setAttr9Value(String attr9Value) {
        this.attr9Value = attr9Value;
    }

    public String getAttr10Value() {
        return attr10Value;
    }

    public void setAttr10Value(String attr10Value) {
        this.attr10Value = attr10Value;
    }

	public Date getCrDate() {
		return crDate;
	}

	public void setCrDate(Date crDate) {
		this.crDate = crDate;
	}

	public Date getUpDate() {
		return upDate;
	}

	public void setUpDate(Date upDate) {
		this.upDate = upDate;
	}

}