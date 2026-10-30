package com.serviceplus.metadata.dto;

public class LgdSubordinateOrgUnits implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer orgUnitCode;

	private String orgUnitName;

	public Integer getOrgUnitCode() {
		return orgUnitCode;
	}

	public void setOrgUnitCode(Integer orgUnitCode) {
		this.orgUnitCode = orgUnitCode;
	}

	public String getOrgUnitName() {
		return orgUnitName;
	}

	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
