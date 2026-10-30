package com.serviceplus.metadata.dto;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalSystemGeneralReqDTO {
	private String clientId;

	@NotNull(message = "General detail must not be null")
	private GeneralDetail generalDetail;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public GeneralDetail getGeneralDetail() {
		if(generalDetail == null) {
			generalDetail = new GeneralDetail();
		}
		return generalDetail;
	}

	public void setGeneralDetail(GeneralDetail generalDetail) {
		this.generalDetail = generalDetail;
	}

	public static class GeneralDetail {

		@NotBlank(message = "System name must not be blank")
		private String clientName;

		@NotBlank(message = "Base URL must not be null")
		@URL(message = "Invalid base URL")
		private String baseUrl;

		@NotBlank(message = "Staging URL must not be null")
		@URL(message = "Invalid staging URL")
		private String stagingUrl;

		@NotBlank(message = "Client description must not be null")
		private String clientDescription;

		private Boolean autoSubmissionEnable = false;

		private Boolean whitelistIpRequired;

		private String liveIp;

		private String stagingIp;

		private Boolean redirectUrlFlag = false;

		@URL(message = "Invalid Production Redirect URL")
		private String redirectUrlProd;

		@URL(message = "Invalid Staging Redirect URL")
		private String redirectUrlStaging;

		private String redirectUrlMethodType;

		private String redirectUrlParamFlag = "N";

		private Integer redirectionTime;

		private String redirectionMsg;

		private String redirectionLinkText;

		private String redirectionMsgPosition;

		@NotBlank(message = "Admin name must not be null")
		private String clientSpokespersonName;

		@NotBlank(message = "Admin email must not be null")
		@Email(message = "Invalid email format")
		private String clientSpokespersonEmail;

		@NotBlank(message = "Admin mobile must not be null")
		@Pattern(regexp = "^[0-9]{10}$", message = "Invalid mobile number")
		private String clientSpokespersonMob;

		private String clientSpokespersonDesg;

		public String getClientName() {
			return clientName;
		}

		public void setClientName(String clientName) {
			this.clientName = clientName;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public String getStagingUrl() {
			return stagingUrl;
		}

		public void setStagingUrl(String stagingUrl) {
			this.stagingUrl = stagingUrl;
		}

		public String getClientDescription() {
			return clientDescription;
		}

		public void setClientDescription(String clientDescription) {
			this.clientDescription = clientDescription;
		}

		public Boolean getAutoSubmissionEnable() {
			return autoSubmissionEnable;
		}

		public void setAutoSubmissionEnable(Boolean autoSubmissionEnable) {
			this.autoSubmissionEnable = autoSubmissionEnable;
		}

		public Boolean getWhitelistIpRequired() {
			return whitelistIpRequired;
		}

		public void setWhitelistIpRequired(Boolean whitelistIpRequired) {
			this.whitelistIpRequired = whitelistIpRequired;
		}

		public String getLiveIp() {
			return liveIp;
		}

		public void setLiveIp(String liveIp) {
			this.liveIp = liveIp;
		}

		public String getStagingIp() {
			return stagingIp;
		}

		public void setStagingIp(String stagingIp) {
			this.stagingIp = stagingIp;
		}

		public Boolean getRedirectUrlFlag() {
			return redirectUrlFlag;
		}

		public void setRedirectUrlFlag(Boolean redirectUrlFlag) {
			this.redirectUrlFlag = redirectUrlFlag;
		}

		public String getRedirectUrlProd() {
			return redirectUrlProd;
		}

		public void setRedirectUrlProd(String redirectUrlProd) {
			this.redirectUrlProd = redirectUrlProd;
		}

		public String getRedirectUrlStaging() {
			return redirectUrlStaging;
		}

		public void setRedirectUrlStaging(String redirectUrlStaging) {
			this.redirectUrlStaging = redirectUrlStaging;
		}

		public String getRedirectUrlMethodType() {
			return redirectUrlMethodType;
		}

		public void setRedirectUrlMethodType(String redirectUrlMethodType) {
			this.redirectUrlMethodType = redirectUrlMethodType;
		}

		public String getRedirectUrlParamFlag() {
			return redirectUrlParamFlag;
		}

		public void setRedirectUrlParamFlag(String redirectUrlParamFlag) {
			this.redirectUrlParamFlag = redirectUrlParamFlag;
		}

		public Integer getRedirectionTime() {
			return redirectionTime;
		}

		public void setRedirectionTime(Integer redirectionTime) {
			this.redirectionTime = redirectionTime;
		}

		public String getRedirectionMsg() {
			return redirectionMsg;
		}

		public void setRedirectionMsg(String redirectionMsg) {
			this.redirectionMsg = redirectionMsg;
		}

		public String getRedirectionLinkText() {
			return redirectionLinkText;
		}

		public void setRedirectionLinkText(String redirectionLinkText) {
			this.redirectionLinkText = redirectionLinkText;
		}

		public String getRedirectionMsgPosition() {
			return redirectionMsgPosition;
		}

		public void setRedirectionMsgPosition(String redirectionMsgPosition) {
			this.redirectionMsgPosition = redirectionMsgPosition;
		}

		public String getClientSpokespersonName() {
			return clientSpokespersonName;
		}

		public void setClientSpokespersonName(String clientSpokespersonName) {
			this.clientSpokespersonName = clientSpokespersonName;
		}

		public String getClientSpokespersonEmail() {
			return clientSpokespersonEmail;
		}

		public void setClientSpokespersonEmail(String clientSpokespersonEmail) {
			this.clientSpokespersonEmail = clientSpokespersonEmail;
		}

		public String getClientSpokespersonMob() {
			return clientSpokespersonMob;
		}

		public void setClientSpokespersonMob(String clientSpokespersonMob) {
			this.clientSpokespersonMob = clientSpokespersonMob;
		}

		public String getClientSpokespersonDesg() {
			return clientSpokespersonDesg;
		}

		public void setClientSpokespersonDesg(String clientSpokespersonDesg) {
			this.clientSpokespersonDesg = clientSpokespersonDesg;
		}
	}

}
