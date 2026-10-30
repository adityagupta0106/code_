package com.serviceplus.metadata.mvel.dto;

import java.io.Serializable;
import java.util.List;

public class MvelFunctionEvent implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String eventType; 

    private Integer serviceId;

    private Integer versionNo;
    private Integer minorVersionNo;

    private List<FunctionPayload> functions;

    public static class FunctionPayload {
        private Long functionId;
        private String functionName;
        private String functionBody;
        private List<ParameterPayload> parameters;
        
        public static class ParameterPayload {
            private String paramId;
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
		public Long getFunctionId() {
			return functionId;
		}
		public void setFunctionId(Long functionId) {
			this.functionId = functionId;
		}
		public String getFunctionName() {
			return functionName;
		}
		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}
		public String getFunctionBody() {
			return functionBody;
		}
		public void setFunctionBody(String functionBody) {
			this.functionBody = functionBody;
		}
		public List<ParameterPayload> getParameters() {
			return parameters;
		}
		public void setParameters(List<ParameterPayload> parameters) {
			this.parameters = parameters;
		}
		
    }

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Integer getMinorVersionNo() {
		return minorVersionNo;
	}

	public void setMinorVersionNo(Integer minorVersionNo) {
		this.minorVersionNo = minorVersionNo;
	}

	public List<FunctionPayload> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FunctionPayload> functions) {
		this.functions = functions;
	}	
    
}
