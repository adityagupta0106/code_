package com.serviceplus.tracking.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.dto.FormDataEvent;
import com.serviceplus.tracking.entity.InboxSentBoxFilterConfig;
import com.serviceplus.tracking.utility.ApplicationConstants;

@Service
public class ApplAttrKafkaService {
	private final ServiceFilterInputDataService filterInputDataService;
	private final ServiceFilterOutputDataService filterOutputDataService;
	private final InboxSentBoxFilterConfigService inboxSentBoxFilterConfigService;

	public ApplAttrKafkaService(InboxSentBoxFilterConfigService inboxSentBoxFilterConfigService,
			ServiceFilterInputDataService filterInputDataService,
			ServiceFilterOutputDataService filterOutputDataService) {
		super();
		this.filterInputDataService = filterInputDataService;
		this.filterOutputDataService = filterOutputDataService;
		this.inboxSentBoxFilterConfigService = inboxSentBoxFilterConfigService;
	}

	public void persistMessage(FormDataEvent event) {
		String applicationId = event.getApplicationId();
		Long serviceId = event.getServiceId().longValue();

		Map<String, Object> formData = event.getFormData();

		if (formData == null || formData.isEmpty()) {
			return;
		}

		List<InboxSentBoxFilterConfig> inputFilterConfigurationList = inboxSentBoxFilterConfigService.findByServiceIdAndAttrType(serviceId, 'I');
		List<InboxSentBoxFilterConfig> outputFilterConfigurationList = inboxSentBoxFilterConfigService.findByServiceIdAndAttrType(serviceId, 'O');

		Set<String> inputFilterConfigAttrSet = new HashSet<String>();
		for (InboxSentBoxFilterConfig filterConfig : inputFilterConfigurationList) {
			inputFilterConfigAttrSet.add(filterConfig.getAttrId());
		}

		Set<String> outputFilterConfigAttrSet = new HashSet<String>();
		for (InboxSentBoxFilterConfig filterConfig : outputFilterConfigurationList) {
			outputFilterConfigAttrSet.add(filterConfig.getAttrId());
		}

		Map<String, Object> dataForInputFilters = new HashMap<String, Object>();
		Map<String, Object> dataForOutputFilters = new HashMap<String, Object>();

		setAttributesData(inputFilterConfigAttrSet, outputFilterConfigAttrSet, dataForInputFilters, dataForOutputFilters, event);
		if (inputFilterConfigurationList != null && !inputFilterConfigurationList.isEmpty()) {
			filterInputDataService.save(applicationId, inputFilterConfigurationList, dataForInputFilters);
		}
		
		if (outputFilterConfigurationList != null && !outputFilterConfigurationList.isEmpty()) {
			filterOutputDataService.save(applicationId, outputFilterConfigurationList, dataForOutputFilters);
		}
	}


	private void setAttributesData(Set<String> inputFilterConfigAttrSet, Set<String> outputFilterConfigAttrSet,
			Map<String, Object> dataForInputFilters, Map<String, Object> dataForOutputFilters, FormDataEvent event) {

		Map<String, Object> formData = event.getFormData();

		List<String> taskIds = event.getTaskIds();

		if (taskIds == null || taskIds.isEmpty() || formData == null || formData.isEmpty()) {
			return;
		}

		List<String> keys = event.getKeys();
		Set<String> keySet = keys != null ? new HashSet<>(keys) : null;

		for (String taskId : taskIds) {

			if (taskId == null || taskId.isBlank()) {
				continue;
			}

			for (Map.Entry<String, Object> entry : formData.entrySet()) {

				String attributeId = entry.getKey();
				Object rawValue = entry.getValue();

				String finalValue;
				String labelValue;

				String attributeKey = taskId + "$" + event.getHolderId() + "$" + attributeId;

				if (keySet != null && !keySet.isEmpty() && !keySet.contains(attributeKey)) {
					continue;
				}

				if (rawValue instanceof String str) {
					try {
						JSONObject json = new JSONObject(str);
						finalValue = json.optString("value", str);
						labelValue = json.optString("value", str);
					} catch (Exception e) {
						finalValue = str;
						labelValue = str;
					}

				} else if (rawValue instanceof Map<?, ?> map) {
					Object value = map.get("value");
					finalValue = value != null ? value.toString() : null;

					Object label = map.get("label");
					labelValue = label != null ? label.toString() : finalValue;

				} else if (rawValue instanceof List<?> list) {
					List<String> values = new ArrayList<>();
					List<String> labels = new ArrayList<>();

					for (Object item : list) {
						if (item instanceof Map<?, ?> itemMap) {
							Object value = itemMap.get("value");
							Object label = itemMap.get("label");

							if (value != null) {
								values.add(value.toString());
							}

							if (label != null) {
								labels.add(label.toString());
							} else if (value != null) {
								labels.add(value.toString());
							}

						} else if (item != null) {
							values.add(item.toString());
							labels.add(item.toString());
						}
					}

					finalValue = String.join(",", values);
					labelValue = String.join(",", labels);

				} else {
					finalValue = rawValue != null ? rawValue.toString() : null;

					labelValue = rawValue != null ? rawValue.toString() : null;
				}

				if (inputFilterConfigAttrSet.contains(attributeId)) {
					dataForInputFilters.put(attributeKey, finalValue);
				}

				if (outputFilterConfigAttrSet.contains(attributeId)) {
					dataForOutputFilters.put(attributeKey, labelValue);
				}
			}
		}

		if (inputFilterConfigAttrSet.contains(ApplicationConstants.SERVICE_COMPLETION_DATE)) {
			dataForInputFilters.put(ApplicationConstants.SERVICE_COMPLETION_DATE,
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		}

		if (outputFilterConfigAttrSet.contains(ApplicationConstants.SERVICE_COMPLETION_DATE)) {
			dataForOutputFilters.put(ApplicationConstants.SERVICE_COMPLETION_DATE,
					new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		}
	}

}
