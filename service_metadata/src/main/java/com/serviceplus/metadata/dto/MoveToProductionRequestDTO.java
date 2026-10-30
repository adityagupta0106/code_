package com.serviceplus.metadata.dto;

public class MoveToProductionRequestDTO {

    private Integer serviceId;

    private String signedBase64;
    
    private String key;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getSignedBase64() {
		return signedBase64;
	}

	public void setSignedBase64(String signedBase64) {
		this.signedBase64 = signedBase64;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
    
    
}
