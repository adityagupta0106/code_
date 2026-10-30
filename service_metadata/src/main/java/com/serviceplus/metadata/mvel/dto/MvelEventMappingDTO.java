package com.serviceplus.metadata.mvel.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MvelEventMappingDTO {
	private Long eventId;

	@NotNull(message = "Service ID must not be null")
	private Long serviceId;

	@NotBlank(message = "Event must not be blank")
	private String event;

	@NotNull(message = "Trigger point must not be null")
	private TriggerPointDTO triggerPoint;

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
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

	public static class TriggerPointDTO {
		private String templId;
		private String taskId;

		@NotNull(message = "Trigger function must not be null")
		private TriggerFunctionDTO triggerFunction;

		public String getTemplId() {
			return templId;
		}

		public void setTemplId(String templId) {
			this.templId = templId;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public TriggerFunctionDTO getTriggerFunction() {
			return triggerFunction;
		}

		public void setTriggerFunction(TriggerFunctionDTO triggerFunction) {
			this.triggerFunction = triggerFunction;
		}

	}

	public static class TriggerFunctionDTO {
		@NotNull(message = "Function Id must not be null")
		private Integer funcId;
		private Integer trigSeq;
		private List<FuncParamDTO> funcParams;

		public Integer getFuncId() {
			return funcId;
		}

		public void setFuncId(Integer funcId) {
			this.funcId = funcId;
		}

		public Integer getTrigSeq() {
			return trigSeq;
		}

		public void setTrigSeq(Integer trigSeq) {
			this.trigSeq = trigSeq;
		}

		public List<FuncParamDTO> getFuncParams() {
			return funcParams;
		}

		public void setFuncParams(List<FuncParamDTO> funcParams) {
			this.funcParams = funcParams;
		}

	}

	public static class FuncParamDTO {
		@NotBlank(message = "Parameter Name must not be blank")
		private String paramName;
		@NotNull(message = "Attribute Id must not be null")
		private String attrId;
		private String taskId;
		private String constant;
		private String defParamTypeId;

		public String getParamName() {
			return paramName;
		}

		public void setParamName(String paramName) {
			this.paramName = paramName;
		}

		public String getAttrId() {
			return attrId;
		}

		public void setAttrId(String attrId) {
			this.attrId = attrId;
		}

		public String getTaskId() {
			return taskId;
		}

		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}

		public String getConstant() {
			return constant;
		}

		public void setConstant(String constant) {
			this.constant = constant;
		}

		public String getDefParamTypeId() {
			return defParamTypeId;
		}

		public void setDefParamTypeId(String defParamTypeId) {
			this.defParamTypeId = defParamTypeId;
		}

	}

}
