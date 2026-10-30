package com.serviceplus.metadata.mvel.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MvelFunctionDTO {

    @NotNull(message = "Service ID must not be null")
    private Long serviceId;

    private Long id;

    @NotBlank(message = "Function name must not be blank")
    @Size(min = 3, max = 50, message = "Function name must be between 3 and 50 characters")
    private String functionName;

    @NotBlank(message = "Function description must not be blank")
    @Size(min = 3, max = 500, message = "Function description must be between 3 and 500 characters")
    private String functionDescription;

    @NotBlank(message = "Function body must not be blank")
    private String functionBody;

    @Valid
    private List<FunctionParameter> parameters;

    @Valid
    private TriggerPoint triggerPoint;

    public static class FunctionParameter {

        @NotBlank(message = "Parameter ID must not be blank")
        private String paramId;

        @NotBlank(message = "Parameter type must not be blank")
        private String paramType;

        public String getParamId() {
            return paramId;
        }

        public void setParamId(String paramId) {
            this.paramId = paramId;
        }

        public String getParamType() {
            return paramType;
        }

        public void setParamType(String paramType) {
            this.paramType = paramType;
        }
    }

    public static class TriggerPoint {

        @NotBlank(message = "Label must not be blank")
        private String label;

        @NotBlank(message = "Value must not be blank")
        private String value;
        
        @NotBlank(message="Mapped node is required")
        private String nodeId;
        
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

		public String getNodeId() {
			return nodeId;
		}

		public void setNodeId(String nodeId) {
			this.nodeId = nodeId;
		}
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionDescription() {
        return functionDescription;
    }

    public void setFunctionDescription(String functionDescription) {
        this.functionDescription = functionDescription;
    }

    public String getFunctionBody() {
        return functionBody;
    }

    public void setFunctionBody(String functionBody) {
        this.functionBody = functionBody;
    }

    public List<FunctionParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<FunctionParameter> parameters) {
        this.parameters = parameters;
    }

    public TriggerPoint getTriggerPoint() {
        return triggerPoint;
    }

    public void setTriggerPoint(TriggerPoint triggerPoint) {
        this.triggerPoint = triggerPoint;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MvelFunctionDTO historyFunction;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MvelFunctionDTO oldFunction;

    public MvelFunctionDTO getHistoryFunction() {
        return historyFunction;
    }

    public void setHistoryFunction(MvelFunctionDTO historyFunction) {
        this.historyFunction = historyFunction;
        if (this.oldFunction == null) {
            this.oldFunction = historyFunction;
        }
    }

    public MvelFunctionDTO getOldFunction() {
        return oldFunction;
    }

    public void setOldFunction(MvelFunctionDTO oldFunction) {
        this.oldFunction = oldFunction;
        if (this.historyFunction == null) {
            this.historyFunction = oldFunction;
        }
    }
}