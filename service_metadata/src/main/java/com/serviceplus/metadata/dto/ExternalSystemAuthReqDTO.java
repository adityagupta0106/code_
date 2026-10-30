package com.serviceplus.metadata.dto;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExternalSystemAuthReqDTO {

	@NotBlank(message = "Client Id must not be null")
	private String clientId;

	@NotNull(message = "API detail must not be null")
	private ApiDetail apiDetail;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public ApiDetail getApiDetail() {
		if(apiDetail == null) {
			apiDetail = new ApiDetail();
		}
		return apiDetail;
	}

	public void setApiDetail(ApiDetail apiDetail) {
		this.apiDetail = apiDetail;
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class ApiDetail {
		@URL(message = "Invalid Production API URL")
		private String apiProdUrl;
		@URL(message = "Invalid Staging API URL")
		private String apiStagingUrl;
		private String uId;
		private String uSecret;
		private List<ApiParameterDetail> apiParameterDetails;

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
			if(apiParameterDetails == null) {
				apiParameterDetails = new ArrayList<ExternalSystemAuthReqDTO.ApiDetail.ApiParameterDetail>();
			}
			return apiParameterDetails;
		}

		public void setApiParameterDetails(List<ApiParameterDetail> apiParameterDetails) {
			this.apiParameterDetails = apiParameterDetails;
		}

		@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
