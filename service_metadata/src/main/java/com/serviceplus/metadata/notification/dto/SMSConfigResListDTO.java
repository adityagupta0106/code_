package com.serviceplus.metadata.notification.dto;

public class SMSConfigResListDTO {

	private Long id;
	private String smsProvider;
	private String url;
	private String status;
	private String smsId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

	public String getSmsProvider() {
		return smsProvider;
	}

	public void setSmsProvider(String smsProvider) {
		this.smsProvider = smsProvider;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}



}
