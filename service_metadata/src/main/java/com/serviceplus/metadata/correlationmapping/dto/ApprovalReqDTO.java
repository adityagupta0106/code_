package com.serviceplus.metadata.correlationmapping.dto;

import jakarta.validation.constraints.NotNull;

public class ApprovalReqDTO {
	
	@NotNull(message="id can't be null")
	private Long id;
	
	@NotNull(message="setId can't be null")
	private Long setId;
	
	private Long ServiceId;

	@NotNull(message="setOpId can't be null")
	private String setOpId;
	
	@NotNull(message="status can't be null")
	private Integer status;
	
	private String remark;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSetId() {
		return setId;
	}
	public void setSetId(Long setId) {
		this.setId = setId;
	}
	public Long getServiceId() {
		return ServiceId;
	}
	public void setServiceId(Long serviceId) {
		ServiceId = serviceId;
	}
	public String getSetOpId() {
		return setOpId;
	}
	public void setSetOpId(String setOpId) {
		this.setOpId = setOpId;
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
	
}
