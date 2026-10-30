package com.serviceplus.tracking.correlationapplication.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrData;
import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrFieldsetData;
import com.serviceplus.tracking.correlationapplication.entity.ApplicationAttrIdentifierData;
import com.serviceplus.tracking.correlationapplication.entity.ApplicationSetOrderAttrData;
import com.serviceplus.tracking.correlationapplication.repository.ApplicationAttrDataRepository;
import com.serviceplus.tracking.correlationapplication.repository.ApplicationAttrFieldsetDataRepository;
import com.serviceplus.tracking.correlationapplication.repository.ApplicationAttrIdentifierDataRepository;
import com.serviceplus.tracking.correlationapplication.util.CorrelationHashUtil;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationSet;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationSetOrder;
import com.serviceplus.tracking.correlationmapping.repository.ApplicationSetOrderAttrDataRepository;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationSetOrderRepository;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationSetRepository;
import com.serviceplus.tracking.dto.ApplicationCurrentProcess;
import com.serviceplus.tracking.dto.FormDataEvent;
import com.serviceplus.tracking.dto.UserInboxKafkaDTO;

@Service
public class CorrelationSetFormDataService {

	private static final Logger LOGGER = LogManager.getLogger(CorrelationSetFormDataService.class);

	private static final String SOURCE_TYPE_SERVICE = "S";
	private static final String ATTR_SOURCE_FORM = "F";
	private static final String ATTR_SOURCE_SYSTEM = "S";
	private static final String SET_TYPE_INPUT = "I";
	private static final String SET_TYPE_OUTPUT = "O";

	private static final int MAX_IDENTIFIER_ATTRS = 5;
	private static final int MAX_OUTPUT_ATTRS = 20;

	private static final int MAX_ORDER_ATTRS = 10;
	private static final String SYS_PREFIX = "Sys_";

	private final ServiceCorrelationSetRepository correlationSetRepository;
	private final ServiceCorrelationSetOrderRepository correlationSetOrderRepository;
	private final ApplicationAttrIdentifierDataRepository applicationAttrIdentifierDataRepository;
	private final ApplicationAttrDataRepository applicationAttrDataRepository;
	private final ApplicationAttrFieldsetDataRepository applicationAttrFieldsetDataRepository;
	private final ApplicationSetOrderAttrDataRepository applicationSetOrderAttrDataRepository;

	private final ObjectMapper objectMapper;

	public CorrelationSetFormDataService(ServiceCorrelationSetRepository correlationSetRepository,
			ApplicationAttrIdentifierDataRepository applicationAttrIdentifierDataRepository,
			ApplicationAttrDataRepository applicationAttrDataRepository,
			ServiceCorrelationSetOrderRepository correlationSetOrderRepository,
			ApplicationSetOrderAttrDataRepository applicationSetOrderAttrDataRepository,
			ApplicationAttrFieldsetDataRepository applicationAttrFieldsetDataRepository, ObjectMapper objectMapper) {
		super();
		this.correlationSetRepository = correlationSetRepository;
		this.applicationAttrIdentifierDataRepository = applicationAttrIdentifierDataRepository;
		this.applicationAttrDataRepository = applicationAttrDataRepository;
		this.applicationAttrFieldsetDataRepository = applicationAttrFieldsetDataRepository;
		this.correlationSetOrderRepository = correlationSetOrderRepository;
		this.applicationSetOrderAttrDataRepository = applicationSetOrderAttrDataRepository;
		this.objectMapper = objectMapper;
	}

