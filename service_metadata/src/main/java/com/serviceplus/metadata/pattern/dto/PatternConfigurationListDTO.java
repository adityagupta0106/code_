package com.serviceplus.metadata.pattern.dto;

public class PatternConfigurationListDTO {

	private Long id;

	private String patternName;

	public PatternConfigurationListDTO() {
	}

	public PatternConfigurationListDTO(Long id, String patternName) {

		this.id = id;
		this.patternName = patternName;
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
}