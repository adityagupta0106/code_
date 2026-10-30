package com.serviceplus.metadata.dto;

public class TaskPaymentMappingDTO {
	private Boolean enabled;
	private Long chargeId;
	private Boolean systemGeneratedAck;
	private LabelValue paymentAckDoc;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getSystemGeneratedAck() {
		return systemGeneratedAck;
	}

	public void setSystemGeneratedAck(Boolean systemGeneratedAck) {
		this.systemGeneratedAck = systemGeneratedAck;
	}

	public Long getChargeId() {
		return chargeId;
	}

	public void setChargeId(Long chargeId) {
		this.chargeId = chargeId;
	}

	public LabelValue getPaymentAckDoc() {
		return paymentAckDoc;
	}

	public void setPaymentAckDoc(LabelValue paymentAckDoc) {
		this.paymentAckDoc = paymentAckDoc;
	}

}
