package com.serviceplus.metadata.dto;

import java.util.ArrayList;
import java.util.List;

public class ExternalSystemDTO {

	private GeneralDetail generalDetail;
	private List<ApiDetail> apiDetails;

	public GeneralDetail getGeneralDetail() {
		if (generalDetail == null) {
			generalDetail = new GeneralDetail();
		}
		return generalDetail;
	}

	public void setGeneralDetail(GeneralDetail generalDetail) {
		this.generalDetail = generalDetail;
	}

	public List<ApiDetail> getApiDetails() {
		if(apiDetails == null) {
			apiDetails = new ArrayList<ExternalSystemDTO.ApiDetail>();
		}
		return apiDetails;
	}

	public void setApiDetails(List<ApiDetail> apiDetails) {
		this.apiDetails = apiDetails;
	}

	public static class GeneralDetail {

		private String clientName;
		private String baseUrl;
		private String stagingUrl;
		private String clientDescription;
		private Boolean autoSubmissionEnable = false;
		private Boolean whitelistIpRequired;
		private String liveIp;
		private String stagingIp;
		private Boolean redirectUrlFlag = false;
		private String redirectUrlProd;
		private String redirectUrlStaging;
		private String redirectUrlMethodType;
		private String redirectUrlParamFlag = "N";
		private Integer redirectionTime;
		private String redirectionMsg;
		private String redirectionLinkText;
		private String redirectionMsgPosition;
		private String clientSpokespersonName;
		private String clientSpokespersonEmail;
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

	public static class ApiDetail {

		private Integer apiType;
		private Boolean draftConfig = false;
		private String apiProdUrl;
		private String apiStagingUrl;
		private String uId;
		private String uSecret;

		private List<ApiParameterDetail> apiParameterDetails;
		
		public Integer getApiType() {
			return apiType;
		}

		public void setApiType(Integer apiType) {
			this.apiType = apiType;
		}

		public Boolean getDraftConfig() {
			return draftConfig;
		}

		public void setDraftConfig(Boolean draftConfig) {
			this.draftConfig = draftConfig;
		}

		public String getApiProdUrl() {
			return apiProdUrl;
		}

		public void setApiProdUrl(String apiProdUrl) {
			this.apiProdUrl = apiProdUrl;
		}

		public String getApiStagingUrl() {
			return apiStagingUrl;
		}

		public void setApiStagingUrl(String apiStagingUrl) {
			this.apiStagingUrl = apiStagingUrl;
		}

		public String getuId() {
			return uId;
		}

		public void setuId(String uId) {
			this.uId = uId;
		}

		public String getuSecret() {
			return uSecret;
		}

		public void setuSecret(String uSecret) {
			this.uSecret = uSecret;
		}
		
		public List<ApiParameterDetail> getApiParameterDetails() {
			return apiParameterDetails;
		}

		public void setApiParameterDetails(List<ApiParameterDetail> apiParameterDetails) {
			this.apiParameterDetails = apiParameterDetails;
		}

		public static class ApiParameterDetail {
			private String paramType;
			private String paramName;
			private String paramNode;
			private String paramValType;
			private String paramConstVal;

			public String getParamType() {
				return paramType;
			}

			public void setParamType(String paramType) {
				this.paramType = paramType;
			}

			public String getParamName() {
				return paramName;
			}

			public void setParamName(String paramName) {
				this.paramName = paramName;
			}

			public String getParamNode() {
				return paramNode;
			}

			public void setParamNode(String paramNode) {
				this.paramNode = paramNode;
			}

			public String getParamValType() {
				return paramValType;
			}

			public void setParamValType(String paramValType) {
				this.paramValType = paramValType;
			}

			public String getParamConstVal() {
				return paramConstVal;
			}

			public void setParamConstVal(String paramConstVal) {
				this.paramConstVal = paramConstVal;
			}

		}
	}
}
