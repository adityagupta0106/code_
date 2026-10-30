package com.serviceplus.tracking.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.tracking.entity.InboxSentBoxFilterConfig;
import com.serviceplus.tracking.entity.ServiceFilterOutputData;
import com.serviceplus.tracking.repository.ServiceFilterOutputDataRepository;

@Service
@Transactional
public class ServiceFilterOutputDataService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceFilterOutputDataService.class);

	private final ServiceFilterOutputDataRepository repository;

	public ServiceFilterOutputDataService(ServiceFilterOutputDataRepository repository) {

		this.repository = repository;
	}

	public ServiceFilterOutputData save(ServiceFilterOutputData outputData) {

		return repository.save(outputData);
	}

	public List<ServiceFilterOutputData> saveAll(List<ServiceFilterOutputData> outputDataList) {

		return repository.saveAll(outputDataList);
	}

	@Transactional(readOnly = true)
	public List<ServiceFilterOutputData> findByApplicationId(String applicationId) {

		return repository.findByApplicationId(applicationId);
	}

	public void save(String applicationId, List<InboxSentBoxFilterConfig> outputFilterConfigurationList,
			Map<String, Object> dataForOutputFilters) {

		if (outputFilterConfigurationList == null || outputFilterConfigurationList.isEmpty()) {
			logger.debug("No output filter configuration found. applicationId={}", applicationId);
			return;
		}

		if (dataForOutputFilters == null || dataForOutputFilters.isEmpty()) {
			logger.debug("No output filter data found. applicationId={}", applicationId);
			return;
		}

		Map<String, List<InboxSentBoxFilterConfig>> groupedConfigurations = new HashMap<>();
		for (InboxSentBoxFilterConfig filterConfig : outputFilterConfigurationList) {
			if (filterConfig == null) {
				continue;
			}

			Long filterId = filterConfig.getFilterId();
			Long serviceId = filterConfig.getServiceId();

			if (filterId == null || serviceId == null) {
				logger.error(
						"Skipping output filter configuration because " + "filterId or serviceId is null. "
								+ "filterId={}, serviceId={}, attrId={}, attrOrder={}",
						filterId, serviceId, filterConfig.getAttrId(), filterConfig.getAttrOrder());
				continue;
			}

			String groupKey = filterId + "_" + serviceId;

			groupedConfigurations.computeIfAbsent(groupKey, key -> new ArrayList<>()).add(filterConfig);
		}
		List<ServiceFilterOutputData> outputDataList = new ArrayList<>();
		for (List<InboxSentBoxFilterConfig> filterConfigs : groupedConfigurations.values()) {
			if (filterConfigs == null || filterConfigs.isEmpty()) {
				continue;
			}

			InboxSentBoxFilterConfig firstConfig = filterConfigs.get(0);
			Long filterId = firstConfig.getFilterId();
			Long serviceId = firstConfig.getServiceId();
			ServiceFilterOutputData outputData = new ServiceFilterOutputData();
			outputData.setFilterId(filterId);
			outputData.setBaseServiceId(firstConfig.getBaseServiceId());
			outputData.setServiceId(serviceId);
			outputData.setApplicationId(applicationId);
			for (InboxSentBoxFilterConfig filterConfig : filterConfigs) {
				String attrId = filterConfig.getAttrId();
				if (attrId == null || attrId.isBlank()) {
					logger.error(
							"Skipping output attribute because " + "attrId is null/blank. "
									+ "filterId={}, serviceId={}, applicationId={}",
							filterId, serviceId, applicationId);
					continue;
				}
				if (!dataForOutputFilters.containsKey(attrId)) {
					continue;
				}
				Object value = dataForOutputFilters.get(attrId);
				setAttributeValue(outputData, filterConfig.getAttrOrder(), attrId, value);
			}
			outputDataList.add(outputData);
		}
		if (!outputDataList.isEmpty()) {
			repository.saveAll(outputDataList);
		}
	}
	private void setAttributeValue(ServiceFilterOutputData outputData, Integer attrOrder, String attrId, Object value) {
		if (attrOrder == null) {
			logger.error(
					"Skipping output attribute because attrOrder is null. "
							+ "attrId={}, filterId={}, serviceId={}, applicationId={}",
					attrId, outputData.getFilterId(), outputData.getServiceId(), outputData.getApplicationId());
			return;
		}

		String finalValue = value != null ? value.toString() : null;
		switch (attrOrder) {
		case 1:
			outputData.setAttr1Value(finalValue);
			break;
		case 2:
			outputData.setAttr2Value(finalValue);
			break;
		case 3:
			outputData.setAttr3Value(finalValue);
			break;
		case 4:
			outputData.setAttr4Value(finalValue);
			break;
		case 5:
			outputData.setAttr5Value(finalValue);
			break;
		case 6:
			outputData.setAttr6Value(finalValue);
			break;
		case 7:
			outputData.setAttr7Value(finalValue);
			break;
		case 8:
			outputData.setAttr8Value(finalValue);
			break;
		case 9:
			outputData.setAttr9Value(finalValue);
			break;
		case 10:
			outputData.setAttr10Value(finalValue);
			break;
		default:
			logger.error(
					"Invalid attrOrder={}. " + "Skipping output attribute value. "
							+ "attrId={}, filterId={}, serviceId={}, applicationId={}",
					attrOrder, attrId, outputData.getFilterId(), outputData.getServiceId(),
					outputData.getApplicationId());

			return;
		}
	}
}