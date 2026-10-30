package com.serviceplus.metadata.pattern.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO.ChecksumConfig;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO.Component;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO.PatternConfig;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO.ResetAttr;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO.ResetConfig;
import com.serviceplus.metadata.pattern.dto.PatternGenerationConfigDTO.TokenConfig;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO.AlphanumericConfigDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO.PartConfigDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO.PatternItemDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO.SequenceConfigDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO.TokenSettingDTO;
import com.serviceplus.metadata.pattern.dto.ServicePatternConfigurationDTO;
import com.serviceplus.metadata.pattern.dto.ServicePatternConfigurationDTO.AttributeMappingDTO;
import com.serviceplus.metadata.pattern.entity.PatternConfiguration;
import com.serviceplus.metadata.pattern.entity.ServicePatternConfiguration;
import com.serviceplus.metadata.pattern.enums.ResetAttribute;
import com.serviceplus.metadata.pattern.enums.Separator;
import com.serviceplus.metadata.pattern.repository.PatternConfigurationRepository;
import com.serviceplus.metadata.pattern.repository.ServicePatternConfigurationRepository;

@Service
public class PatternGenerationConfigService {

	private static final Logger logger = LoggerFactory.getLogger(PatternGenerationConfigService.class);

	private static final Set<String> UPPERCASE = Set.of("PREFIX", "SUFFIX", "SERVICE_CODE", "SPGD_ENTITY",
			"SERVICE_ATTRIBUTE");

	private final ServicePatternConfigurationRepository repository;
	private final PatternConfigurationRepository patternRepository;
	private final ObjectMapper objectMapper;

	@Value("${pattern.checksum.enabled:true}")
	private boolean checksumEnabled;

	@Value("${pattern.checksum.algorithm:LUHN}")
	private String checksumAlgorithm;

	@Value("${pattern.checksum.length:1}")
	private int checksumLength;

	public PatternGenerationConfigService(ServicePatternConfigurationRepository repository,
			PatternConfigurationRepository patternRepository, ObjectMapper objectMapper) {
		this.repository = repository;
		this.patternRepository = patternRepository;
		this.objectMapper = objectMapper;
	}

	public Set<String> buildPaymentConfiguration(Long serviceId, UserSessionDTO userSessionDetails) {
		return buildPatternGenerationConfig(serviceId, userSessionDetails).getUsedAttributeList()
				.getPatternGenerationUsedAttrList();
	}

	@Transactional(readOnly = true)
	public PatternGenerationConfigDTO buildPatternGenerationConfig(Long serviceId, UserSessionDTO userSessionDetails) {
		PatternGenerationConfigDTO result = new PatternGenerationConfigDTO();
		Set<String> used = result.getUsedAttributeList().getPatternGenerationUsedAttrList();

		List<ServicePatternConfiguration> mappings = loadMappings(serviceId, userSessionDetails);
		if (mappings.isEmpty()) {
			return result;
		}

		Set<Long> patternIds = mappings.stream().map(ServicePatternConfiguration::getPatternId).filter(Objects::nonNull)
				.collect(Collectors.toSet());
		Map<Long, PatternConfiguration> templates = patternRepository.findAllById(patternIds).stream()
				.collect(Collectors.toMap(PatternConfiguration::getId, Function.identity(), (a, b) -> a));

		for (ServicePatternConfiguration sp : mappings) {
			PatternConfiguration template = templates.get(sp.getPatternId());
			PatternTemplateDTO tpl = template == null ? null
					: convert(template.getConfigJson(), PatternTemplateDTO.class);
			if (tpl == null) {
				logger.warn("Pattern template {} missing/unreadable for service {}", sp.getPatternId(), serviceId);
				continue;
			}
			ServicePatternConfigurationDTO spDto = convert(sp.getConfigJson(), ServicePatternConfigurationDTO.class);

			List<AttributeMappingDTO> maps = spDto != null && spDto.getAttributeMappings() != null
					? spDto.getAttributeMappings()
					: List.of();

			List<Component> components = buildComponents(tpl, maps, used);
			boolean hasChecksum = components.stream().anyMatch(c -> "{CHECKSUM}".equals(c.token()));

			result.getPatternConfigurations().add(new PatternConfig("Pattern_" + sp.getPatternId(), sp.getServiceId(),
					spDto != null && Boolean.TRUE.equals(spDto.getIsApplicationRefNo()), components,
					buildTokenConfig(tpl.getPrefix(), "prefixConfig", "PREFIX", maps, used),
					buildTokenConfig(tpl.getSuffix(), "suffixConfig", "SUFFIX", maps, used),
					buildSequenceConfig(tpl.getSequenceConfig()), buildResetConfig(tpl, maps, used),
					hasChecksum ? new ChecksumConfig(checksumEnabled, checksumAlgorithm, checksumLength) : null));
		}
		return result;
	}

