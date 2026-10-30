package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

public class ServiceCombinationConfigurationDTO {

    private Long serviceId;

    private Long baseServiceId;

    private String taskId;

    private Long combinationId;

    private Boolean searchAfterDataFound;
    
    private String tenantId;
    
    private Integer entityId;
    

    public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	private List<CombinationSetDTO> sets = new ArrayList<>();

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getCombinationId() {
        return combinationId;
    }

    public void setCombinationId(Long combinationId) {
        this.combinationId = combinationId;
    }

    public Boolean getSearchAfterDataFound() {
        return searchAfterDataFound;
    }

    public void setSearchAfterDataFound(Boolean searchAfterDataFound) {
        this.searchAfterDataFound = searchAfterDataFound;
    }

    public List<CombinationSetDTO> getSets() {
        return sets;
    }

    public void setSets(List<CombinationSetDTO> sets) {
        this.sets = sets;
    }

    public static class CombinationSetDTO {

        private String sourceType;

        private Long serviceId;

        private Long baseServiceId;

        private Long setId;

        private String outputSetId;

        private Integer priority;

        private List<AttributeMappingDTO> inputAttributes = new ArrayList<>();

        private List<AttributeMappingDTO> outputAttributes = new ArrayList<>();

        private ApplicationStatusDTO applicationStatus;

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

        public Long getSetId() {
            return setId;
        }

        public void setSetId(Long setId) {
            this.setId = setId;
        }

        public String getOutputSetId() {
            return outputSetId;
        }

        public void setOutputSetId(String outputSetId) {
            this.outputSetId = outputSetId;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public List<AttributeMappingDTO> getInputAttributes() {
            return inputAttributes;
        }

        public void setInputAttributes(List<AttributeMappingDTO> inputAttributes) {
            this.inputAttributes = inputAttributes;
        }

        public List<AttributeMappingDTO> getOutputAttributes() {
            return outputAttributes;
        }

        public void setOutputAttributes(List<AttributeMappingDTO> outputAttributes) {
            this.outputAttributes = outputAttributes;
        }

        public ApplicationStatusDTO getApplicationStatus() {
            return applicationStatus;
        }

        public void setApplicationStatus(ApplicationStatusDTO applicationStatus) {
            this.applicationStatus = applicationStatus;
        }
    }

    public static class AttributeMappingDTO {

        private String mappedAttributeId;

        private String attributeId;

        public String getMappedAttributeId() {
            return mappedAttributeId;
        }

        public void setMappedAttributeId(String mappedAttributeId) {
            this.mappedAttributeId = mappedAttributeId;
        }

        public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(String attributeId) {
            this.attributeId = attributeId;
        }
    }

    public static class ApplicationStatusDTO {

        private Integer value;

        private String taskId;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }
    }
}