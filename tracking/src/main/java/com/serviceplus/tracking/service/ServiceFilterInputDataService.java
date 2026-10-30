package com.serviceplus.tracking.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.tracking.entity.InboxSentBoxFilterConfig;
import com.serviceplus.tracking.entity.ServiceFilterInputData;
import com.serviceplus.tracking.repository.ServiceFilterInputDataRepository;

@Service
@Transactional
public class ServiceFilterInputDataService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceFilterInputDataService.class);

	private final ServiceFilterInputDataRepository repository;

	public ServiceFilterInputDataService(ServiceFilterInputDataRepository repository) {
		this.repository = repository;
	}

	public void save(String applicationId, List<InboxSentBoxFilterConfig> inputFilterConfigurationList, Map<String, Object> dataForInputFilters) {
		if (inputFilterConfigurationList == null || inputFilterConfigurationList.isEmpty()) {
			logger.debug("No input filter configuration found. applicationId={}", applicationId);
			return;
		}

		if (dataForInputFilters == null || dataForInputFilters.isEmpty()) {
			logger.debug("No input filter data found. applicationId={}", applicationId);
			return;
		}

		Map<String, List<InboxSentBoxFilterConfig>> groupedConfigurations = new HashMap<>();

		for (InboxSentBoxFilterConfig filterConfig : inputFilterConfigurationList) {
			if (filterConfig == null) {
				continue;
			}

			Long filterId = filterConfig.getFilterId();
			Long serviceId = filterConfig.getServiceId();

			if (filterId == null || serviceId == null) {
				logger.error(
						"Skipping filter configuration because filterId "
								+ "or serviceId is null. filterId={}, serviceId={}, " + "attrId={}, attrOrder={}",
						filterId, serviceId, filterConfig.getAttrId(), filterConfig.getAttrOrder());
				continue;
			}

			String groupKey = filterId + "_" + serviceId;
			groupedConfigurations.computeIfAbsent(groupKey, key -> new ArrayList<>()).add(filterConfig);
		}

		for (List<InboxSentBoxFilterConfig> filterConfigs : groupedConfigurations.values()) {

			if (filterConfigs == null || filterConfigs.isEmpty()) {
				continue;
			}

			InboxSentBoxFilterConfig firstConfig = filterConfigs.get(0);
			Long filterId = firstConfig.getFilterId();
			Long serviceId = firstConfig.getServiceId();

			ServiceFilterInputData inputData = getAttribute(applicationId, serviceId, filterId);
			if (inputData == null) {
				inputData = new ServiceFilterInputData();
				inputData.setFilterId(filterId);
				inputData.setBaseServiceId(firstConfig.getBaseServiceId());
				inputData.setServiceId(serviceId);
				inputData.setApplicationId(applicationId);

				inputData.setCrDate(new Date());
				logger.debug("Creating new service filter input data. " + "applicationId={}, serviceId={}, filterId={}",
						applicationId, serviceId, filterId);
			}

			for (InboxSentBoxFilterConfig filterConfig : filterConfigs) {
				String attrId = filterConfig.getAttrId();
				if (attrId == null || attrId.isBlank()) {
					logger.error(
							"Skipping attribute because attrId is null/blank. "
									+ "filterId={}, serviceId={}, applicationId={}",
							filterId, serviceId, applicationId);
					continue;
				}

				if (!dataForInputFilters.containsKey(attrId)) {
					continue;
				}

				Object value = dataForInputFilters.get(attrId);
				setAttributeValue(inputData, filterConfig.getAttrOrder(), attrId, value);
			}

			repository.save(inputData);
		}
	}

	private ServiceFilterInputData getAttribute(String applicationId, Long serviceId, Long filterId) {

		List<ServiceFilterInputData> existingRecords = repository
				.findByApplicationIdAndServiceIdAndFilterId(applicationId, serviceId, filterId);

		if (existingRecords == null || existingRecords.isEmpty()) {
			return null;
		}

		if (existingRecords.size() > 1) {
			logger.error(
					"Multiple service filter input records found. " + "Using the first record. applicationId={}, "
							+ "serviceId={}, filterId={}, recordCount={}",
					applicationId, serviceId, filterId, existingRecords.size());
		}

		return existingRecords.get(0);
	}

	private void setAttributeValue(ServiceFilterInputData inputData, Integer attrOrder, String attrId, Object value) {

		if (attrOrder == null) {
			logger.error(
					"Skipping attribute because attrOrder is null. "
							+ "attrId={}, filterId={}, serviceId={}, applicationId={}",
					attrId, inputData.getFilterId(), inputData.getServiceId(), inputData.getApplicationId());
			return;
		}

		String finalValue = value != null ? value.toString() : null;

		switch (attrOrder) {

		case 1:
			inputData.setAttr1Value(finalValue);
			break;

		case 2:
			inputData.setAttr2Value(finalValue);
			break;

		case 3:
			inputData.setAttr3Value(finalValue);
			break;

		case 4:
			inputData.setAttr4Value(finalValue);
			break;

		case 5:
			inputData.setAttr5Value(finalValue);
			break;

		case 6:
			inputData.setAttr6Value(finalValue);
			break;

		case 7:
			inputData.setAttr7Value(finalValue);
			break;

		case 8:
			inputData.setAttr8Value(finalValue);
			break;

		case 9:
			inputData.setAttr9Value(finalValue);
			break;

		case 10:
			inputData.setAttr10Value(finalValue);
			break;

		default:
			logger.error(
					"Invalid attrOrder={}. " + "Skipping attribute value. "
							+ "attrId={}, filterId={}, serviceId={}, applicationId={}",
					attrOrder, attrId, inputData.getFilterId(), inputData.getServiceId(), inputData.getApplicationId());

			return;
		}
	}
}