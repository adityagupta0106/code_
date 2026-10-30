package com.serviceplus.metadata.mvel.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MvelTriggerPointResponseDTO {

	private Long eventId;
	
	private String triggerPoint;

	private String triggerPointDetails;

	private String functionName;

	private MappedMvelFunctionDTO parameters;

	public MvelTriggerPointResponseDTO() {
		this.parameters = new MappedMvelFunctionDTO();
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getTriggerPoint() {
		return triggerPoint;
	}

	public void setTriggerPoint(String triggerPoint) {
		this.triggerPoint = triggerPoint;
	}

	public String getTriggerPointDetails() {
		return triggerPointDetails;
	}

	public void setTriggerPointDetails(String triggerPointDetails) {
		this.triggerPointDetails = triggerPointDetails;
	}

	public MappedMvelFunctionDTO getParameters() {
		return parameters;
	}

	public void setParameters(MappedMvelFunctionDTO parameters) {
		this.parameters = parameters;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class MappedMvelFunctionDTO {
		private Integer trigSeq;
		private List<Map<String,String>> parameters;

		public Integer getTrigSeq() {
			return trigSeq;
		}

		public void setTrigSeq(Integer trigSeq) {
			this.trigSeq = trigSeq;
		}

		public List<Map<String,String>> getParameters() {
			return parameters;
		}

		public void setParameters(List<Map<String,String>> parameters) {
			this.parameters = parameters;
		}
	}
}
