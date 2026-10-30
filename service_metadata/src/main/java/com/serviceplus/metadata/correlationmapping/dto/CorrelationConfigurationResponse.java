package com.serviceplus.metadata.correlationmapping.dto;

public class CorrelationConfigurationResponse {

    private Integer id;
    private String setName;
    private String setType;
    private String serviceName;

    public Integer getId() {
        return id;
    }

    public String getSetName() {
        return setName;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}