	private List<ServicePatternConfiguration> loadMappings(Long serviceId, UserSessionDTO user) {
		String tenantId = user != null ? user.getTenantId() : null;
		List<ServicePatternConfiguration> list = List.of();
		if (tenantId != null && !tenantId.isEmpty()) {
			list = repository.findAllByServiceIdAndTenantId(serviceId, tenantId);
		}
		return list.isEmpty() ? repository.findAllByServiceId(serviceId) : list;
	}

	private <T> T convert(Object source, Class<T> type) {
		if (source == null) {
			return null;
		}
		if (type.isInstance(source)) {
			return type.cast(source);
		}
		try {
			return objectMapper.convertValue(source, type);
		} catch (IllegalArgumentException ex) {
			logger.error("Cannot convert configJson to {}", type.getSimpleName(), ex);
			return null;
		}
	}

	private List<Component> buildComponents(PatternTemplateDTO tpl, List<AttributeMappingDTO> maps, Set<String> used) {
		List<Component> out = new ArrayList<>();
		if (tpl.getPattern() == null) {
			return out;
		}
		List<PatternItemDTO> items = new ArrayList<>(tpl.getPattern());
		items.sort(Comparator.comparing(PatternItemDTO::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())));

		for (PatternItemDTO item : items) {
			String value = item.getValue();
			if (value == null || value.isBlank()) {
				continue;
			}
			int sequence = item.getSortOrder() != null ? item.getSortOrder() : out.size() + 1;
			Separator separator = asSeparator(value);

			if (separator != null) {
				out.add(new Component(sequence, "SEPARATOR", separator.getSymbol(), separator.getSymbol(), null, null,
						null));
			} else {
				AttributeMappingDTO m = find(maps, "components", value, "{" + value + "}");
				out.add(new Component(sequence, "VARIABLE", "{" + value + "}", null, true, mappedValue(m, used),
						UPPERCASE.contains(value) ? "UPPERCASE" : null));
			}
		}
		return out;
	}

	private Separator asSeparator(String v) {
		try {
			return Separator.fromValue(v);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private TokenConfig buildTokenConfig(TokenSettingDTO t, String keyType, String defaultKey,
			List<AttributeMappingDTO> maps, Set<String> used) {
		if (t == null) {
			return new TokenConfig("STATIC", "", "", "");
		}
		boolean dynamic = "DYNAMIC".equalsIgnoreCase(t.getType());
		String staticValue = dynamic ? "" : nz(t.getValue()).isEmpty() ? nz(t.getStaticValue()) : t.getValue();

		AttributeMappingDTO m = find(maps, keyType, t.getValue(), defaultKey);
		String mappedAttribute = "";
		String systemVariable = "";
		if (m != null && m.getMappedAttrValue() != null) {
			if ("F".equalsIgnoreCase(m.getMappedAttrType())) {
				mappedAttribute = m.getMappedAttrValue();
				used.add(mappedAttribute);
			} else if ("S".equalsIgnoreCase(m.getMappedAttrType())) {
				systemVariable = m.getMappedAttrValue();
			}
		}
		return new TokenConfig(dynamic ? "DYNAMIC" : "STATIC", staticValue, mappedAttribute, systemVariable);
	}

	private Map<String, Object> buildSequenceConfig(SequenceConfigDTO s) {
		Map<String, Object> m = PatternGenerationConfigDTO.newSequenceMap();
		if (s == null || s.getSeriesType() == null) {
			return m;
		}
		m.put("seriesType", s.getSeriesType());

		switch (s.getSeriesType().toUpperCase()) {
		case "NUMERIC" -> {
			var n = s.getNumeric();
			if (n == null) {
				break;
			}
			int digits = n.getNumberOfDigits() == null ? 0 : n.getNumberOfDigits();
			m.put("digits", digits);
			m.put("minValue", n.getStartValue());
			m.put("maxValue", digits > 0 ? (long) Math.pow(10, digits) - 1 : null);
			m.put("startingValue", n.getStartValue());
			m.put("increment", n.getIncrementBy());
			m.put("padding", false);
			m.put("paddingChar", "");
		}
		case "ALPHABETIC" -> {
			var a = s.getAlphabetic();
			if (a == null) {
				break;
			}
			m.put("alphabeticLength", a.getNumberOfAlphabets());
			m.put("startingAlphabeticValue", a.getStartingAlphabet());
			m.put("alphabeticIncrement", a.getIncrementBy());
		}
		case "ALPHANUMERIC" -> {
			AlphanumericConfigDTO an = s.getAlphanumeric();
			if (an == null) {
				break;
			}
			PartConfigDTO numeric = partOfType(an, "NUMERIC");
			PartConfigDTO alphabetic = partOfType(an, "ALPHABETIC");

			m.put("startingPattern", an.getStartingPattern());
			if (alphabetic != null) {
				m.put("alphabeticPartLength", alphabetic.getSize());
			}
			if (numeric != null) {
				int size = numeric.getSize() == null ? 0 : numeric.getSize();
				m.put("numericPartLength", numeric.getSize());
				m.put("minValue", toLong(numeric.getValue()));
				m.put("maxValue", size > 0 ? (long) Math.pow(10, size) - 1 : null);
			}
			m.put("incrementStep", an.getIncrementBy());
			m.put("totalLength", sizeOf(an.getPartOne()) + sizeOf(an.getPartTwo()));
		}
		default -> logger.warn("Unknown seriesType {}", s.getSeriesType());
		}
		return m;
	}

	private PartConfigDTO partOfType(AlphanumericConfigDTO an, String type) {
		for (PartConfigDTO p : new PartConfigDTO[] { an.getPartOne(), an.getPartTwo() }) {
			if (p != null && type.equalsIgnoreCase(p.getType())) {
				return p;
			}
		}
		return null;
	}

	private int sizeOf(PartConfigDTO p) {
		return p == null || p.getSize() == null ? 0 : p.getSize();
	}

	private Long toLong(String v) {
		try {
			return v == null ? null : Long.valueOf(v.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private ResetConfig buildResetConfig(PatternTemplateDTO tpl, List<AttributeMappingDTO> maps, Set<String> used) {
		var reset = tpl.getSequenceReset();
		boolean enabled = reset != null && Boolean.TRUE.equals(reset.getEnabled());
		List<ResetAttr> attrs = new ArrayList<>();

		if (enabled && reset.getAttributes() != null) {
			for (var a : reset.getAttributes()) {
				if (a.getValue() == null || a.getValue().isBlank()) {
					continue;
				}
				AttributeMappingDTO m = find(maps, "resetConfig", a.getValue());
				attrs.add(new ResetAttr("{" + resetToken(a.getValue()) + "}", mappedValue(m, used)));
			}
		}
		return new ResetConfig(enabled, "NEVER", attrs);
	}

	private String resetToken(String value) {
		try {
			return switch (ResetAttribute.fromValue(value)) {
			case CALENDAR_YEAR -> "YYYY";
			case FINANCIAL_YEAR -> "FY";
			case SPGD_ENTITY -> "SPGD_ENTITY";
			case SERVICE_ATTRIBUTE_1, SERVICE_ATTRIBUTE_2, SERVICE_ATTRIBUTE_3 -> "SERVICE_ATTRIBUTE";
			};
		} catch (IllegalArgumentException e) {
			return value;
		}
	}

	private AttributeMappingDTO find(List<AttributeMappingDTO> maps, String keyType, String... keyValues) {
		for (AttributeMappingDTO m : maps) {
			if (!keyType.equalsIgnoreCase(m.getKeyType())) {
				continue;
			}
			for (String kv : keyValues) {
				if (kv != null && kv.equalsIgnoreCase(m.getKeyValue())) {
					return m;
				}
			}
		}
		return null;
	}

	private String mappedValue(AttributeMappingDTO m, Set<String> used) {
		if (m == null || m.getMappedAttrValue() == null || m.getMappedAttrValue().isBlank()) {
			return null;
		}
		if (!"S".equalsIgnoreCase(m.getMappedAttrType())) {
			used.add(m.getMappedAttrValue());
		}
		return m.getMappedAttrValue();
	}

	private String nz(String s) {
		return s == null ? "" : s;
	}
}