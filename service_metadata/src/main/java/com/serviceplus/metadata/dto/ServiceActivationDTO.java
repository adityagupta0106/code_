package com.serviceplus.metadata.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ServiceActivationDTO {

    @NotNull
    private Integer serviceId;

    @NotNull
    private Integer deliveryUnitLevelId;

    @NotEmpty
    private List<Integer> locationIds;

    @NotNull
    private Integer activationStatus;

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

	public List<Integer> getLocationIds() {
		return locationIds;
	}

	public void setLocationIds(List<Integer> locationIds) {
		this.locationIds = locationIds;
	}

	public Integer getActivationStatus() {
		return activationStatus;
	}

	public void setActivationStatus(Integer activationStatus) {
		this.activationStatus = activationStatus;
	}

}
