package com.serviceplus.metadata.dto;

public class AssignedServiceDTO {

    private Integer serviceId;

    private String serviceName;

    private String serviceAbbrevation;
    
    private String status;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceAbbrevation() {
		return serviceAbbrevation;
	}

	public void setServiceAbbrevation(String serviceAbbrevation) {
		this.serviceAbbrevation = serviceAbbrevation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
}
