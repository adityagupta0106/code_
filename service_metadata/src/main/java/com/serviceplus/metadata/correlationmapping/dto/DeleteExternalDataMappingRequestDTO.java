package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class DeleteExternalDataMappingRequestDTO {

    @NotNull(message = "id can't be null")
    private Integer id;

    @NotNull(message = "data can't be null")
    @NotEmpty(message = "data can't be empty")
    @Valid
    private List<DeleteDataDTO> data = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<DeleteDataDTO> getData() {
        return data;
    }

    public void setData(List<DeleteDataDTO> data) {
        this.data = data;
    }

    public static class DeleteDataDTO {

        @NotNull(message = "sourceType can't be null")
        private String sourceType;

        private Long serviceId;

        private Long setId;

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public Long getServiceId() {
            return serviceId;
        }

        public void setServiceId(Long serviceId) {
            this.serviceId = serviceId;
        }

        public Long getSetId() {
            return setId;
        }

        public void setSetId(Long setId) {
            this.setId = setId;
        }
    }
}