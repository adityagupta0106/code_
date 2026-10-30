package com.serviceplus.metadata.dto;

public class ServiceDeliveryUnitResponseDTO {

	private Integer roleId;
	private String userRole;
    private Integer deliveryUnitLevelId;
    private String deliveryUnitLevelName;

    private Integer designatedOfficerUserLevelId;
    private String designatedOfficerUserLevelName;

    private Long assignedUserId;
    private Integer assignedDesignationId;
    private String designationName;
    private String assignedUserName;
    
    public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
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
	public Long getAssignedUserId() {
		return assignedUserId;
	}
	public void setAssignedUserId(Long assignedUserId) {
		this.assignedUserId = assignedUserId;
	}
	public Integer getAssignedDesignationId() {
		return assignedDesignationId;
	}
	public void setAssignedDesignationId(Integer assignedDesignationId) {
		this.assignedDesignationId = assignedDesignationId;
	}
	public String getDesignationName() {
		return designationName;
	}
	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}
	public String getAssignedUserName() {
		return assignedUserName;
	}
	public void setAssignedUserName(String assignedUserName) {
		this.assignedUserName = assignedUserName;
	}

    
}
