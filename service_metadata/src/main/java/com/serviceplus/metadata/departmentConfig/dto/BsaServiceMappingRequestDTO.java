package com.serviceplus.metadata.departmentConfig.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BsaServiceMappingRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "serviceId is mandatory")
    private Integer serviceId;

    @NotNull(message = "bsaConfigId is mandatory")
    private Long bsaConfigId;

    private Boolean activeStatus = true;

    private List<BsaInOutAttributeMappingDTO> attributeMappings = new ArrayList<>();

    public BsaServiceMappingRequestDTO() {
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Long getBsaConfigId() {
        return bsaConfigId;
    }

    public void setBsaConfigId(Long bsaConfigId) {
        this.bsaConfigId = bsaConfigId;
    }

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public List<BsaInOutAttributeMappingDTO> getAttributeMappings() {
        return attributeMappings;
    }

    public void setAttributeMappings(List<BsaInOutAttributeMappingDTO> attributeMappings) {
        this.attributeMappings = attributeMappings;
    }
}
