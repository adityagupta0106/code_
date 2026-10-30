package com.serviceplus.metadata.pattern.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "patternName", "rawPattern", "isApplicationRefNo", "configuredTemplateFormat" })
public class ServicePatternSummaryDTO {

	private Long id;
	private String patternName;
	private String rawPattern;

	@JsonProperty("isApplicationRefNo")
	private Boolean isApplicationRefNo;

	private String configuredTemplateFormat;

	public ServicePatternSummaryDTO() {
	}

	public ServicePatternSummaryDTO(Long id, String patternName, String rawPattern, Boolean isApplicationRefNo,
			String configuredTemplateFormat) {
		this.id = id;
		this.patternName = patternName;
		this.rawPattern = rawPattern;
		this.isApplicationRefNo = isApplicationRefNo;
		this.configuredTemplateFormat = configuredTemplateFormat;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public String getRawPattern() {
		return rawPattern;
	}

	public Boolean getIsApplicationRefNo() {
		return isApplicationRefNo;
	}

	public void setIsApplicationRefNo(Boolean isApplicationRefNo) {
		this.isApplicationRefNo = isApplicationRefNo;
	}

	public void setRawPattern(String rawPattern) {
		this.rawPattern = rawPattern;
	}

	public String getConfiguredTemplateFormat() {
		return configuredTemplateFormat;
	}

	public void setConfiguredTemplateFormat(String configuredTemplateFormat) {
		this.configuredTemplateFormat = configuredTemplateFormat;
	}
}
