package com.serviceplus.metadata.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SandesConfigDTO {

	@NotBlank(message = "Configuration name is required")
	@Size(min = 1, max = 200, message = "Configuration Name must be between 1 and 200 characters.")
	private String configurationName;
	
	@NotBlank(message = "Client Id is required")
	@Size(min = 1, max = 500, message = "Client Id must be between 1 and 500 characters.")
	private String clientId;
	
	@NotBlank(message = "Client secret is required")
	@Size(min = 1, max = 500, message = "Client secret must be between 1 and 500 characters.")
	private String clientSecret;
	
	@NotBlank(message = "Hmac Key is required")
	@Size(min = 1, max = 200, message = "Hmac Key must be between 1 and 200 characters.")
	private String hmacKey;

	public String getConfigurationName() {
		return configurationName;
	}

	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getHmacKey() {
		return hmacKey;
	}

	public void setHmacKey(String hmacKey) {
		this.hmacKey = hmacKey;
	}

}
