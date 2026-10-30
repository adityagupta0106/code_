package com.serviceplus.metadata.dto;

import java.util.List;

import com.serviceplus.metadata.dto.OfficeDetailsDTO.OfficeUnitData;

public class ServiceDeliveryUnitMappingDTO {

    private Integer deliveryUnitLevelId;
    private String deliveryUnitLevelName;

    private List<OfficeUnitData> officeUnits;

    private Integer designatedOfficerUserLevelId;
    private String designatedOfficerUserLevelName;

    private Integer userRoleId;
    private String  userRole;

    public Integer getDeliveryUnitLevelId() {
        return deliveryUnitLevelId;
    }

    public void setDeliveryUnitLevelId(Integer deliveryUnitLevelId) {
        this.deliveryUnitLevelId = deliveryUnitLevelId;
    }

    public String getDeliveryUnitLevelName() {
        return deliveryUnitLevelName;
    }

    public void setDeliveryUnitLevelName(String deliveryUnitLevelName) {
        this.deliveryUnitLevelName = deliveryUnitLevelName;
    }

    public List<OfficeUnitData> getOfficeUnits() {
        return officeUnits;
    }

    public void setOfficeUnits(List<OfficeUnitData> officeUnits) {
        this.officeUnits = officeUnits;
    }

    public Integer getDesignatedOfficerUserLevelId() {
        return designatedOfficerUserLevelId;
    }

    public void setDesignatedOfficerUserLevelId(Integer designatedOfficerUserLevelId) {
        this.designatedOfficerUserLevelId = designatedOfficerUserLevelId;
    }

    public String getDesignatedOfficerUserLevelName() {
        return designatedOfficerUserLevelName;
    }

    public void setDesignatedOfficerUserLevelName(String designatedOfficerUserLevelName) {
        this.designatedOfficerUserLevelName = designatedOfficerUserLevelName;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
}
