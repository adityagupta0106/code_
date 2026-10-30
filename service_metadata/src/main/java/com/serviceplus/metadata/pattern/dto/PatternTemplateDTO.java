package com.serviceplus.metadata.pattern.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PatternTemplateDTO {

    private Long id;

    @NotBlank(message = "Pattern Name is required")
    @Size(max = 50, message = "Pattern Name cannot exceed 50 characters")
    private String patternName;

    private String configuredTemplateFormat;
    private String rawPattern;

    private List<PatternItemDTO> pattern;

    private List<AttributeItemDTO> attributes;

    @Valid
    private SequenceConfigDTO sequenceConfig;

    @Valid
    private SequenceResetConfigDTO sequenceReset;

    private TokenSettingDTO prefix;

    private TokenSettingDTO suffix;

    public PatternTemplateDTO() {
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

    public String getConfiguredTemplateFormat() {
        return configuredTemplateFormat;
    }

    public void setConfiguredTemplateFormat(String configuredTemplateFormat) {
        this.configuredTemplateFormat = configuredTemplateFormat;
    }

    public String getRawPattern() {
        return rawPattern;
    }

    public void setRawPattern(String rawPattern) {
        this.rawPattern = rawPattern;
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

    public SequenceConfigDTO getSequenceConfig() {
        return sequenceConfig;
    }

    public void setSequenceConfig(SequenceConfigDTO sequenceConfig) {
        this.sequenceConfig = sequenceConfig;
    }

    public SequenceResetConfigDTO getSequenceReset() {
        return sequenceReset;
    }

    public void setSequenceReset(SequenceResetConfigDTO sequenceReset) {
        this.sequenceReset = sequenceReset;
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

    @JsonPropertyOrder({ "label", "value", "sortOrder" })
    public static class PatternItemDTO {
        private String label;
        private String value;
        private Integer sortOrder;
        
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String keyType;

        public PatternItemDTO() {
        }

        public PatternItemDTO(String label, String value, Integer sortOrder) {
            this.label = label;
            this.value = value;
            this.sortOrder = sortOrder;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
		public String getKeyType() {
			return keyType;
		}

		public void setKeyType(String keyType) {
			this.keyType = keyType;
		}

    }

    public static class SequenceConfigDTO {
        private String seriesType;
        private NumericConfigDTO numeric;
        private AlphabeticConfigDTO alphabetic;
        private AlphanumericConfigDTO alphanumeric;

        public SequenceConfigDTO() {
        }

        public String getSeriesType() {
            return seriesType;
        }

        public void setSeriesType(String seriesType) {
            this.seriesType = seriesType;
        }

        public NumericConfigDTO getNumeric() {
            return numeric;
        }

        public void setNumeric(NumericConfigDTO numeric) {
            this.numeric = numeric;
        }

        public AlphabeticConfigDTO getAlphabetic() {
            return alphabetic;
        }

        public void setAlphabetic(AlphabeticConfigDTO alphabetic) {
            this.alphabetic = alphabetic;
        }

        public AlphanumericConfigDTO getAlphanumeric() {
            return alphanumeric;
        }

        public void setAlphanumeric(AlphanumericConfigDTO alphanumeric) {
            this.alphanumeric = alphanumeric;
        }
    }

    public static class NumericConfigDTO {
        private Integer numberOfDigits;
        private Long startValue;
        private Integer incrementBy;

        public NumericConfigDTO() {
        }

        public Integer getNumberOfDigits() {
            return numberOfDigits;
        }

        public void setNumberOfDigits(Integer numberOfDigits) {
            this.numberOfDigits = numberOfDigits;
        }

        public Long getStartValue() {
            return startValue;
        }

        public void setStartValue(Long startValue) {
            this.startValue = startValue;
        }

        public Integer getIncrementBy() {
            return incrementBy;
        }

        public void setIncrementBy(Integer incrementBy) {
            this.incrementBy = incrementBy;
        }
    }

    public static class AlphabeticConfigDTO {
        private Integer numberOfAlphabets;
        private String startingAlphabet;
        private Integer incrementBy;

        public AlphabeticConfigDTO() {
        }

        public Integer getNumberOfAlphabets() {
            return numberOfAlphabets;
        }

        public void setNumberOfAlphabets(Integer numberOfAlphabets) {
            this.numberOfAlphabets = numberOfAlphabets;
        }

        public String getStartingAlphabet() {
            return startingAlphabet;
        }

        public void setStartingAlphabet(String startingAlphabet) {
            this.startingAlphabet = startingAlphabet;
        }

        public Integer getIncrementBy() {
            return incrementBy;
        }

        public void setIncrementBy(Integer incrementBy) {
            this.incrementBy = incrementBy;
        }
    }

    public static class AlphanumericConfigDTO {
        private PartConfigDTO partOne;
        private PartConfigDTO partTwo;
        private String startingPattern;
        private Integer incrementBy;

        public AlphanumericConfigDTO() {
        }

        public PartConfigDTO getPartOne() {
            return partOne;
        }

        public void setPartOne(PartConfigDTO partOne) {
            this.partOne = partOne;
        }

        public PartConfigDTO getPartTwo() {
            return partTwo;
        }

        public void setPartTwo(PartConfigDTO partTwo) {
            this.partTwo = partTwo;
        }

        public String getStartingPattern() {
            return startingPattern;
        }

        public void setStartingPattern(String startingPattern) {
            this.startingPattern = startingPattern;
        }

        public Integer getIncrementBy() {
            return incrementBy;
        }

        public void setIncrementBy(Integer incrementBy) {
            this.incrementBy = incrementBy;
        }
    }

    public static class PartConfigDTO {
        private String type;
        private Integer size;
        private String value;

        public PartConfigDTO() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class SequenceResetConfigDTO {
        private Boolean enabled;
        private List<AttributeItemDTO> attributes;

        public SequenceResetConfigDTO() {
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public List<AttributeItemDTO> getAttributes() {
            return attributes;
        }

        public void setAttributes(List<AttributeItemDTO> attributes) {
            this.attributes = attributes;
        }
    }

    @JsonPropertyOrder({ "label", "value" })
    public static class AttributeItemDTO {
        private String label;
        private String value;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String keyType;

        public AttributeItemDTO() {
        }

        public AttributeItemDTO(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public String getKeyType() {
			return keyType;
		}

		public void setKeyType(String keyType) {
			this.keyType = keyType;
		}
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonPropertyOrder({ "label", "value", "type", "staticValue" })
    public static class TokenSettingDTO {
        private String label;
        private String value;
        private String staticValue;
        private String type;
        
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String keyType;

        public TokenSettingDTO() {
        }

        public TokenSettingDTO(String label, String value, String staticValue) {
            this.label = label;
            this.value = value;
            this.staticValue = staticValue;
        }

        public TokenSettingDTO(String label, String value, String staticValue, String type) {
            this.label = label;
            this.value = value;
            this.staticValue = staticValue;
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStaticValue() {
            return staticValue;
        }

        public void setStaticValue(String staticValue) {
            this.staticValue = staticValue;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
        
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public String getKeyType() {
			return keyType;
		}

		public void setKeyType(String keyType) {
			this.keyType = keyType;
		}
    }
}
