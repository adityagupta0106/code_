package com.serviceplus.metadata.dto;

import java.util.List;

public class ExternalClientApiDetailDTO {

    private Long registrationId;

    private String clientId;

    private String clientName;

    private String liveIp;

    private String stagingIp;

    private Integer apiType;

    private Boolean isWhitelistIpReq;

    private String status;

    private String apiStagingUrl;

    private String apiProdUrl;

    private String callType;

    private String uId;

    private String uSecret;

    private List<ApiParameterDetailDTO> apiParameters;
    
    public static class ApiParameterDetailDTO {
        private String paramType;
        private String paramName;
        private String paramNode;
        private String paramValType;
        private String paramConstVal;
        private Integer paramSequence;
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
		public Integer getParamSequence() {
			return paramSequence;
		}
		public void setParamSequence(Integer paramSequence) {
			this.paramSequence = paramSequence;
		}
    }

	public Long getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(Long registrationId) {
		this.registrationId = registrationId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
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

	public Integer getApiType() {
		return apiType;
	}

	public void setApiType(Integer apiType) {
		this.apiType = apiType;
	}

	public Boolean getIsWhitelistIpReq() {
		return isWhitelistIpReq;
	}

	public void setIsWhitelistIpReq(Boolean isWhitelistIpReq) {
		this.isWhitelistIpReq = isWhitelistIpReq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApiStagingUrl() {
		return apiStagingUrl;
	}

	public void setApiStagingUrl(String apiStagingUrl) {
		this.apiStagingUrl = apiStagingUrl;
	}

	public String getApiProdUrl() {
		return apiProdUrl;
	}

	public void setApiProdUrl(String apiProdUrl) {
		this.apiProdUrl = apiProdUrl;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
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

	public List<ApiParameterDetailDTO> getApiParameters() {
		return apiParameters;
	}

	public void setApiParameters(List<ApiParameterDetailDTO> apiParameters) {
		this.apiParameters = apiParameters;
	}

	
}
