package com.serviceplus.metadata.correlationmapping.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApprovedDataSetResponseDTO {

	private Long serviceId;
	private String serviceName;
	private Long baseServiceId;
	private Long setId;
	private String setName;
	private String sourceType;
	private String status;
	private String remark;

	private List<ApprovedSetDTO> approvedSets = new ArrayList<>();

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class ApprovedSetDTO {

		private Long setId;
		private String setName;
		private List<OutputSetDTO> outputSet = new ArrayList<>();

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

		public List<OutputSetDTO> getOutputSet() {
			return outputSet;
		}

		public void setOutputSet(List<OutputSetDTO> outputSet) {
			this.outputSet = outputSet;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class OutputSetDTO {

		private String id;
		private String name;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

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

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<ApprovedSetDTO> getApprovedSets() {
		return approvedSets;
	}

	public void setApprovedSets(List<ApprovedSetDTO> approvedSets) {
		this.approvedSets = approvedSets;
	}

}