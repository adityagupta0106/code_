package com.serviceplus.tracking.correlationapplication.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.tracking.correlationapplication.entity.ApplicationSetOrderAttrData;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationSetOrder;
import com.serviceplus.tracking.correlationmapping.repository.ApplicationSetOrderAttrDataRepository;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationSetOrderRepository;

@Service
public class ApplicationSetOrderAttrDataService {

	private static final Logger LOGGER = LogManager.getLogger(ApplicationSetOrderAttrDataService.class);

	private final ApplicationSetOrderAttrDataRepository applicationRepository;

	private final ServiceCorrelationSetOrderRepository configurationRepository;

	public ApplicationSetOrderAttrDataService(ApplicationSetOrderAttrDataRepository applicationRepository,
			ServiceCorrelationSetOrderRepository configurationRepository) {

		this.applicationRepository = applicationRepository;
		this.configurationRepository = configurationRepository;
	}

	@Transactional
	public void saveApplicationSetOrderAttrData(String applicationId, Long baseServiceId, Long serviceId, String taskId,
			String tenantId, Integer entityId, Map<String, String> attributeValues) {

		LOGGER.info(
				"Save Application Set Order Attribute Data START - "
						+ "applicationId={}, baseServiceId={}, serviceId={}, taskId={}, tenantId={}, entityId={}",
				applicationId, baseServiceId, serviceId, taskId, tenantId, entityId);

		validateRequest(applicationId, baseServiceId, serviceId, tenantId, entityId);

		/*
		 * 1. Get configuration.
		 */
		List<ServiceCorrelationSetOrder> configurations = configurationRepository
				.findByBaseServiceIdAndServiceIdAndTenantIdAndEntityIdOrderBySetIdAscAttrSortOrderAsc(baseServiceId,
						serviceId, tenantId, entityId);

		if (configurations == null || configurations.isEmpty()) {

			LOGGER.info("No set order configuration found - baseServiceId={}, serviceId={}, tenantId={}, entityId={}",
					baseServiceId, serviceId, tenantId, entityId);

			return;
		}

		Long currentSetId = null;

		ApplicationSetOrderAttrData entity = null;

		int attributePosition = 0;

		for (ServiceCorrelationSetOrder configuration : configurations) {

			if (currentSetId == null || !currentSetId.equals(configuration.getSetId())) {

				if (entity != null) {
					saveEntity(entity);
				}

				currentSetId = configuration.getSetId();

				Optional<ApplicationSetOrderAttrData> existing = applicationRepository
						.findByApplicationIdAndServiceIdAndSetIdAndTenantId(applicationId, serviceId, currentSetId,
								tenantId);

				if (existing.isPresent()) {

					entity = existing.get();

					LOGGER.debug(
							"Existing application set record found. Updating - "
									+ "applicationId={}, serviceId={}, setId={}",
							applicationId, serviceId, currentSetId);

				} else {

					entity = new ApplicationSetOrderAttrData();

					entity.setApplicationId(applicationId);
					entity.setBaseServiceId(baseServiceId);
					entity.setServiceId(serviceId);
					entity.setSetId(currentSetId);
					entity.setTaskId(taskId);
					entity.setTenantId(tenantId);

					LOGGER.debug("Creating new application set record - " + "applicationId={}, serviceId={}, setId={}",
							applicationId, serviceId, currentSetId);
				}

				attributePosition = 0;
			}

			entity.setBaseServiceId(baseServiceId);
			entity.setServiceId(serviceId);
			entity.setTaskId(taskId);
			entity.setTenantId(tenantId);

			attributePosition++;

			String attrId = configuration.getAttrId();

			String attrValue = null;

			if (attributeValues != null && attrId != null) {
				attrValue = attributeValues.get(attrId);
			}

			setAttribute(entity, attributePosition, attrId, attrValue);
		}

		if (entity != null) {
			saveEntity(entity);
		}

		LOGGER.info("Save Application Set Order Attribute Data SUCCESS - applicationId={}", applicationId);
	}

	private void saveEntity(ApplicationSetOrderAttrData entity) {

		LocalDateTime now = LocalDateTime.now();

		if (entity.getCrDate() == null) {
			entity.setCrDate(now);
		}

		entity.setUpDate(now);

		applicationRepository.save(entity);

		LOGGER.debug("Application set order attribute data saved - " + "applicationId={}, serviceId={}, setId={}",
				entity.getApplicationId(), entity.getServiceId(), entity.getSetId());
	}

	private void setAttribute(ApplicationSetOrderAttrData entity, int position, String attrId, String attrValue) {

		switch (position) {

		case 1:
			entity.setAttr1(attrId);
			entity.setAttr1Value(attrValue);
			break;

		case 2:
			entity.setAttr2(attrId);
			entity.setAttr2Value(attrValue);
			break;

		case 3:
			entity.setAttr3(attrId);
			entity.setAttr3Value(attrValue);
			break;

		case 4:
			entity.setAttr4(attrId);
			entity.setAttr4Value(attrValue);
			break;

		case 5:
			entity.setAttr5(attrId);
			entity.setAttr5Value(attrValue);
			break;

		case 6:
			entity.setAttr6(attrId);
			entity.setAttr6Value(attrValue);
			break;

		case 7:
			entity.setAttr7(attrId);
			entity.setAttr7Value(attrValue);
			break;

		case 8:
			entity.setAttr8(attrId);
			entity.setAttr8Value(attrValue);
			break;

		case 9:
			entity.setAttr9(attrId);
			entity.setAttr9Value(attrValue);
			break;

		case 10:
			entity.setAttr10(attrId);
			entity.setAttr10Value(attrValue);
			break;

		default:
			throw new IllegalArgumentException("Maximum 10 attributes are supported for setId=" + entity.getSetId());
		}
	}

	private void validateRequest(String applicationId, Long baseServiceId, Long serviceId, String tenantId,
			Integer entityId) {

		if (applicationId == null || applicationId.trim().isEmpty()) {

			throw new IllegalArgumentException("applicationId is required");
		}

		if (baseServiceId == null) {
			throw new IllegalArgumentException("baseServiceId is required");
		}

		if (serviceId == null) {
			throw new IllegalArgumentException("serviceId is required");
		}

		if (tenantId == null || tenantId.trim().isEmpty()) {

			throw new IllegalArgumentException("tenantId is required");
		}

		if (entityId == null) {
			throw new IllegalArgumentException("entityId is required");
		}
	}
}