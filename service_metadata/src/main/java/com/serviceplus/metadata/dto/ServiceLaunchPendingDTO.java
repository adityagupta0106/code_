package com.serviceplus.metadata.dto;


public class ServiceLaunchPendingDTO {

    private Integer serviceId;
    private String serviceName;
    private String serviceDescription;
    private String serviceStatus;
    private String launchDocumentHtml;
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
	public String getServiceDescription() {
		return serviceDescription;
	}
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}
	public String getServiceStatus() {
		return serviceStatus;
	}
	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}
	public String getLaunchDocumentHtml() {
		return launchDocumentHtml;
	}
	public void setLaunchDocumentHtml(String launchDocumentHtml) {
		this.launchDocumentHtml = launchDocumentHtml;
	}  
    
}
