package com.serviceplus.tracking.correlationapplication.entity;

import static com.serviceplus.tracking.utility.ApplicationConstants.SP_SCHEMA_NAME;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(
    schema = SP_SCHEMA_NAME,
    name = "application_set_order_attr_data"
)
public class ApplicationSetOrderAttrData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "base_service_id", nullable = false)
    private Long baseServiceId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "application_id", nullable = false)
    private String applicationId;

    @Column(name = "set_id", nullable = false)
    private Long setId;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "attr_1")
    private String attr1;

    @Column(name = "attr_1_value")
    private String attr1Value;

    @Column(name = "attr_2")
    private String attr2;

    @Column(name = "attr_2_value")
    private String attr2Value;

    @Column(name = "attr_3")
    private String attr3;

    @Column(name = "attr_3_value")
    private String attr3Value;

    @Column(name = "attr_4")
    private String attr4;

    @Column(name = "attr_4_value")
    private String attr4Value;

    @Column(name = "attr_5")
    private String attr5;

    @Column(name = "attr_5_value")
    private String attr5Value;

    @Column(name = "attr_6")
    private String attr6;

    @Column(name = "attr_6_value")
    private String attr6Value;

    @Column(name = "attr_7")
    private String attr7;

    @Column(name = "attr_7_value")
    private String attr7Value;

    @Column(name = "attr_8")
    private String attr8;

    @Column(name = "attr_8_value")
    private String attr8Value;

    @Column(name = "attr_9")
    private String attr9;

    @Column(name = "attr_9_value")
    private String attr9Value;

    @Column(name = "attr_10")
    private String attr10;

    @Column(name = "attr_10_value")
    private String attr10Value;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "cr_date")
    private LocalDateTime crDate;

    @Column(name = "up_date")
    private LocalDateTime upDate;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (crDate == null) {
            crDate = now;
        }

        if (upDate == null) {
            upDate = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        upDate = LocalDateTime.now();
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

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Long getSetId() {
        return setId;
    }

    public void setSetId(Long setId) {
        this.setId = setId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr1Value() {
        return attr1Value;
    }

    public void setAttr1Value(String attr1Value) {
        this.attr1Value = attr1Value;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }

    public String getAttr2Value() {
        return attr2Value;
    }

    public void setAttr2Value(String attr2Value) {
        this.attr2Value = attr2Value;
    }

    public String getAttr3() {
        return attr3;
    }

    public void setAttr3(String attr3) {
        this.attr3 = attr3;
    }

    public String getAttr3Value() {
        return attr3Value;
    }

    public void setAttr3Value(String attr3Value) {
        this.attr3Value = attr3Value;
    }

    public String getAttr4() {
        return attr4;
    }

    public void setAttr4(String attr4) {
        this.attr4 = attr4;
    }

    public String getAttr4Value() {
        return attr4Value;
    }

    public void setAttr4Value(String attr4Value) {
        this.attr4Value = attr4Value;
    }

    public String getAttr5() {
        return attr5;
    }

    public void setAttr5(String attr5) {
        this.attr5 = attr5;
    }

    public String getAttr5Value() {
        return attr5Value;
    }

    public void setAttr5Value(String attr5Value) {
        this.attr5Value = attr5Value;
    }

    public String getAttr6() {
        return attr6;
    }

    public void setAttr6(String attr6) {
        this.attr6 = attr6;
    }

    public String getAttr6Value() {
        return attr6Value;
    }

    public void setAttr6Value(String attr6Value) {
        this.attr6Value = attr6Value;
    }

    public String getAttr7() {
        return attr7;
    }

    public void setAttr7(String attr7) {
        this.attr7 = attr7;
    }

    public String getAttr7Value() {
        return attr7Value;
    }

    public void setAttr7Value(String attr7Value) {
        this.attr7Value = attr7Value;
    }

    public String getAttr8() {
        return attr8;
    }

    public void setAttr8(String attr8) {
        this.attr8 = attr8;
    }

    public String getAttr8Value() {
        return attr8Value;
    }

    public void setAttr8Value(String attr8Value) {
        this.attr8Value = attr8Value;
    }

    public String getAttr9() {
        return attr9;
    }

    public void setAttr9(String attr9) {
        this.attr9 = attr9;
    }

    public String getAttr9Value() {
        return attr9Value;
    }

    public void setAttr9Value(String attr9Value) {
        this.attr9Value = attr9Value;
    }

    public String getAttr10() {
        return attr10;
    }

    public void setAttr10(String attr10) {
        this.attr10 = attr10;
    }

    public String getAttr10Value() {
        return attr10Value;
    }

    public void setAttr10Value(String attr10Value) {
        this.attr10Value = attr10Value;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDateTime getCrDate() {
        return crDate;
    }

    public void setCrDate(LocalDateTime crDate) {
        this.crDate = crDate;
    }

    public LocalDateTime getUpDate() {
        return upDate;
    }

    public void setUpDate(LocalDateTime upDate) {
        this.upDate = upDate;
    }
}