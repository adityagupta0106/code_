package com.serviceplus.metadata.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.serviceplus.metadata.notification.enums.SmsServiceProvider;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SMSConfigurationDTO {
	@NotNull(message = "SMS Service Provider is required")
	private SmsServiceProvider smsServiceProvider;

	@Size(min = 1, max = 100, message = "URL can contain a maximum of 100 characters only")
	private String url;

	@Size(min = 1, max = 50, message = "Authentication Password can contain a maximum of 50 characters only")
	private String authenticationPassword;

	@Size(min = 1, max = 50, message = "Confirm Authentication Password can contain a maximum of 50 characters only")
	private String confirmAuthenticationPassword;

	@Size(min = 1, max = 50, message = "SMS ID can contain a maximum of 50 characters only")
	private String smsId;

	@Size(min = 6, max = 20, message = "Signature ID must be between 6 and 20 characters.")
	private String signatureId;

	private String dltEntityId;

	private String userName;
	private String senderId;

	private String secureKey;

	private String apiServiceKey;

	public SmsServiceProvider getSmsServiceProvider() {
		return smsServiceProvider;
	}

	public void setSmsServiceProvider(SmsServiceProvider smsServiceProvider) {
		this.smsServiceProvider = smsServiceProvider;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthenticationPassword() {
		return authenticationPassword;
	}

	public void setAuthenticationPassword(String authenticationPassword) {
		this.authenticationPassword = authenticationPassword;
	}

	public String getConfirmAuthenticationPassword() {
		return confirmAuthenticationPassword;
	}

	public void setConfirmAuthenticationPassword(String confirmAuthenticationPassword) {
		this.confirmAuthenticationPassword = confirmAuthenticationPassword;
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

	public String getSignatureId() {
		return signatureId;
	}

	public void setSignatureId(String signatureId) {
		this.signatureId = signatureId;
	}

	public String getDltEntityId() {
		return dltEntityId;
	}

	public void setDltEntityId(String dltEntityId) {
		this.dltEntityId = dltEntityId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSecureKey() {
		return secureKey;
	}

	public void setSecureKey(String secureKey) {
		this.secureKey = secureKey;
	}

	public String getApiServiceKey() {
		return apiServiceKey;
	}

	public void setApiServiceKey(String apiServiceKey) {
		this.apiServiceKey = apiServiceKey;
	}

}
