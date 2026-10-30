package com.serviceplus.metadata.dto;

public class DesignatedOfficerAssignmentDTO {

    private Integer serviceId;

    private Integer deliveryUnitLevelId;

    private Long userId;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getDeliveryUnitLevelId() {
		return deliveryUnitLevelId;
	}

	public void setDeliveryUnitLevelId(Integer deliveryUnitLevelId) {
		this.deliveryUnitLevelId = deliveryUnitLevelId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
    
    
}