	public void saveSystemVariable(UserInboxKafkaDTO kafkaDTO) {

		if (kafkaDTO == null || kafkaDTO.getProcessList() == null || kafkaDTO.getProcessList().isEmpty()) {
			LOGGER.info("Skipping system variable save. Process list is empty.");
			return;
		}

		ApplicationCurrentProcess process = kafkaDTO.getProcessList().getFirst();
		String applicationId = process.getApplicationId();
		String kafkaTaskId = process.getCurrentTask();
		Long serviceId = null;

		try {
			serviceId = process.getServiceId().longValue();

			LOGGER.info("Saving system variables. applicationId={}, serviceId={}, taskId={}", applicationId,
					serviceId, kafkaTaskId);

			List<ServiceCorrelationSet> configs = getServiceCorrelationSetsByServiceId(serviceId);
			if (configs == null || configs.isEmpty()) {
				LOGGER.info("No system variable configuration found. applicationId={}, serviceId={}", applicationId,
						serviceId);
				return;
			}

			Map<String, Object> sysVariableValue = new LinkedHashMap<>();

			for (ServiceCorrelationSet cfg : configs) {
				String attrId = cfg.getAttrId();
				if (attrId == null || sysVariableValue.containsKey(attrId)) {
					continue;
				}

				Object value = resolveSysValue(attrId, cfg.getTaskId(), kafkaDTO);

				if (value != null) {
					sysVariableValue.put(attrId, value);
				} else {
					LOGGER.debug("No value resolved for system variable. applicationId={}, attrId={}, cfgTaskId={}",
							applicationId, attrId, cfg.getTaskId());
				}
			}

			if (sysVariableValue.isEmpty()) {
				LOGGER.info("No system variable values resolved. applicationId={}, serviceId={}, taskId={}",
						applicationId, serviceId, kafkaTaskId);
				return;
			}

			LOGGER.info("Resolved system variables. applicationId={}, keys={}", applicationId,
					sysVariableValue.keySet());

			saveSystemVariable(serviceId, applicationId, kafkaTaskId, sysVariableValue);

			LOGGER.info("System variables saved. applicationId={}, serviceId={}, taskId={}", applicationId,
					serviceId, kafkaTaskId);

		} catch (Exception e) {
			LOGGER.error("Failed to save system variables. applicationId={}, serviceId={}, taskId={}", applicationId,
					serviceId, kafkaTaskId, e);
		}
	}

	private Object resolveSysValue(String sysKey, String cfgTaskId, UserInboxKafkaDTO kafkaDTO) {

		ApplicationCurrentProcess process = kafkaDTO.getProcessList().getFirst();
	    boolean sameTask = cfgTaskId != null && cfgTaskId.equals(process.getCurrentTask());

	    switch (sysKey) {
	        case "Sys_1":                                                        // Application Date
	            return formatDate(kafkaDTO.getApplyDate());
	
	        case "Sys_2":                                                        // Service Completion Date
	            return kafkaDTO.isCompleteClosure() ? formatDate(LocalDateTime.now()) : null;
	
	        case "Sys_3":                                                        // Application Ref No
	            return kafkaDTO.getApplicationRefNo();
	
	        case "Sys_1340":                                                     // Application Received On
	            return sameTask ? formatDate(process.getInitiatedOn()) : null;
	
	        case "Sys_1342":                                                     // Action On Date
	            return sameTask ? formatDate(process.getActionOn()) : null;
	
	        case "Sys_2065":                                                     // Remarks
	            return sameTask ? null : null;
	        default:
	            return null;                                                     // add other Sys_xx keys here
	    }
	}

