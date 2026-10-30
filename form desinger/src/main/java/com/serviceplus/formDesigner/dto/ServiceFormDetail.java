package com.serviceplus.formDesigner.dto;

public class ServiceFormDetail {
	private Integer serviceId;
	private String formId;
	private String txnId;
	private Boolean defaultFormEncryption;
	private Boolean encryptAtRest;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public Boolean getDefaultFormEncryption() {
		return defaultFormEncryption;
	}

	public void setDefaultFormEncryption(Boolean defaultFormEncryption) {
		this.defaultFormEncryption = defaultFormEncryption;
	}

	public Boolean getEncryptAtRest() {
		return encryptAtRest;
	}

	public void setEncryptAtRest(Boolean encryptAtRest) {
		this.encryptAtRest = encryptAtRest;
	}

}
