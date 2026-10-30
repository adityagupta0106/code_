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
@Table(schema = SP_SCHEMA_NAME, name = "application_attr_data")
public class ApplicationAttrData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "base_service_id")
	private Long baseServiceId;

	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "application_id")
	private String applicationId;

	@Column(name = "set_id")
	private Long setId;

	@Column(name = "output_set_id")
	private String outputSetId;

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

	@Column(name = "attr_11")
	private String attr11;
	@Column(name = "attr_11_value")
	private String attr11Value;

	@Column(name = "attr_12")
	private String attr12;
	@Column(name = "attr_12_value")
	private String attr12Value;

	@Column(name = "attr_13")
	private String attr13;
	@Column(name = "attr_13_value")
	private String attr13Value;

	@Column(name = "attr_14")
	private String attr14;
	@Column(name = "attr_14_value")
	private String attr14Value;

	@Column(name = "attr_15")
	private String attr15;
	@Column(name = "attr_15_value")
	private String attr15Value;

	@Column(name = "attr_16")
	private String attr16;
	@Column(name = "attr_16_value")
	private String attr16Value;

	@Column(name = "attr_17")
	private String attr17;
	@Column(name = "attr_17_value")
	private String attr17Value;

	@Column(name = "attr_18")
	private String attr18;
	@Column(name = "attr_18_value")
	private String attr18Value;

	@Column(name = "attr_19")
	private String attr19;
	@Column(name = "attr_19_value")
	private String attr19Value;

	@Column(name = "attr_20")
	private String attr20;
	@Column(name = "attr_20_value")
	private String attr20Value;

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

	public String getOutputSetId() {
		return outputSetId;
	}

	public void setOutputSetId(String outputSetId) {
		this.outputSetId = outputSetId;
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

	public String getAttr11() {
		return attr11;
	}

	public void setAttr11(String attr11) {
		this.attr11 = attr11;
	}

	public String getAttr11Value() {
		return attr11Value;
	}

	public void setAttr11Value(String attr11Value) {
		this.attr11Value = attr11Value;
	}

	public String getAttr12() {
		return attr12;
	}

	public void setAttr12(String attr12) {
		this.attr12 = attr12;
	}

	public String getAttr12Value() {
		return attr12Value;
	}

	public void setAttr12Value(String attr12Value) {
		this.attr12Value = attr12Value;
	}

	public String getAttr13() {
		return attr13;
	}

	public void setAttr13(String attr13) {
		this.attr13 = attr13;
	}

	public String getAttr13Value() {
		return attr13Value;
	}

	public void setAttr13Value(String attr13Value) {
		this.attr13Value = attr13Value;
	}

	public String getAttr14() {
		return attr14;
	}

	public void setAttr14(String attr14) {
		this.attr14 = attr14;
	}

	public String getAttr14Value() {
		return attr14Value;
	}

	public void setAttr14Value(String attr14Value) {
		this.attr14Value = attr14Value;
	}

	public String getAttr15() {
		return attr15;
	}

	public void setAttr15(String attr15) {
		this.attr15 = attr15;
	}

	public String getAttr15Value() {
		return attr15Value;
	}

	public void setAttr15Value(String attr15Value) {
		this.attr15Value = attr15Value;
	}

	public String getAttr16() {
		return attr16;
	}

	public void setAttr16(String attr16) {
		this.attr16 = attr16;
	}

	public String getAttr16Value() {
		return attr16Value;
	}

	public void setAttr16Value(String attr16Value) {
		this.attr16Value = attr16Value;
	}

	public String getAttr17() {
		return attr17;
	}

	public void setAttr17(String attr17) {
		this.attr17 = attr17;
	}

	public String getAttr17Value() {
		return attr17Value;
	}

	public void setAttr17Value(String attr17Value) {
		this.attr17Value = attr17Value;
	}

	public String getAttr18() {
		return attr18;
	}

	public void setAttr18(String attr18) {
		this.attr18 = attr18;
	}

	public String getAttr18Value() {
		return attr18Value;
	}

	public void setAttr18Value(String attr18Value) {
		this.attr18Value = attr18Value;
	}

	public String getAttr19() {
		return attr19;
	}

	public void setAttr19(String attr19) {
		this.attr19 = attr19;
	}

	public String getAttr19Value() {
		return attr19Value;
	}

	public void setAttr19Value(String attr19Value) {
		this.attr19Value = attr19Value;
	}

	public String getAttr20() {
		return attr20;
	}

	public void setAttr20(String attr20) {
		this.attr20 = attr20;
	}

	public String getAttr20Value() {
		return attr20Value;
	}

	public void setAttr20Value(String attr20Value) {
		this.attr20Value = attr20Value;
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