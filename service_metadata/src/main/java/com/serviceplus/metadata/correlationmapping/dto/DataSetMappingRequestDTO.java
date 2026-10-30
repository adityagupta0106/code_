package com.serviceplus.metadata.correlationmapping.dto;

import java.util.List;

public class DataSetMappingRequestDTO {

    private List<String> sources;

    private Integer serviceStatus;

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
}