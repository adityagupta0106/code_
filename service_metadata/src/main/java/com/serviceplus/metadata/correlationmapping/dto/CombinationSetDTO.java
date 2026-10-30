package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CombinationSetDTO {

    @NotNull(message = "sequence can't be null")
    private Integer sequence;

    @NotBlank(message = "sourceType can't be blank")
    private String sourceType;

    private Long serviceId;

    private Long baseServiceId;

    private String serviceName;

    private Long setId;

    private String setName;

    @Valid
    private List<ExternalDataMappingRequestDTO.ApprovedSetDTO>
            approvedSets = new ArrayList<>();

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

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

	public Long getBaseServiceId() {
		return baseServiceId;
	}

	public void setBaseServiceId(Long baseServiceId) {
		this.baseServiceId = baseServiceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Long getSetId() {
		return setId;
	}

	public void setSetId(Long setId) {
		this.setId = setId;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public List<ExternalDataMappingRequestDTO.ApprovedSetDTO> getApprovedSets() {
		return approvedSets;
	}

	public void setApprovedSets(List<ExternalDataMappingRequestDTO.ApprovedSetDTO> approvedSets) {
		this.approvedSets = approvedSets;
	}

    
}