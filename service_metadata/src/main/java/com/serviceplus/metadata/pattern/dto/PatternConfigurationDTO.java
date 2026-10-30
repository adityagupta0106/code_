package com.serviceplus.metadata.pattern.dto;

import java.util.List;

import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.pattern.enums.CallbackBehaviour;
import com.serviceplus.metadata.pattern.enums.PatternGenerationEvent;
import com.serviceplus.metadata.pattern.enums.SeriesType;
import com.serviceplus.metadata.pattern.enums.SpgdAttributeCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatternConfigurationDTO {

	private Long id;

	@NotBlank(message = "Pattern Name is required")
	@Size(max = 50, message = "Pattern Name cannot exceed 50 characters")
	@Pattern(regexp = "^[A-Za-z0-9 ]+$", message = "Pattern Name can contain only letters, numbers and spaces")
	private String patternName;

	@NotNull(message = "Reset Entity Attributes is required")
	private List<LabelValue> resetEntityAttributes;

	private SeriesType seriesType = SeriesType.NUMERIC;

	@NotNull(message = "numberOfAlphabets is required")
	private Integer numberOfAlphabets;

	private String startAlphabet;

	private Integer numberOfDigits;

	private String minimumValue;

	private String maximumValue;

	private PatternGenerationEvent patternGenerationEvent = PatternGenerationEvent.UNIQUE_FOR_EACH_APPLICATION;

	private String format;

	private List<LabelValue> patternAttributes;

	private SpgdAttributeCode spgdAttributeCode = SpgdAttributeCode.LGD_LOCATION_CODE;

	private CallbackBehaviour callbackBehaviour = CallbackBehaviour.REGENERATE;



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

	public List<LabelValue> getResetEntityAttributes() {
		return resetEntityAttributes;
	}

	public void setResetEntityAttributes(List<LabelValue> resetEntityAttributes) {
		this.resetEntityAttributes = resetEntityAttributes;
	}

	public SeriesType getSeriesType() {
		return seriesType;
	}

	public void setSeriesType(SeriesType seriesType) {
		this.seriesType = seriesType;
	}

	public Integer getNumberOfAlphabets() {
		return numberOfAlphabets;
	}

	public void setNumberOfAlphabets(Integer numberOfAlphabets) {
		this.numberOfAlphabets = numberOfAlphabets;
	}

	public String getStartAlphabet() {
		return startAlphabet;
	}

	public void setStartAlphabet(String startAlphabet) {
		this.startAlphabet = startAlphabet;
	}

	public Integer getNumberOfDigits() {
		return numberOfDigits;
	}

	public void setNumberOfDigits(Integer numberOfDigits) {
		this.numberOfDigits = numberOfDigits;
	}

	public String getMinimumValue() {
		return minimumValue;
	}

	public void setMinimumValue(String minimumValue) {
		this.minimumValue = minimumValue;
	}

	public String getMaximumValue() {
		return maximumValue;
	}

	public void setMaximumValue(String maximumValue) {
		this.maximumValue = maximumValue;
	}

	public PatternGenerationEvent getPatternGenerationEvent() {
		return patternGenerationEvent;
	}

	public void setPatternGenerationEvent(PatternGenerationEvent patternGenerationEvent) {
		this.patternGenerationEvent = patternGenerationEvent;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}


	public List<LabelValue> getPatternAttributes() {
		return patternAttributes;
	}

	public void setPatternAttributes(List<LabelValue> patternAttributes) {
		this.patternAttributes = patternAttributes;
	}

	public SpgdAttributeCode getSpgdAttributeCode() {
		return spgdAttributeCode;
	}

	public void setSpgdAttributeCode(SpgdAttributeCode spgdAttributeCode) {
		this.spgdAttributeCode = spgdAttributeCode;
	}

	public CallbackBehaviour getCallbackBehaviour() {
		return callbackBehaviour;
	}

	public void setCallbackBehaviour(CallbackBehaviour callbackBehaviour) {
		this.callbackBehaviour = callbackBehaviour;
	}
}