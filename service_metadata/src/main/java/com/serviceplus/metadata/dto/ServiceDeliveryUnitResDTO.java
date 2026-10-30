package com.serviceplus.metadata.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON;

public class ServiceDeliveryUnitResDTO {
	private Integer serviceId;
	private String sduSource;
	private List<ServiceDeliveryUnitJSON> serviceDeliveryUnits;
	private String tablist;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<ServiceDeliveryUnitJSON> historyServiceDeliveryUnits;

    @JsonIgnore
	private List<ServiceDeliveryUnitJSON> oldServiceDeliveryUnits;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String historySduSource;

    @JsonIgnore
	private String oldSduSource;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getSduSource() {
		return sduSource;
	}

	public void setSduSource(String sduSource) {
		this.sduSource = sduSource;
	}

	public List<ServiceDeliveryUnitJSON> getServiceDeliveryUnits() {
		return serviceDeliveryUnits;
	}

	public void setServiceDeliveryUnits(List<ServiceDeliveryUnitJSON> serviceDeliveryUnits) {
		this.serviceDeliveryUnits = serviceDeliveryUnits;
	}

	public String getTablist() {
		return tablist;
	}

	public void setTablist(String tablist) {
		this.tablist = tablist;
	}

	public List<ServiceDeliveryUnitJSON> getHistoryServiceDeliveryUnits() {
		return historyServiceDeliveryUnits;
	}

	public void setHistoryServiceDeliveryUnits(List<ServiceDeliveryUnitJSON> historyServiceDeliveryUnits) {
		this.historyServiceDeliveryUnits = historyServiceDeliveryUnits;
		if (this.oldServiceDeliveryUnits == null) {
			this.oldServiceDeliveryUnits = historyServiceDeliveryUnits;
		}
	}

	public List<ServiceDeliveryUnitJSON> getOldServiceDeliveryUnits() {
		return oldServiceDeliveryUnits;
	}

	public void setOldServiceDeliveryUnits(List<ServiceDeliveryUnitJSON> oldServiceDeliveryUnits) {
		this.oldServiceDeliveryUnits = oldServiceDeliveryUnits;
		if (this.historyServiceDeliveryUnits == null) {
			this.historyServiceDeliveryUnits = oldServiceDeliveryUnits;
		}
	}

	public String getHistorySduSource() {
		return historySduSource;
	}

	public void setHistorySduSource(String historySduSource) {
		this.historySduSource = historySduSource;
		if (this.oldSduSource == null) {
			this.oldSduSource = historySduSource;
		}
	}

	public String getOldSduSource() {
		return oldSduSource;
	}

	public void setOldSduSource(String oldSduSource) {
		this.oldSduSource = oldSduSource;
		if (this.historySduSource == null) {
			this.historySduSource = oldSduSource;
		}
	}
}
