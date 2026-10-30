package com.serviceplus.metadata.mvel.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MvelEventMappingInvalidationMSGDTO {
	private String event;
	private TriggerPointDTO triggerPoint;

	public MvelEventMappingInvalidationMSGDTO() {
		this.triggerPoint = new TriggerPointDTO();
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public TriggerPointDTO getTriggerPoint() {
		return triggerPoint;
	}

	public void setTriggerPoint(TriggerPointDTO triggerPoint) {
		this.triggerPoint = triggerPoint;
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class TriggerPointDTO {
		private TriggerFunctionDTO triggerFunction;

		public TriggerPointDTO() {
			this.triggerFunction = new TriggerFunctionDTO();
		}

		public TriggerFunctionDTO getTriggerFunction() {
			return triggerFunction;
		}

		public void setTriggerFunction(TriggerFunctionDTO triggerFunction) {
			this.triggerFunction = triggerFunction;
		}

	}
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class TriggerFunctionDTO {
		private String funcId;
		private String message;
		
		private List<FuncParamDTO> params;

		public String getFuncId() {
			return funcId;
		}

		public void setFuncId(String funcId) {
			this.funcId = funcId;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public List<FuncParamDTO> getParams() {
			return params;
		}

		public void setParams(List<FuncParamDTO> params) {
			this.params = params;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class FuncParamDTO {
		private String paramName;

		public String getParamName() {
			return paramName;
		}

		public void setParamName(String paramName) {
			this.paramName = paramName;
		}
	}

}
