package com.serviceplus.metadata.dto;

public class ServiceLaunchResponseDTO {

    private Integer serviceId;
    private Integer serviceStatus;
    private String launchFlag;
    private String pdfBase64;
    private String message;
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public Integer getServiceStatus() {
		return serviceStatus;
	}
	public void setServiceStatus(Integer serviceStatus) {
		this.serviceStatus = serviceStatus;
	}
	public String getLaunchFlag() {
		return launchFlag;
	}
	public void setLaunchFlag(String launchFlag) {
		this.launchFlag = launchFlag;
	}
	public String getPdfBase64() {
		return pdfBase64;
	}
	public void setPdfBase64(String pdfBase64) {
		this.pdfBase64 = pdfBase64;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ServiceLaunchResponseDTO(Integer serviceId,String pdfBase64,
			String message) {
		this.serviceId = serviceId;
		this.pdfBase64 = pdfBase64;
		this.message = message;
	}
	
	public ServiceLaunchResponseDTO(Integer serviceId, Integer serviceStatus,String message) {
		this.serviceId = serviceId;
		this.serviceStatus = serviceStatus;
		this.message = message;
	}
	
    
    
}
