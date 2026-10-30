package com.serviceplus.metadata.dto;

public class ServiceListDetailsDTO {
	
	private Integer serviceId;
	private String serviceName;
	private String status;
	private String tablist;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTablist() {
		return tablist;
	}
	public void setTablist(String tablist) {
		this.tablist = tablist;
	}	

}
