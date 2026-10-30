package com.serviceplus.metadata.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigCommonDTO {
	private SMSConfigurationDTO smsConfigurationDTO;
	private SandesConfigDTO sandesConfigDTO;

	public SMSConfigurationDTO getSmsConfigurationDTO() {
		return smsConfigurationDTO;
	}

	public void setSmsConfigurationDTO(SMSConfigurationDTO smsConfigurationDTO) {
		this.smsConfigurationDTO = smsConfigurationDTO;
	}

	public SandesConfigDTO getSandesConfigDTO() {
		return sandesConfigDTO;
	}

	public void setSandesConfigDTO(SandesConfigDTO sandesConfigDTO) {
		this.sandesConfigDTO = sandesConfigDTO;
	}

}
