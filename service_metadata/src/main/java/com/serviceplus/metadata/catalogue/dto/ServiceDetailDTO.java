package com.serviceplus.metadata.catalogue.dto;

public class ServiceDetailDTO {
	
	private Integer baseServiceId;

    private Integer serviceId;

    private String serviceName;

    private String description;

    private String status;
    
    private Boolean used;
    
    private Long configurationId;
    
    private String configurationName;
    
    
    public Integer getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Integer baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public Long getConfigurationId() {
		return configurationId;
	}

	public void setConfigurationId(Long configurationId) {
		this.configurationId = configurationId;
	}

	public String getConfigurationName() {
		return configurationName;
	}

	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}
    
}
