package com.serviceplus.metadata.inboxSentboxFilterConfig.dto;

import java.util.List;

import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.inboxSentboxFilterConfig.dto.InboxSentBoxFilterDTO.FilterAttributeDTO;

public class FilterJSONDTO {

	private LabelValue service;
	private List<FilterAttributeDTO> filterInput;
	private List<FilterAttributeDTO> filterOutput;

	public LabelValue getService() {
		return service;
	}

	public void setService(LabelValue service) {
		this.service = service;
	}

	public List<FilterAttributeDTO> getFilterInput() {
		return filterInput;
	}

	public void setFilterInput(List<FilterAttributeDTO> filterInput) {
		this.filterInput = filterInput;
	}

	public List<FilterAttributeDTO> getFilterOutput() {
		return filterOutput;
	}

	public void setFilterOutput(List<FilterAttributeDTO> filterOutput) {
		this.filterOutput = filterOutput;
	}
}
