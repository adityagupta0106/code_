package com.serviceplus.metadata.dto;


public class ServiceLaunchRequestDTO {

    private Integer serviceId;
    private Boolean disclaimerAccepted;
    
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public Boolean getDisclaimerAccepted() {
		return disclaimerAccepted;
	}
	public void setDisclaimerAccepted(Boolean disclaimerAccepted) {
		this.disclaimerAccepted = disclaimerAccepted;
	}
    
    
}
