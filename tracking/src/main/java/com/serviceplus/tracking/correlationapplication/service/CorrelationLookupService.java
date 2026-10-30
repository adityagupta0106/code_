package com.serviceplus.tracking.correlationapplication.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.tracking.correlationapplication.dto.CorrelationLookupRequest;
import com.serviceplus.tracking.correlationapplication.dto.CorrelationLookupResponse;
import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrData;
import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrFieldsetData;
import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrIdentifierData;
import com.serviceplus.tracking.correlationapplication.repository.ApplicationAttrDataRepository;
import com.serviceplus.tracking.correlationapplication.repository.ApplicationAttrFieldsetDataRepository;
import com.serviceplus.tracking.correlationapplication.repository.ApplicationAttrIdentifierDataRepository;
import com.serviceplus.tracking.correlationapplication.util.CorrelationHashUtil;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationAttrMapping;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationMapping;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationSet;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationAttrMappingRepository;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationMappingRepository;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationSetRepository;

@Service
public class CorrelationLookupService {

	private static final Logger LOGGER = LogManager.getLogger(CorrelationLookupService.class);

	private static final String ATTR_SOURCE_FORM = "F";
	private static final String SET_TYPE_INPUT = "I";
	private static final String MAPPING_TYPE_INPUT = "I";
	private static final String MAPPING_TYPE_OUTPUT = "O";
	private static final int MAX_OUTPUT_ATTRS = 20;
	private static final int MAX_IDENTIFIER_ATTRS = 5;

	private final ServiceCorrelationMappingRepository mappingRepository;
	private final ServiceCorrelationAttrMappingRepository attrMappingRepository;
	private final ServiceCorrelationSetRepository setRepository;
	private final ApplicationAttrIdentifierDataRepository identifierRepository;
	private final ApplicationAttrDataRepository attrDataRepository;
	private final ApplicationAttrFieldsetDataRepository fieldsetRepository;
	private final ObjectMapper objectMapper;

