package com.serviceplus.metadata.catalogue.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class ServiceCatalogueChatbotRequestDTO {

    private String status;
    
    private List<ServiceDetailDTO> services;
    
    private JsonNode data;
    
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<ServiceDetailDTO> getServices() {
		return services;
	}
	public void setServices(List<ServiceDetailDTO> services) {
		this.services = services;
	}
	public JsonNode getData() {
		return data;
	}
	public void setData(JsonNode data) {
		this.data = data;
	} 
}
