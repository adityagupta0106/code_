package com.serviceplus.formDesigner.formconfig.attrtypemaster.kafka;

public class TenantDomainKafkaEvent {
	private String tenantId;
	private String prodUrl;
	private String preprodUrl;
	private String action;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getProdUrl() {
		return prodUrl;
	}

	public void setProdUrl(String prodUrl) {
		this.prodUrl = prodUrl;
	}

	public String getPreprodUrl() {
		return preprodUrl;
	}

	public void setPreprodUrl(String preprodUrl) {
		this.preprodUrl = preprodUrl;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
