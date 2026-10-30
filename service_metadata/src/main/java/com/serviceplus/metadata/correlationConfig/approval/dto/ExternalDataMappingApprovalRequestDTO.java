package com.serviceplus.metadata.correlationConfig.approval.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExternalDataMappingApprovalRequestDTO {

	@NotBlank(message = "source can't be blank")
	private String source;

	private Long serviceId;

	private Long setId;

	@NotNull(message = "reqUserId can't be null")
	private Long reqUserId;

	@NotNull(message = "sets can't be null")
	@Valid
	private List<ApprovalSetDTO> sets = new ArrayList<>();

	@NotNull(message = "status can't be null")
	private Integer status;

	private String remark;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getSetId() {
		return setId;
	}

	public void setSetId(Long setId) {
		this.setId = setId;
	}

	public Long getReqUserId() {
		return reqUserId;
	}

	public void setReqUserId(Long reqUserId) {
		this.reqUserId = reqUserId;
	}

	public List<ApprovalSetDTO> getSets() {
		return sets;
	}

	public void setSets(List<ApprovalSetDTO> sets) {
		this.sets = sets;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static class ApprovalSetDTO {

		@NotNull(message = "setId can't be null")
		private Long setId;

		private String setName;

		@Valid
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

	public static class OutputSetDTO {

		@NotBlank(message = "outputSet id can't be blank")
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
}