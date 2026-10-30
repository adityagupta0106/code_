package com.serviceplus.metadata.pattern.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

public class PatternGenerationConfigDTO {

	private UsedAttributeList usedAttributeList = new UsedAttributeList();
	private List<PatternConfig> patternConfigurations = new ArrayList<>();

	public UsedAttributeList getUsedAttributeList() {
		return usedAttributeList;
	}

	public void setUsedAttributeList(UsedAttributeList usedAttributeList) {
		this.usedAttributeList = usedAttributeList;
	}

	public List<PatternConfig> getPatternConfigurations() {
		return patternConfigurations;
	}

	public void setPatternConfigurations(List<PatternConfig> patternConfigurations) {
		this.patternConfigurations = patternConfigurations;
	}

	public static class UsedAttributeList {
		private Set<String> patternGenerationUsedAttrList = new LinkedHashSet<>();

		public Set<String> getPatternGenerationUsedAttrList() {
			return patternGenerationUsedAttrList;
		}

		public void setPatternGenerationUsedAttrList(Set<String> list) {
			this.patternGenerationUsedAttrList = list;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record PatternConfig(String patternId, Long serviceId, Boolean isArnPattern, List<Component> components,
			TokenConfig prefixConfig, TokenConfig suffixConfig, Map<String, Object> sequenceConfig,
			ResetConfig resetConfig, ChecksumConfig checksumConfig) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record Component(Integer sequence, String type, String token, String value, Boolean mandatory,
			String mappedAttribute, String transformation) {
	}

	public record TokenConfig(String sourceMode, String staticValue, String mappedAttribute, String systemVariable) {
	}

	public record ResetConfig(Boolean resetEnabled, String resetFrequency, List<ResetAttr> resetAttributes) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ResetAttr(String token, String mappedAttribute) {
	}

	public record ChecksumConfig(Boolean enabled, String algorithm, Integer length) {
	}

	public static Map<String, Object> newSequenceMap() {
		return new LinkedHashMap<>();
	}
}