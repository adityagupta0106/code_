package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

import com.serviceplus.metadata.correlationConfig.dto.CorrelationFieldDTO;
import com.serviceplus.metadata.dto.LabelValue;

import jakarta.validation.Valid;

public class ExternalServiceDataConfigurationDTO {

    @Valid
    private List<CorrelationFieldDTO> attributes = new ArrayList<>();

    @Valid
    private LabelValue combonation;
    
    @Valid
    private List<ExternalServiceSetDTO> sets = new ArrayList<>();

	public List<CorrelationFieldDTO> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<CorrelationFieldDTO> attributes) {
		this.attributes = attributes;
	}

	public LabelValue getCombonation() {
		return combonation;
	}

	public void setCombonation(LabelValue combonation) {
		this.combonation = combonation;
	}

	public List<ExternalServiceSetDTO> getSets() {
		return sets;
	}

	public void setSets(List<ExternalServiceSetDTO> sets) {
		this.sets = sets;
	}

	public static class ExternalServiceSetDTO {

        @Valid
        private LabelValue inputSet;

        @Valid
        private List<AttributeMappingDTO> inputAttributes = new ArrayList<>();

        @Valid
        private LabelValue outputSet;

        @Valid
        private List<AttributeMappingDTO> outputAttributes = new ArrayList<>();

        @Valid
        private ApplicationStatusDTO applicationStatus;

		public LabelValue getInputSet() {
			return inputSet;
		}

		public void setInputSet(LabelValue inputSet) {
			this.inputSet = inputSet;
		}

		public List<AttributeMappingDTO> getInputAttributes() {
			return inputAttributes;
		}

		public void setInputAttributes(List<AttributeMappingDTO> inputAttributes) {
			this.inputAttributes = inputAttributes;
		}

		public LabelValue getOutputSet() {
			return outputSet;
		}

		public void setOutputSet(LabelValue outputSet) {
			this.outputSet = outputSet;
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

        private String mappedAttributeLabel;

        private String attributeId;

        private String attributeLabel;

		public String getMappedAttributeId() {
			return mappedAttributeId;
		}

		public void setMappedAttributeId(String mappedAttributeId) {
			this.mappedAttributeId = mappedAttributeId;
		}

		public String getMappedAttributeLabel() {
			return mappedAttributeLabel;
		}

		public void setMappedAttributeLabel(String mappedAttributeLabel) {
			this.mappedAttributeLabel = mappedAttributeLabel;
		}

		public String getAttributeId() {
			return attributeId;
		}

		public void setAttributeId(String attributeId) {
			this.attributeId = attributeId;
		}

		public String getAttributeLabel() {
			return attributeLabel;
		}

		public void setAttributeLabel(String attributeLabel) {
			this.attributeLabel = attributeLabel;
		}

    }
    
    public static class ApplicationStatusDTO {

        private String label;

        private Integer value;

        private Long serviceId;

        private Long baseServiceId;

        private String taskId;

        private String taskName;

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Integer getValue() {
			return value;
		}

		public void setValue(Integer value) {
			this.value = value;
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

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getTaskName() {
			return taskName;
		}

		public void setTaskName(String taskName) {
			this.taskName = taskName;
		}

    }
}
