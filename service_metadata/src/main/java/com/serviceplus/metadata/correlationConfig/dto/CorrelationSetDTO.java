package com.serviceplus.metadata.correlationConfig.dto;

import java.util.ArrayList;
import java.util.List;

import com.serviceplus.metadata.correlationConfig.enums.SortingSequence;

public class CorrelationSetDTO {

    private Long serviceId;

    private Long baseServiceId;

    private Long setId;

    private String sourceType;
    
    private String securityKey;

    private String tenantId;
    private Integer entityId;
    
    private List<CorrelationAttributeDTO> inputAttributes = new ArrayList<>();

    private List<OutputAttributeDTO> outputAttributes = new ArrayList<>();

    private List<SortingAttributeDTO> sortingAttributes = new ArrayList<>();

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

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public List<CorrelationAttributeDTO> getInputAttributes() {
        return inputAttributes;
    }

    public void setInputAttributes(List<CorrelationAttributeDTO> inputAttributes) {
        this.inputAttributes = inputAttributes;
    }

    public List<OutputAttributeDTO> getOutputAttributes() {
        return outputAttributes;
    }

    public void setOutputAttributes(List<OutputAttributeDTO> outputAttributes) {
        this.outputAttributes = outputAttributes;
    }

    public List<SortingAttributeDTO> getSortingAttributes() {
        return sortingAttributes;
    }

    public void setSortingAttributes(List<SortingAttributeDTO> sortingAttributes) {
        this.sortingAttributes = sortingAttributes;
    }

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

	public static class CorrelationAttributeDTO {

        private Integer sortOrder;

        private String attributeId;

        private String taskId;
        
        private String attributeSource;
        
		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(String attributeId) {
            this.attributeId = attributeId;
        }

        public String getAttributeSource() {
            return attributeSource;
        }

        public void setAttributeSource(String attributeSource) {
            this.attributeSource = attributeSource;
        }
    }

    public static class OutputAttributeDTO {

        private List<OutputAttributeItemDTO> attributes = new ArrayList<>();

        private String outputSetid;

        public List<OutputAttributeItemDTO> getAttributes() {
            return attributes;
        }

        public void setAttributes(List<OutputAttributeItemDTO> attributes) {
            this.attributes = attributes;
        }

        public String getOutputSetid() {
            return outputSetid;
        }

        public void setOutputSetid(String outputSetid) {
            this.outputSetid = outputSetid;
        }
    }

    public static class OutputAttributeItemDTO {

        private String attributeId;

        private String attributeSource;

        private String taskId;
        
        public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(String attributeId) {
            this.attributeId = attributeId;
        }

        public String getAttributeSource() {
            return attributeSource;
        }

        public void setAttributeSource(String attributeSource) {
            this.attributeSource = attributeSource;
        }
    }

    public static class SortingAttributeDTO {

        private Integer sortOrder;

        private String attributeId;
        
        private SortingSequence sortSequence;
        
        public SortingSequence getSortSequence() {
			return sortSequence;
		}

		public void setSortSequence(SortingSequence sortSequence) {
			this.sortSequence = sortSequence;
		}

		public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public String getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(String attributeId) {
            this.attributeId = attributeId;
        }
    }
}