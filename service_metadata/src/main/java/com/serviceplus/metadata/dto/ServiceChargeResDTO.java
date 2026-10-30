package com.serviceplus.metadata.dto;

import java.util.List;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceChargeResDTO {

	private Long chargeId;

	@NotNull(message = "serviceId can't be null")
	private Integer serviceId;
	
	private List<LabelValue> tasks;

	@NotBlank(message = "templateName can't be blank")
	private String templateName;

	@NotNull(message = "chargeDefinedFor can't be null")
	private List<LabelValue> chargeDefinedFor;

	@NotNull(message = "serviceUnitWise can't be null")
	private Boolean serviceUnitWise;

	private Boolean defaultCharge;

	private List<ChargeTypeDTO> chargeTypeList;

	@NotNull(message = "paymentModes can't be null")
	private List<PaymentModeDTO> paymentModes;

	private List<ClosedUserTypeDTO> closedUserType;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public Long getChargeId() {
		return chargeId;
	}

	public void setChargeId(Long chargeId) {
		this.chargeId = chargeId;
	}

	public List<LabelValue> getTasks() {
		return tasks;
	}

	public void setTasks(List<LabelValue> tasks) {
		this.tasks = tasks;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<LabelValue> getChargeDefinedFor() {
		return chargeDefinedFor;
	}

	public void setChargeDefinedFor(List<LabelValue> chargeDefinedFor) {
		this.chargeDefinedFor = chargeDefinedFor;
	}

	public Boolean getServiceUnitWise() {
		return serviceUnitWise;
	}

	public void setServiceUnitWise(Boolean serviceUnitWise) {
		this.serviceUnitWise = serviceUnitWise;
	}

	public Boolean getDefaultCharge() {
		return defaultCharge;
	}

	public void setDefaultCharge(Boolean defaultCharge) {
		this.defaultCharge = defaultCharge;
	}

	public List<ChargeTypeDTO> getChargeTypeList() {
		return chargeTypeList;
	}

	public void setChargeTypeList(List<ChargeTypeDTO> chargeTypeList) {
		this.chargeTypeList = chargeTypeList;
	}

	public List<PaymentModeDTO> getPaymentModes() {
		return paymentModes;
	}

	public void setPaymentModes(List<PaymentModeDTO> paymentModes) {
		this.paymentModes = paymentModes;
	}

	public List<ClosedUserTypeDTO> getClosedUserType() {
		return closedUserType;
	}

	public void setClosedUserType(List<ClosedUserTypeDTO> closedUserType) {
		this.closedUserType = closedUserType;
	}

	public static class ChargeTypeDTO {
		private String name;
		private Double amount;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Double getAmount() {
			return amount;
		}

		public void setAmount(Double amount) {
			this.amount = amount;
		}
	}

	public static class PaymentModeDTO {
		private String value;
		private String label;
		private Boolean onlineFlag;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Boolean getOnlineFlag() {
			return onlineFlag;
		}

		public void setOnlineFlag(Boolean onlineFlag) {
			this.onlineFlag = onlineFlag;
		}
	}

	public static class ClosedUserTypeDTO {
		private Integer value;
		private String label;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}

}