	public CorrelationLookupService(ServiceCorrelationMappingRepository mappingRepository,
			ServiceCorrelationAttrMappingRepository attrMappingRepository,
			ServiceCorrelationSetRepository setRepository, ApplicationAttrIdentifierDataRepository identifierRepository,
			ApplicationAttrDataRepository attrDataRepository, ApplicationAttrFieldsetDataRepository fieldsetRepository,
			ObjectMapper objectMapper) {
		this.mappingRepository = mappingRepository;
		this.attrMappingRepository = attrMappingRepository;
		this.setRepository = setRepository;
		this.identifierRepository = identifierRepository;
		this.attrDataRepository = attrDataRepository;
		this.fieldsetRepository = fieldsetRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional(readOnly = true)
	public CorrelationLookupResponse lookup(CorrelationLookupRequest request) {

		CorrelationLookupResponse response = new CorrelationLookupResponse();

		if (request.getServiceId() == null || request.getTaskId() == null) {
			return response;
		}

		Map<String, Object> formData = request.getFormData() == null ? new LinkedHashMap<>() : request.getFormData();

		List<ServiceCorrelationMapping> mappings = mappingRepository
				.findByServiceIdAndTaskIdOrderByPriorityAsc(request.getServiceId(), request.getTaskId());

		if (mappings.isEmpty()) {
			LOGGER.info("No correlation mapping. serviceId={}, taskId={}", request.getServiceId(), request.getTaskId());
			return response;
		}

		Map<Long, List<ServiceCorrelationAttrMapping>> attrMappingsBySet = new LinkedHashMap<>();
		Set<String> pendingOutputs = new HashSet<>();

		for (ServiceCorrelationMapping m : mappings) {
			List<ServiceCorrelationAttrMapping> attrMappings = attrMappingRepository
					.findByServiceIdAndTaskIdAndMappedBaseServiceIdAndMappedSetId(request.getServiceId(),
							request.getTaskId(), m.getMappedBaseServiceId(), m.getMappedSetId())
					.stream().filter(a -> Objects.equals(a.getOutputSetId(), m.getOutputSetId()))
					.collect(Collectors.toList());

			attrMappingsBySet.put(m.getMappedSetId(), attrMappings);

			attrMappings.stream().filter(a -> MAPPING_TYPE_OUTPUT.equals(a.getMappingType()))
					.forEach(a -> pendingOutputs.add(extractFieldKey(a.getAttrId())));
		}

		for (ServiceCorrelationMapping mapping : mappings) {

			if (pendingOutputs.isEmpty()) {
				break;
			}

			processMapping(mapping, attrMappingsBySet.get(mapping.getMappedSetId()), formData, response,
					pendingOutputs);

			if (!Boolean.TRUE.equals(mapping.getSearchNext())) {
				LOGGER.info("search_next is false for setId={}. Stopping.", mapping.getMappedSetId());
				break;
			}
		}

		return response;
	}

	private void processMapping(ServiceCorrelationMapping mapping, List<ServiceCorrelationAttrMapping> attrMappings,
			Map<String, Object> formData, CorrelationLookupResponse response, Set<String> pendingOutputs) {

		Long baseId = mapping.getMappedBaseServiceId();
		Long setId = mapping.getMappedSetId();

		List<ServiceCorrelationSet> inputCfgs = setRepository
				.findByBaseServiceIdAndSetIdAndSourceType(baseId, setId, mapping.getSourceType()).stream()
				.filter(r -> SET_TYPE_INPUT.equals(r.getSetType()))
				.sorted(Comparator.comparing(ServiceCorrelationSet::getAttrSortOrder,
						Comparator.nullsLast(Integer::compareTo)))
				.limit(MAX_IDENTIFIER_ATTRS).collect(Collectors.toList());

		if (inputCfgs.isEmpty()) {
			LOGGER.warn("No input attributes configured. baseServiceId={}, setId={}", baseId, setId);
			return;
		}

		List<String> inputValues = new ArrayList<>();

		for (ServiceCorrelationSet cfg : inputCfgs) {

			Optional<ServiceCorrelationAttrMapping> map = attrMappings.stream()
					.filter(a -> MAPPING_TYPE_INPUT.equals(a.getMappingType()))
					.filter(a -> Objects.equals(a.getMappedAttrId(), cfg.getAttrId())).findFirst();

			if (!map.isPresent()) {
				LOGGER.warn("No input mapping for set attr {}. Skipping setId={}", cfg.getAttrId(), setId);
				return;
			}

			String driveKey = extractFieldKey(map.get().getAttrId());
			inputValues.add(findFormValue(driveKey, formData));
		}

		long hash = CorrelationHashUtil.computeAttrHash(inputValues);

		LOGGER.info("Correlation lookup. baseServiceId={}, setId={}, values={}, hash={}", baseId, setId, inputValues, hash);

		List<ApplicationAttrIdentifierData> candidates = identifierRepository
				.findByBaseServiceIdAndSetIdAndAttrHash(baseId, setId, hash).stream()
				.filter(c -> valuesMatch(c, inputValues)).collect(Collectors.toList());

		if (candidates.isEmpty()) {
			LOGGER.info("No application matched. baseServiceId={}, setId={}", baseId, setId);
			return;
		}

		ApplicationAttrIdentifierData chosen = pickApplication(candidates);
		String applicationId = chosen.getApplicationId();

		String outputSetId = mapping.getOutputSetId();

		Map<String, String> attrDataMap = new LinkedHashMap<>();
		for (ApplicationAttrData row : attrDataRepository
				.findByApplicationIdAndSetIdAndOutputSetIdOrderByIdAsc(applicationId, setId, outputSetId)) {
			attrDataMap.putAll(readPairs(row));
		}

		List<Map<String, String>> fieldsetRows = new ArrayList<>();
		for (ApplicationAttrFieldsetData row : fieldsetRepository
				.findByApplicationIdAndSetIdAndOutputSetIdOrderByRowNoAsc(applicationId, setId, outputSetId)) {
			fieldsetRows.add(readPairs(row));
		}

		Set<String> fieldsetKeys = new HashSet<>();
		fieldsetRows.forEach(r -> fieldsetKeys.addAll(r.keySet()));

		String fieldsetParentKey = null;

		for (ServiceCorrelationAttrMapping a : attrMappings) {
			if (MAPPING_TYPE_OUTPUT.equals(a.getMappingType())
					&& fieldsetKeys.contains(extractFieldKey(a.getMappedAttrId()))) {
				fieldsetParentKey = findDynamicRowParentKey(extractFieldKey(a.getAttrId()), formData);
				if (fieldsetParentKey != null) {
					break;
				}
			}
		}

		List<Map<String, Object>> driveRows = new ArrayList<>();
		for (int i = 0; i < fieldsetRows.size(); i++) {
			driveRows.add(new LinkedHashMap<>());
		}

		for (ServiceCorrelationAttrMapping a : attrMappings) {

			if (!MAPPING_TYPE_OUTPUT.equals(a.getMappingType())) {
				continue;
			}

			String driveKey = extractFieldKey(a.getAttrId());
			String primaryKey = extractFieldKey(a.getMappedAttrId());

			if (!pendingOutputs.contains(driveKey)) {
				continue;
			}

			if (fieldsetKeys.contains(primaryKey)) {
				for (int i = 0; i < fieldsetRows.size(); i++) {
					driveRows.get(i).put(driveKey, fieldsetRows.get(i).get(primaryKey));
				}
				pendingOutputs.remove(driveKey);

			} else if (attrDataMap.containsKey(primaryKey)) {
				response.getValues().put(driveKey, attrDataMap.get(primaryKey));
				pendingOutputs.remove(driveKey);
			}
		}

		if (!driveRows.isEmpty() && driveRows.stream().anyMatch(r -> !r.isEmpty())) {
			if (fieldsetParentKey != null) {
				response.getValues().put(fieldsetParentKey, driveRows);
			} else {
				LOGGER.warn("Dynamic row parent not found in formData for outputSetId={}", outputSetId);
				response.getFieldsets().put(outputSetId, driveRows);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String findDynamicRowParentKey(String fieldKey, Map<String, Object> formData) {

		for (Map.Entry<String, Object> e : formData.entrySet()) {
			Object v = e.getValue();
			if (v instanceof List && !((List<?>) v).isEmpty() && ((List<?>) v).get(0) instanceof Map) {
				Map<String, Object> first = (Map<String, Object>) ((List<?>) v).get(0);
				if (first.containsKey(fieldKey)) {
					return e.getKey();
				}
			}
		}

		return null;
	}

	private ApplicationAttrIdentifierData pickApplication(List<ApplicationAttrIdentifierData> candidates) {

		if (candidates.size() > 1) {
			LOGGER.warn("{} applications matched; picking the most recent. Implement ordering here.",
					candidates.size());
		}

		return candidates.stream().max(Comparator.comparing(ApplicationAttrIdentifierData::getCrDate,
				Comparator.nullsFirst(Comparator.naturalOrder()))).orElse(candidates.get(0));
	}

	private String extractFieldKey(String attrId) {
		if (attrId != null && attrId.contains("$")) {
			String[] parts = attrId.split("\\$");
			return parts[parts.length - 1];
		}
		return attrId;
	}

	private Map<String, String> readPairs(Object row) {

		Map<String, String> out = new LinkedHashMap<>();
		BeanWrapper wrapper = new BeanWrapperImpl(row);

		for (int i = 1; i <= MAX_OUTPUT_ATTRS; i++) {
			Object key = wrapper.getPropertyValue("attr" + i);
			if (key == null || String.valueOf(key).trim().isEmpty()) {
				continue;
			}
			Object value = wrapper.getPropertyValue("attr" + i + "Value");
			out.put(String.valueOf(key), value == null ? null : String.valueOf(value));
		}

		return out;
	}

	private boolean valuesMatch(ApplicationAttrIdentifierData entity, List<String> expected) {

		BeanWrapper wrapper = new BeanWrapperImpl(entity);

		for (int i = 0; i < MAX_IDENTIFIER_ATTRS; i++) {
			String exp = i < expected.size() ? normalize(expected.get(i)) : "";
			Object act = wrapper.getPropertyValue("attr" + (i + 1) + "Value");
			if (!exp.equals(normalize(act == null ? null : String.valueOf(act)))) {
				return false;
			}
		}

		return true;
	}

	private String normalize(String v) {
		return v == null ? "" : v.trim();
	}

	@SuppressWarnings("unchecked")
	private String findFormValue(String fieldKey, Map<String, Object> formData) {

		if (formData.containsKey(fieldKey)) {
			return extractScalarFieldValue(formData.get(fieldKey));
		}

		for (Object v : formData.values()) {
			if (v instanceof List && !((List<?>) v).isEmpty() && ((List<?>) v).get(0) instanceof Map) {
				Map<String, Object> first = (Map<String, Object>) ((List<?>) v).get(0);
				if (first.containsKey(fieldKey)) {
					return extractScalarFieldValue(first.get(fieldKey));
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private String extractScalarFieldValue(Object rawValue) {

		if (rawValue == null) {
			return null;
		}

		if (rawValue instanceof String || rawValue instanceof Number || rawValue instanceof Boolean) {
			return String.valueOf(rawValue);
		}

		if (rawValue instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) rawValue;

			if (map.containsKey("value")) {
				Object v = map.get("value");
				return v == null ? null : String.valueOf(v);
			}

			if (map.containsKey("uploadId")) {
				try {
					return objectMapper.writeValueAsString(map);
				} catch (JsonProcessingException e) {
					throw new IllegalArgumentException("Unable to convert attribute value to JSON", e);
				}
			}

			return null;
		}

		if (rawValue instanceof List) {
			StringBuilder sb = new StringBuilder();
			for (Object item : (List<?>) rawValue) {
				if (item instanceof Map) {
					Object v = ((Map<?, ?>) item).get("value");
					if (v != null) {
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append(v);
					}
				}
			}
			return sb.length() > 0 ? sb.toString() : null;
		}

		return String.valueOf(rawValue);
	}
}