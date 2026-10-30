package com.serviceplus.metadata.correlationConfig.dto;

public class CorrelationPrimaryDTO {
	private Integer setId;
	private String setName;
	private String serviceName;
	private String setType;
	private String clcName;
	
	
	public CorrelationPrimaryDTO() {
		super();
	}
	public CorrelationPrimaryDTO(Integer setId, String setName, String serviceName, String setType, String clcName) {
		super();
		this.setId = setId;
		this.setName = setName;
		this.serviceName = serviceName;
		this.setType = setType;
		this.clcName = clcName;
	}
	public Integer getSetId() {
		return setId;
	}
	public void setSetId(Integer setId) {
		this.setId = setId;
	}
	public String getSetName() {
		return setName;
	}
	public void setSetName(String setName) {
		this.setName = setName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
	}
	public String getClcName() {
		return clcName;
	}
	public void setClcName(String clcName) {
		this.clcName = clcName;
	}
	

}
