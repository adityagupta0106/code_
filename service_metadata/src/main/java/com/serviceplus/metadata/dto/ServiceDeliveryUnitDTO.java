package com.serviceplus.metadata.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class ServiceDeliveryUnitDTO {
	@NotNull(message = "serviceId can't be null")
	private Integer serviceId;
	private String sduSource;
	@NotNull(message = "service delivery units can't be null")
	private List<ServiceDeliveryUnitJSON> serviceDeliveryUnits;

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

	public static class ServiceDeliveryUnitJSON {
		private LabelValue userRole;
	    private HierarchyLabelValue serviceDeliveryUnit;
	    private HierarchyLabelValue userLevel;
	    private LabelValue designation;

		public LabelValue getUserRole() {
			return userRole;
		}

		public void setUserRole(LabelValue userRole) {
			this.userRole = userRole;
		}

		public HierarchyLabelValue getServiceDeliveryUnit() {
			return serviceDeliveryUnit;
		}

		public void setServiceDeliveryUnit(HierarchyLabelValue serviceDeliveryUnit) {
			this.serviceDeliveryUnit = serviceDeliveryUnit;
		}

		public HierarchyLabelValue getUserLevel() {
			return userLevel;
		}

		public void setUserLevel(HierarchyLabelValue userLevel) {
			this.userLevel = userLevel;
		}

		public LabelValue getDesignation() {
			return designation;
		}

		public void setDesignation(LabelValue designation) {
			this.designation = designation;
		}

	}
}
