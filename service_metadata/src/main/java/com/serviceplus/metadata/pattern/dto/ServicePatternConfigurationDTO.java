package com.serviceplus.metadata.pattern.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServicePatternConfigurationDTO {

	private Long id;

	@NotNull(message = "serviceId is required")
	private Long serviceId;


	@NotNull(message = "patternId is required")
	private Long patternId;

	@JsonProperty("isApplicationRefNo")
	private Boolean isApplicationRefNo;

	@Valid
	@NotEmpty(message = "attributeMappings cannot be empty")
	private List<AttributeMappingDTO> attributeMappings;

	public ServicePatternConfigurationDTO() {
	}

	public ServicePatternConfigurationDTO(Long patternId, Boolean isApplicationRefNo,
			List<AttributeMappingDTO> attributeMappings) {
		this.patternId = patternId;
		this.isApplicationRefNo = isApplicationRefNo;
		this.attributeMappings = attributeMappings;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}


	public Long getPatternId() {
		return patternId;
	}

	public void setPatternId(Long patternId) {
		this.patternId = patternId;
	}

	@JsonProperty("isApplicationRefNo")
	public Boolean getIsApplicationRefNo() {
		return isApplicationRefNo;
	}

	@JsonProperty("isApplicationRefNo")
	public void setIsApplicationRefNo(Boolean isApplicationRefNo) {
		this.isApplicationRefNo = isApplicationRefNo;
	}

	public Boolean isApplicationRefNo() {
		return isApplicationRefNo;
	}

	public Boolean getApplicationRefNo() {
		return isApplicationRefNo;
	}

	public void setApplicationRefNo(Boolean applicationRefNo) {
		this.isApplicationRefNo = applicationRefNo;
	}

	public List<AttributeMappingDTO> getAttributeMappings() {
		return attributeMappings;
	}

	public void setAttributeMappings(List<AttributeMappingDTO> attributeMappings) {
		this.attributeMappings = attributeMappings;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class AttributeMappingDTO {

		@NotBlank(message = "keyValue cannot be blank")
		private String keyValue;

		@NotBlank(message = "keyType cannot be blank")
		private String keyType;

		@NotBlank(message = "mappedAttrType cannot be blank")
		private String mappedAttrType;

		@NotBlank(message = "mappedAttrValue cannot be blank")
		private String mappedAttrValue;

		public AttributeMappingDTO() {
		}

		public AttributeMappingDTO(String keyValue, String keyType, String mappedAttrType, String mappedAttrValue) {
			this.keyValue = keyValue;
			this.keyType = keyType;
			this.mappedAttrType = mappedAttrType;
			this.mappedAttrValue = mappedAttrValue;
		}

		public String getKeyValue() {
			return keyValue;
		}

		public void setKeyValue(String keyValue) {
			this.keyValue = keyValue;
		}

		public String getKeyType() {
			return keyType;
		}

		public void setKeyType(String keyType) {
			this.keyType = keyType;
		}

		public String getMappedAttrType() {
			return mappedAttrType;
		}

		public void setMappedAttrType(String mappedAttrType) {
			this.mappedAttrType = mappedAttrType;
		}

		public String getMappedAttrValue() {
			return mappedAttrValue;
		}

		public void setMappedAttrValue(String mappedAttrValue) {
			this.mappedAttrValue = mappedAttrValue;
		}
	}
}
