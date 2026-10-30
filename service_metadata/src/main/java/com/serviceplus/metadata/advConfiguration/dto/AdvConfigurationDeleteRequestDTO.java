package com.serviceplus.metadata.advConfiguration.dto;

import java.io.Serializable;

public class AdvConfigurationDeleteRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long advConfigId;
    private Integer serviceId;
    private Long userId;

    public AdvConfigurationDeleteRequestDTO() {
    }

    public AdvConfigurationDeleteRequestDTO(Long advConfigId, Integer serviceId, Long userId) {
        this.advConfigId = advConfigId;
        this.serviceId = serviceId;
        this.userId = userId;
    }

    public Long getAdvConfigId() {
        return advConfigId;
    }

    public void setAdvConfigId(Long advConfigId) {
        this.advConfigId = advConfigId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