	private String formatDate(Object date) {
		if (date == null) {
			return null;
		}
		if (date instanceof Date) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) date); // adjust pattern
		}
		return String.valueOf(date);
	}

	public List<ServiceCorrelationSet> getServiceCorrelationSetsByServiceId(Long serviceId) {
		List<ServiceCorrelationSet> serviceCorrelationSets = correlationSetRepository
				.findByServiceIdAndSourceTypeAndAttrSource(serviceId, SOURCE_TYPE_SERVICE, ATTR_SOURCE_SYSTEM);
		return serviceCorrelationSets;
	}

	@Transactional
	public void saveSystemVariable(Long serviceId, String applicationId, String taskId,
			Map<String, Object> sysVariableValue) {

		if (serviceId == null || applicationId == null || sysVariableValue == null || sysVariableValue.isEmpty()) {
			LOGGER.info("Skipping system variable save. serviceId={}, applicationId={}", serviceId, applicationId);
			return;
		}

		Map<String, String> sys = new HashMap<>();
		sysVariableValue.forEach((k, v) -> sys.put(k, v == null ? null : String.valueOf(v)));

		List<Long> setIds = correlationSetRepository
				.findByServiceIdAndSourceTypeAndAttrSource(serviceId, SOURCE_TYPE_SERVICE, ATTR_SOURCE_SYSTEM).stream()
				.map(ServiceCorrelationSet::getSetId).distinct().collect(Collectors.toList());

		for (Long setId : setIds) {
			List<ServiceCorrelationSet> records = correlationSetRepository.findByServiceIdAndSetId(serviceId, setId);
			if (records.isEmpty()) {
				continue;
			}
			applySystemToIdentifier(setId, records, applicationId, sys);
			applySystemToOutputs(setId, records, applicationId, taskId, sys);
			saveSetOrderAttrData(serviceId, setId, applicationId, taskId, records.get(0), systemResolver(sys));
		}
	}

	private void applySystemToIdentifier(Long setId, List<ServiceCorrelationSet> records, String applicationId,
			Map<String, String> sys) {

		List<ServiceCorrelationSet> inputCfgs = records
				.stream().filter(r -> SET_TYPE_INPUT.equals(r.getSetType())).sorted(Comparator
						.comparing(ServiceCorrelationSet::getAttrSortOrder, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList());

		if (inputCfgs.stream().noneMatch(c -> ATTR_SOURCE_SYSTEM.equals(c.getAttrSource()))) {
			return;
		}

		ServiceCorrelationSet anyRecord = records.get(0);

		ApplicationAttrIdentifierData entity = applicationAttrIdentifierDataRepository
				.findByApplicationIdAndSetId(applicationId, setId).orElseGet(() -> {
					ApplicationAttrIdentifierData e = new ApplicationAttrIdentifierData();
					e.setBaseServiceId(anyRecord.getBaseServiceId());
					e.setServiceId(anyRecord.getServiceId());
					e.setApplicationId(applicationId);
					e.setSetId(setId);
					e.setTenantId(anyRecord.getTenantId());
					e.setCrDate(LocalDateTime.now());
					return e;
				});

		int size = Math.min(inputCfgs.size(), MAX_IDENTIFIER_ATTRS);
		List<String> values = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			ServiceCorrelationSet cfg = inputCfgs.get(i);
			if (ATTR_SOURCE_SYSTEM.equals(cfg.getAttrSource()) && sys.containsKey(cfg.getAttrId())) {
				values.add(sys.get(cfg.getAttrId()));
			} else {
				values.add(readProp(entity, "attr" + (i + 1) + "Value")); // keep saved form value
			}
		}

		applyIdentifierAttrValues(entity, values); // also recomputes hash
		entity.setUpDate(LocalDateTime.now());
		applicationAttrIdentifierDataRepository.save(entity);
	}

	private void applySystemToOutputs(Long setId, List<ServiceCorrelationSet> records, String applicationId,
			String taskId, Map<String, String> sys) {

		ServiceCorrelationSet anyRecord = records.get(0);

		Map<String, List<ServiceCorrelationSet>> outputGroups = records.stream()
				.filter(r -> SET_TYPE_OUTPUT.equals(r.getSetType())).filter(r -> r.getOutputSetId() != null)
				.collect(Collectors.groupingBy(ServiceCorrelationSet::getOutputSetId, LinkedHashMap::new,
						Collectors.toList()));

		for (Map.Entry<String, List<ServiceCorrelationSet>> g : outputGroups.entrySet()) {

			String outputSetId = g.getKey();
			List<ServiceCorrelationSet> sorted = g.getValue().stream()
					.sorted(Comparator.comparing(ServiceCorrelationSet::getId)).collect(Collectors.toList());

			if (sorted.stream().noneMatch(c -> ATTR_SOURCE_SYSTEM.equals(c.getAttrSource()))) {
				continue;
			}

			List<ApplicationAttrFieldsetData> fieldsetRows = applicationAttrFieldsetDataRepository
					.findByApplicationIdAndSetIdAndOutputSetId(applicationId, setId, outputSetId);

			if (!fieldsetRows.isEmpty()) {
				for (ApplicationAttrFieldsetData row : fieldsetRows) {
					fillSystemSlots(row, sorted, sys);
				}
				applicationAttrFieldsetDataRepository.saveAll(fieldsetRows);
				continue;
			}

			ApplicationAttrData row = new ApplicationAttrData();
			row.setBaseServiceId(anyRecord.getBaseServiceId());
			row.setServiceId(anyRecord.getServiceId());
			row.setApplicationId(applicationId);
			row.setSetId(setId);
			row.setOutputSetId(outputSetId);
			row.setTaskId(taskId);
			row.setTenantId(anyRecord.getTenantId());
			row.setCrDate(LocalDateTime.now());

			fillSystemSlots(row, sorted, sys);
			applicationAttrDataRepository.save(row);
		}
	}

	private void fillSystemSlots(Object row, List<ServiceCorrelationSet> sortedCfgs, Map<String, String> sys) {
		for (int i = 0; i < sortedCfgs.size() && i < MAX_OUTPUT_ATTRS; i++) {
			ServiceCorrelationSet cfg = sortedCfgs.get(i);
			if (ATTR_SOURCE_SYSTEM.equals(cfg.getAttrSource()) && sys.containsKey(cfg.getAttrId())) {
				setAttrPair(row, i + 1, cfg.getAttrId(), sys.get(cfg.getAttrId()));
			}
		}
	}

	private String readProp(Object bean, String prop) {
		Object v = new BeanWrapperImpl(bean).getPropertyValue(prop);
		return v == null ? null : String.valueOf(v);
	}

	private void saveSetOrderAttrData(Long serviceId, Long setId, String applicationId, String taskId,
			ServiceCorrelationSet anyRecord, Function<String, String> valueResolver) {

		List<ServiceCorrelationSetOrder> orderCfgs = correlationSetOrderRepository
				.findByServiceIdAndSetId(serviceId, setId).stream()
				.sorted(Comparator
						.comparing(ServiceCorrelationSetOrder::getAttrSortOrder,
								Comparator.nullsLast(Integer::compareTo))
						.thenComparing(ServiceCorrelationSetOrder::getId))
				.collect(Collectors.toList());

		if (orderCfgs.isEmpty()) {
			return;
		}

		if (orderCfgs.size() > MAX_ORDER_ATTRS) {
			LOGGER.warn("Set {} has {} order attributes but only {} are supported; extras ignored.", setId,
					orderCfgs.size(), MAX_ORDER_ATTRS);
		}

		ApplicationSetOrderAttrData entity = applicationSetOrderAttrDataRepository
				.findByApplicationIdAndSetId(applicationId, setId).orElseGet(() -> {
					ApplicationSetOrderAttrData e = new ApplicationSetOrderAttrData();
					e.setBaseServiceId(anyRecord.getBaseServiceId());
					e.setServiceId(anyRecord.getServiceId());
					e.setApplicationId(applicationId);
					e.setSetId(setId);
					e.setTenantId(anyRecord.getTenantId());
					e.setCrDate(LocalDateTime.now());
					return e;
				});

		Map<String, String> saved = new HashMap<>();
		for (int i = 1; i <= MAX_ORDER_ATTRS; i++) {
			String key = readProp(entity, "attr" + i);
			if (key != null) {
				saved.put(key, readProp(entity, "attr" + i + "Value"));
			}
		}

		for (int i = 0; i < MAX_ORDER_ATTRS; i++) {
			if (i < orderCfgs.size()) {
				String attrId = orderCfgs.get(i).getAttrId();
				String value = valueResolver.apply(attrId);
				if (value == null) {
					value = saved.get(attrId);
				}
				setAttrPair(entity, i + 1, attrId, value);
			} else {
				setAttrPair(entity, i + 1, null, null); // clear unused slots
			}
		}

		if (taskId != null) {
			entity.setTaskId(taskId);
		}
		entity.setUpDate(LocalDateTime.now());
		applicationSetOrderAttrDataRepository.save(entity);
	}

	private Function<String, String> systemResolver(Map<String, String> sys) {
		return attrId -> attrId != null && attrId.startsWith(SYS_PREFIX) ? sys.get(attrId) : null;
	}

	@Transactional
	public void processFormData(FormDataEvent event) {

		List<String> taskIds = event.getTaskIds();

		if (taskIds == null || taskIds.isEmpty()) {
			LOGGER.info("No task IDs found for applicationId={}", event.getApplicationId());
			return;
		}

		if (event.getKeys() == null || event.getKeys().isEmpty()) {

			LOGGER.info("No correlation set attributes found for applicationId={}", event.getApplicationId());

			return;
		}

		if (event.getFormData() == null || event.getFormData().isEmpty()) {

			LOGGER.info("No form data found for applicationId={}", event.getApplicationId());

			return;
		}

		Long serviceId = event.getServiceId().longValue();

		List<ServiceCorrelationSet> sourceRecords = correlationSetRepository
				.findByServiceIdAndSourceTypeAndAttrSource(serviceId, "S", "F");

		List<Long> setIds = sourceRecords.stream().map(ServiceCorrelationSet::getSetId).distinct()
				.collect(Collectors.toList());

		if (setIds.isEmpty()) {
			LOGGER.info("No correlation sets found for serviceId={}", serviceId);
			return;
		}

		for (String taskId : taskIds) {

			if (taskId == null || taskId.isBlank()) {
				continue;
			}

			LOGGER.info("Processing correlation set form data. applicationId={}, serviceId={}, taskId={}",
					event.getApplicationId(), serviceId, taskId);

			for (Long setId : setIds) {

				List<ServiceCorrelationSet> records = correlationSetRepository.findByServiceIdAndSetId(serviceId,
						setId);

				if (records.isEmpty()) {
					continue;
				}

				doCorrelationOperation(setId, records, event, taskId);
			}
		}
	}

	private void doCorrelationOperation(
	        Long setId,
	        List<ServiceCorrelationSet> records,
	        FormDataEvent event,
			String taskId) {

		Map<String, Object> formData = event.getFormData();
		String applicationId = event.getApplicationId();

		Optional<ApplicationAttrIdentifierData> existing = applicationAttrIdentifierDataRepository
				.findByApplicationIdAndSetId(applicationId, setId);

		if (existing.isPresent()) {
			ApplicationAttrIdentifierData existingRecord = existing.get();

			updateApplicationAttrIdentifierData(existingRecord, records, event, formData, taskId);

			existingRecord.setUpDate(LocalDateTime.now());
			applicationAttrIdentifierDataRepository.save(existingRecord);

		} else {
			ApplicationAttrIdentifierData newRecord = createApplicationAttrIdentifierData(setId, records, event,
					formData, taskId);

			applicationAttrIdentifierDataRepository.save(newRecord);
		}
	}

	private ApplicationAttrIdentifierData createApplicationAttrIdentifierData(Long setId,
			List<ServiceCorrelationSet> records, FormDataEvent event, Map<String, Object> formData, String taskId) {

		ServiceCorrelationSet anyRecord = records.get(0);
		FlattenedFormData flattened = flattenFormData(formData);

		ApplicationAttrIdentifierData entity = new ApplicationAttrIdentifierData();
		entity.setBaseServiceId(anyRecord.getBaseServiceId());
		entity.setServiceId(anyRecord.getServiceId());
		entity.setApplicationId(event.getApplicationId());
		entity.setSetId(setId);
		entity.setTenantId(anyRecord.getTenantId());
		entity.setCrDate(LocalDateTime.now());
		entity.setCrDate(LocalDateTime.now());

		applyIdentifierAttrValues(entity, buildIdentifierAttrValues(records, event, flattened));

		persistOutputData(setId, records, event, flattened, anyRecord, taskId);

		return entity;
	}

	private void updateApplicationAttrIdentifierData(ApplicationAttrIdentifierData entity,
			List<ServiceCorrelationSet> records, FormDataEvent event, Map<String, Object> formData, String taskId) {

		ServiceCorrelationSet anyRecord = records.get(0);
		FlattenedFormData flattened = flattenFormData(formData);

		applyIdentifierAttrValues(entity, buildIdentifierAttrValues(records, event, flattened));

		persistOutputData(entity.getSetId(), records, event, flattened, anyRecord, taskId);
	}

	private List<String> buildIdentifierAttrValues(List<ServiceCorrelationSet> records, FormDataEvent event,
			FlattenedFormData flattened) {

		List<ServiceCorrelationSet> inputCfgs = records
				.stream().filter(r -> SET_TYPE_INPUT.equals(r.getSetType())).sorted(Comparator
						.comparing(ServiceCorrelationSet::getAttrSortOrder, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList());

		if (inputCfgs.size() > MAX_IDENTIFIER_ATTRS) {
			LOGGER.warn("Set {} has {} input attributes but only {} are supported; extras ignored.",
					inputCfgs.isEmpty() ? null : inputCfgs.get(0).getSetId(), inputCfgs.size(), MAX_IDENTIFIER_ATTRS);
		}

		List<String> values = new ArrayList<>();

		for (ServiceCorrelationSet cfg : inputCfgs) {
			if (values.size() == MAX_IDENTIFIER_ATTRS) {
				break;
			}
			values.add(resolveAttrValue(cfg, event, flattened));
		}

		return values;
	}

	private void applyIdentifierAttrValues(ApplicationAttrIdentifierData entity, List<String> values) {

		entity.setAttr1Value(getOrNull(values, 0));
		entity.setAttr2Value(getOrNull(values, 1));
		entity.setAttr3Value(getOrNull(values, 2));
		entity.setAttr4Value(getOrNull(values, 3));
		entity.setAttr5Value(getOrNull(values, 4));
		LOGGER.info("computeAttrHash : applicationId :{} ,hashValue {}", entity.getApplicationId(), values);
		entity.setAttrHash(CorrelationHashUtil.computeAttrHash(values));
	}

	private String getOrNull(List<String> list, int index) {
		return index < list.size() ? list.get(index) : null;
	}

	private void persistOutputData(Long setId, List<ServiceCorrelationSet> records, FormDataEvent event,
			FlattenedFormData flattened, ServiceCorrelationSet anyRecord, String taskId) {

		Map<String, List<ServiceCorrelationSet>> outputGroups = records.stream()
				.filter(r -> SET_TYPE_OUTPUT.equals(r.getSetType())).filter(r -> r.getOutputSetId() != null)
				.collect(Collectors.groupingBy(ServiceCorrelationSet::getOutputSetId, LinkedHashMap::new,
						Collectors.toList()));

		if (outputGroups.isEmpty()) {
			return;
		}

		for (Map.Entry<String, List<ServiceCorrelationSet>> groupEntry : outputGroups.entrySet()) {

			List<ServiceCorrelationSet> groupCfgs = groupEntry.getValue();
			String outputSetId = groupEntry.getKey();

			if (isFieldsetGroup(groupCfgs, flattened)) {

				List<ApplicationAttrFieldsetData> fieldsetRows = buildFieldsetRows(setId, groupCfgs, event, flattened,
						anyRecord, taskId);

				LOGGER.info("Replacing fieldset rows. applicationId={}, setId={}, outputSetId={}, rows={}",
						event.getApplicationId(), setId, outputSetId, fieldsetRows.size());

				applicationAttrFieldsetDataRepository
						.deleteByApplicationIdAndSetIdAndOutputSetId(event.getApplicationId(), setId, outputSetId);

				applicationAttrFieldsetDataRepository.saveAll(fieldsetRows);

			} else {

				ApplicationAttrData row = buildAttrDataRow(setId, groupCfgs, event, flattened, anyRecord, taskId);

				LOGGER.info("Inserting new attr_data history row. applicationId={}, setId={}, outputSetId={}",
						event.getApplicationId(), setId, outputSetId);

				applicationAttrDataRepository.save(row);
			}
		}
	}

	private boolean isFieldsetGroup(List<ServiceCorrelationSet> groupCfgs, FlattenedFormData flattened) {

		for (ServiceCorrelationSet cfg : groupCfgs) {
			if (ATTR_SOURCE_FORM.equals(cfg.getAttrSource())) {
				String fieldKey = extractFieldKey(cfg.getAttrId(), cfg.getAttrSource());
				if (findDynamicRowParentKey(fieldKey, flattened) != null) {
					return true;
				}
			}
		}

		return false;
	}

	private ApplicationAttrData buildAttrDataRow(Long setId, List<ServiceCorrelationSet> groupCfgs, FormDataEvent event,
			FlattenedFormData flattened, ServiceCorrelationSet anyRecord, String taskId) {

		ServiceCorrelationSet first = groupCfgs.get(0);

		ApplicationAttrData row = new ApplicationAttrData();
		row.setBaseServiceId(anyRecord.getBaseServiceId());
		row.setServiceId(anyRecord.getServiceId());
		row.setApplicationId(event.getApplicationId());
		row.setSetId(setId);
		row.setOutputSetId(first.getOutputSetId());
		row.setTaskId(taskId);
		row.setTenantId(anyRecord.getTenantId());
		row.setCrDate(LocalDateTime.now());

		List<ServiceCorrelationSet> sorted = groupCfgs.stream()
				.sorted(Comparator.comparing(ServiceCorrelationSet::getId)).collect(Collectors.toList());

		if (sorted.size() > MAX_OUTPUT_ATTRS) {
			LOGGER.warn("Output set {} has {} attributes but only {} are supported; extras ignored.",
					first.getOutputSetId(), sorted.size(), MAX_OUTPUT_ATTRS);
		}

		for (int i = 0; i < sorted.size() && i < MAX_OUTPUT_ATTRS; i++) {
			ServiceCorrelationSet cfg = sorted.get(i);
			String fieldKey = extractFieldKey(cfg.getAttrId(), cfg.getAttrSource());
			String value = resolveAttrValue(cfg, event, flattened);
			setAttrPair(row, i + 1, fieldKey, value);
		}

		return row;
	}

	private List<ApplicationAttrFieldsetData> buildFieldsetRows(Long setId, List<ServiceCorrelationSet> groupCfgs,
			FormDataEvent event, FlattenedFormData flattened, ServiceCorrelationSet anyRecord, String taskId) {

		ServiceCorrelationSet first = groupCfgs.get(0);

		String dynamicParentKey = null;

		for (ServiceCorrelationSet cfg : groupCfgs) {
			if (ATTR_SOURCE_FORM.equals(cfg.getAttrSource())) {
				String fieldKey = extractFieldKey(cfg.getAttrId(), cfg.getAttrSource());
				String parentKey = findDynamicRowParentKey(fieldKey, flattened);
				if (parentKey != null) {
					dynamicParentKey = parentKey;
					break;
				}
			}
		}

		if (dynamicParentKey == null) {
			return Collections.emptyList();
		}

		List<Map<String, Object>> dynamicRowsData = flattened.dynamicRows.get(dynamicParentKey);

		if (dynamicRowsData == null || dynamicRowsData.isEmpty()) {
			return Collections.emptyList();
		}

		List<ServiceCorrelationSet> sortedCfgs = groupCfgs.stream()
				.sorted(Comparator.comparing(ServiceCorrelationSet::getId)).collect(Collectors.toList());

		List<ApplicationAttrFieldsetData> fieldsetRows = new ArrayList<>();

		for (int rowIdx = 0; rowIdx < dynamicRowsData.size(); rowIdx++) {

			Map<String, Object> rowData = dynamicRowsData.get(rowIdx);

			ApplicationAttrFieldsetData row = new ApplicationAttrFieldsetData();
			row.setBaseServiceId(anyRecord.getBaseServiceId());
			row.setServiceId(anyRecord.getServiceId());
			row.setApplicationId(event.getApplicationId());
			row.setSetId(setId);
			row.setOutputSetId(first.getOutputSetId());
			row.setTaskId(taskId);
			row.setRowNo(rowIdx + 1);
			row.setTenantId(anyRecord.getTenantId());
			row.setCrDate(LocalDateTime.now());

			for (int i = 0; i < sortedCfgs.size() && i < MAX_OUTPUT_ATTRS; i++) {

				ServiceCorrelationSet cfg = sortedCfgs.get(i);
				String fieldKey = extractFieldKey(cfg.getAttrId(), cfg.getAttrSource());
				String value;

				if (rowData.containsKey(fieldKey)) {
					value = extractScalarFieldValue(rowData.get(fieldKey));
				} else {
					value = resolveAttrValue(cfg, event, flattened);
				}

				setAttrPair(row, i + 1, fieldKey, value);
			}

			fieldsetRows.add(row);
		}

		return fieldsetRows;
	}

	private void setAttrPair(Object row, int index, String attrKey, String attrValue) {
		BeanWrapper wrapper = new BeanWrapperImpl(row);
		wrapper.setPropertyValue("attr" + index, attrKey);
		wrapper.setPropertyValue("attr" + index + "Value", attrValue);
	}

	private String resolveAttrValue(ServiceCorrelationSet cfg, FormDataEvent event, FlattenedFormData flattened) {

		String fieldKey = extractFieldKey(cfg.getAttrId(), cfg.getAttrSource());

		if (flattened.scalarValues.containsKey(fieldKey)) {
			return extractScalarFieldValue(flattened.scalarValues.get(fieldKey));
		}

		String parentKey = findDynamicRowParentKey(fieldKey, flattened);
		if (parentKey != null) {
			List<Map<String, Object>> rows = flattened.dynamicRows.get(parentKey);
			if (!rows.isEmpty()) {
				return extractScalarFieldValue(rows.get(0).get(fieldKey));
			}
		}

		return null;
	}

	private String extractFieldKey(String attrId, String attrSource) {

		if (ATTR_SOURCE_FORM.equals(attrSource) && attrId != null && attrId.contains("$")) {
			String[] parts = attrId.split("\\$");
			return parts[parts.length - 1];
		}

		return attrId;
	}

	private static final class FlattenedFormData {
		final Map<String, Object> scalarValues = new HashMap<>();
		final Map<String, List<Map<String, Object>>> dynamicRows = new LinkedHashMap<>();
	}

	@SuppressWarnings("unchecked")
	private FlattenedFormData flattenFormData(Map<String, Object> formData) {

		FlattenedFormData flattened = new FlattenedFormData();

		if (formData == null) {
			return flattened;
		}

		for (Map.Entry<String, Object> entry : formData.entrySet()) {

			Object value = entry.getValue();

			if (value instanceof List && isDynamicRowList((List<?>) value)) {

				List<Map<String, Object>> rows = new ArrayList<>();

				for (Object row : (List<?>) value) {
					if (row instanceof Map) {
						rows.add((Map<String, Object>) row);
					}
				}

				flattened.dynamicRows.put(entry.getKey(), rows);

			} else {
				flattened.scalarValues.put(entry.getKey(), value);
			}
		}

		return flattened;
	}

	@SuppressWarnings("unchecked")
	private boolean isDynamicRowList(List<?> list) {

		if (list.isEmpty() || !(list.get(0) instanceof Map)) {
			return false;
		}

		Map<String, Object> first = (Map<String, Object>) list.get(0);

		boolean looksLikeCheckboxOption = first.size() <= 2 && first.containsKey("label") && first.containsKey("value");

		return !looksLikeCheckboxOption;
	}

	private String findDynamicRowParentKey(String fieldKey, FlattenedFormData flattened) {

		for (Map.Entry<String, List<Map<String, Object>>> entry : flattened.dynamicRows.entrySet()) {
			List<Map<String, Object>> rows = entry.getValue();
			if (!rows.isEmpty() && rows.get(0).containsKey(fieldKey)) {
				return entry.getKey();
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
				Map<String, Object> mapObj = (Map<String, Object>) rawValue;
				try {
					return objectMapper.writeValueAsString(mapObj);
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