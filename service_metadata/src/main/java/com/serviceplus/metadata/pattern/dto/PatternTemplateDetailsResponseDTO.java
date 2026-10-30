package com.serviceplus.metadata.pattern.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO.AttributeItemDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO.PatternItemDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO.TokenSettingDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "patternName", "rawPattern", "configuredTemplateFormat", "pattern", "attributes", "prefix",
		"suffix" })
public class PatternTemplateDetailsResponseDTO {

	private String patternName;
	private String rawPattern;
	private String configuredTemplateFormat;
	private List<PatternItemDTO> pattern;
	private List<AttributeItemDTO> attributes;
	private TokenSettingDTO prefix;
	private TokenSettingDTO suffix;

	public PatternTemplateDetailsResponseDTO() {
	}

	public PatternTemplateDetailsResponseDTO(List<PatternItemDTO> pattern, List<AttributeItemDTO> attributes,
			TokenSettingDTO prefix, TokenSettingDTO suffix) {
		this.pattern = pattern;
		this.attributes = attributes;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public PatternTemplateDetailsResponseDTO(String patternName, String rawPattern, String configuredTemplateFormat,
			List<PatternItemDTO> pattern, List<AttributeItemDTO> attributes, TokenSettingDTO prefix,
			TokenSettingDTO suffix) {
		this.patternName = patternName;
		this.rawPattern = rawPattern;
		this.configuredTemplateFormat = configuredTemplateFormat;
		this.pattern = pattern;
		this.attributes = attributes;
		this.prefix = prefix;
		this.suffix = suffix;
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

	public void setRawPattern(String rawPattern) {
		this.rawPattern = rawPattern;
	}

	public String getConfiguredTemplateFormat() {
		return configuredTemplateFormat;
	}

	public void setConfiguredTemplateFormat(String configuredTemplateFormat) {
		this.configuredTemplateFormat = configuredTemplateFormat;
	}

	public List<PatternItemDTO> getPattern() {
		return pattern;
	}

	public void setPattern(List<PatternItemDTO> pattern) {
		this.pattern = pattern;
	}

	public List<AttributeItemDTO> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeItemDTO> attributes) {
		this.attributes = attributes;
	}

	public TokenSettingDTO getPrefix() {
		return prefix;
	}

	public void setPrefix(TokenSettingDTO prefix) {
		this.prefix = prefix;
	}

	public TokenSettingDTO getSuffix() {
		return suffix;
	}

	public void setSuffix(TokenSettingDTO suffix) {
		this.suffix = suffix;
	}
}
