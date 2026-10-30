package com.serviceplus.metadata.inboxSentboxFilterConfig.dto;

import java.util.List;

import com.serviceplus.metadata.dto.LabelValue;

public class InboxSentBoxFilterListDTO {

	private Long id;

	private String serviceName;

	private String filterName;

	private List<LabelValue> targetTask;

	private String outputType;

	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public List<LabelValue> getTargetTask() {
		return targetTask;
	}

	public void setTargetTask(List<LabelValue> targetTask) {
		this.targetTask = targetTask;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
