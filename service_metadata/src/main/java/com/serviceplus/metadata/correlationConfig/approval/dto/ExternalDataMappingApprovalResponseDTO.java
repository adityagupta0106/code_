package com.serviceplus.metadata.correlationConfig.approval.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExternalDataMappingApprovalResponseDTO {

	private Integer id;

	private String sourceType;

	private Long serviceId;

	private Long baseServiceId;

	private String serviceName;

	private String reqUserName;
	
	private Long reqUserId;

	private String reqUserDepartmentName;

	private Long setId;

	private String setName;

	private List<SetApprovalDTO> sets = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Long getReqUserId() {
		return reqUserId;
	}

	public void setReqUserId(Long reqUserId) {
		this.reqUserId = reqUserId;
	}

	public String getReqUserName() {
		return reqUserName;
	}

	public void setReqUserName(String reqUserName) {
		this.reqUserName = reqUserName;
	}

	public String getReqUserDepartmentName() {
		return reqUserDepartmentName;
	}

	public void setReqUserDepartmentName(String reqUserDepartmentName) {
		this.reqUserDepartmentName = reqUserDepartmentName;
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

	public List<SetApprovalDTO> getSets() {
		return sets;
	}

	public void setSets(List<SetApprovalDTO> sets) {
		this.sets = sets;
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class SetApprovalDTO {

		private Long setId;

		private String setName;

		private boolean approved;

		private List<OutputSetApprovalDTO> outputSet = new ArrayList<>();

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

		public boolean isApproved() {
			return approved;
		}

		public void setApproved(boolean approved) {
			this.approved = approved;
		}

		public List<OutputSetApprovalDTO> getOutputSet() {
			return outputSet;
		}

		public void setOutputSet(List<OutputSetApprovalDTO> outputSet) {
			this.outputSet = outputSet;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class OutputSetApprovalDTO {

		private String id;

		private String name;

		private boolean approved;

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

		public boolean isApproved() {
			return approved;
		}

		public void setApproved(boolean approved) {
			this.approved = approved;
		}
	}
}