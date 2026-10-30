package com.serviceplus.tracking.correlationmapping.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.tracking.correlationConfig.dto.CorrelationSetDTO;
import com.serviceplus.tracking.correlationConfig.dto.CorrelationSetKafkaDTO;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationSet;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationSetOrder;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationSetOrderRepository;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationSetRepository;

@Service
public class ServiceCorrelationSetService {

	private static final Logger LOGGER = LogManager.getLogger(ServiceCorrelationSetService.class);

	private final ServiceCorrelationSetRepository correlationSetRepository;
	private final ServiceCorrelationSetOrderRepository correlationSetOrderRepository;

	public ServiceCorrelationSetService(ServiceCorrelationSetRepository correlationSetRepository,
			ServiceCorrelationSetOrderRepository correlationSetOrderRepository) {
		this.correlationSetRepository = correlationSetRepository;
		this.correlationSetOrderRepository = correlationSetOrderRepository;
	}

	@Transactional
	public void saveOrUpdateCorrelationSet(CorrelationSetKafkaDTO request) {

		if (request == null) {
			throw new IllegalArgumentException("Correlation set request cannot be null");
		}

		if (request.getServiceId() == null) {
			throw new IllegalArgumentException("serviceId cannot be null");
		}

		if (request.getSets() == null) {
			throw new IllegalArgumentException("sets cannot be null");
		}

		Long serviceId = request.getServiceId();
		String tenantId = request.getTenantId();
		Integer entityId = request.getEntityId();

		LOGGER.info(
				"Correlation set save/update started. serviceId={}, baseServiceId={}, "
						+ "tenantId={}, entityId={}, setCount={}",
				serviceId, request.getBaseServiceId(), tenantId, entityId, request.getSets().size());

		try {

			deleteExistingSets(serviceId, tenantId, entityId);
			
			deleteExistingOrders(serviceId, tenantId, entityId);

			List<ServiceCorrelationSet> setEntities = new ArrayList<>();

			List<ServiceCorrelationSetOrder> orderEntities = new ArrayList<>();

			for (CorrelationSetDTO setDTO : request.getSets()) {

				if (setDTO == null) {
					continue;
				}

				prepareCorrelationSetEntities(request, setDTO, setEntities, orderEntities);
			}

			LOGGER.info("Prepared correlation set records. " + "serviceId={}, setRecordCount={}, orderRecordCount={}",
					serviceId, setEntities.size(), orderEntities.size());

			saveSetEntities(setEntities, serviceId);

			saveOrderEntities(orderEntities, serviceId);

			LOGGER.info(
					"Correlation set save/update completed successfully. " + "serviceId={}, tenantId={}, entityId={}",
					serviceId, tenantId, entityId);

		} catch (RuntimeException e) {

			LOGGER.error("Correlation set save/update failed. " + "serviceId={}, tenantId={}, entityId={}", serviceId,
					tenantId, entityId, e);

			throw e;
		}
	}

	private void deleteExistingSets(Long serviceId, String tenantId, Integer entityId) {

		List<ServiceCorrelationSet> existingSets = correlationSetRepository
				.findByServiceIdAndTenantIdAndEntityId(serviceId, tenantId, entityId);

		if (existingSets.isEmpty()) {

			LOGGER.debug("No existing correlation set records found. " + "serviceId={}, tenantId={}, entityId={}",
					serviceId, tenantId, entityId);

			return;
		}

		LOGGER.info("Deleting existing correlation set records. " + "serviceId={}, recordCount={}", serviceId,
				existingSets.size());

		correlationSetRepository.deleteAll(existingSets);
		correlationSetRepository.flush();
	}

	private void deleteExistingOrders(Long serviceId, String tenantId, Integer entityId) {

		List<ServiceCorrelationSetOrder> existingOrders = correlationSetOrderRepository
				.findByServiceIdAndTenantIdAndEntityId(serviceId, tenantId, entityId);

		if (existingOrders.isEmpty()) {

			LOGGER.debug("No existing correlation set order records found. " + "serviceId={}, tenantId={}, entityId={}",
					serviceId, tenantId, entityId);

			return;
		}

		LOGGER.info("Deleting existing correlation set order records. " + "serviceId={}, recordCount={}", serviceId,
				existingOrders.size());

		correlationSetOrderRepository.deleteAll(existingOrders);
		correlationSetOrderRepository.flush();
	}

	private void saveSetEntities(List<ServiceCorrelationSet> setEntities, Long serviceId) {

		if (setEntities.isEmpty()) {

			LOGGER.debug("No correlation set records to save. serviceId={}", serviceId);

			return;
		}

		LOGGER.info("Total correlation set entities to save: {}", setEntities.size());

		// Saved one at a time (not saveAll) to preserve the original per-row
		// INSERT START/SUCCESS/FAILED log granularity.
		for (ServiceCorrelationSet entity : setEntities) {

			LOGGER.info("INSERT START - serviceId={}, setId={}, attrId={}, setType={}, outputSetId={}",
					entity.getServiceId(), entity.getSetId(), entity.getAttrId(), entity.getSetType(),
					entity.getOutputSetId());

			try {

				correlationSetRepository.save(entity);

				LOGGER.info("INSERT SUCCESS - serviceId={}, setId={}, attrId={}, setType={}, outputSetId={}",
						entity.getServiceId(), entity.getSetId(), entity.getAttrId(), entity.getSetType(),
						entity.getOutputSetId());

			} catch (RuntimeException e) {

				LOGGER.error("INSERT FAILED - serviceId={}, setId={}, attrId={}, setType={}, outputSetId={}",
						entity.getServiceId(), entity.getSetId(), entity.getAttrId(), entity.getSetType(),
						entity.getOutputSetId(), e);

				throw e;
			}
		}
	}

	private void saveOrderEntities(List<ServiceCorrelationSetOrder> orderEntities, Long serviceId) {

		if (orderEntities.isEmpty()) {

			LOGGER.debug("No correlation set order records to save. " + "serviceId={}", serviceId);

			return;
		}

		List<ServiceCorrelationSetOrder> savedOrders = correlationSetOrderRepository.saveAll(orderEntities);

		LOGGER.info("Correlation set order records " + "saved successfully. " + "serviceId={}, recordCount={}",
				serviceId, savedOrders.size());
	}

	private void prepareCorrelationSetEntities(CorrelationSetKafkaDTO request, CorrelationSetDTO setDTO,
			List<ServiceCorrelationSet> setEntities, List<ServiceCorrelationSetOrder> orderEntities) {

		OffsetDateTime currentDate = OffsetDateTime.now();

		Long serviceId = setDTO.getServiceId();
		Long baseServiceId = setDTO.getBaseServiceId();
		Long setId = setDTO.getSetId();

		String sourceType = setDTO.getSourceType();
		String securityKey = setDTO.getSecurityKey();

		String tenantId = setDTO.getTenantId() != null ? setDTO.getTenantId() : request.getTenantId();

		Integer entityId = setDTO.getEntityId() != null ? setDTO.getEntityId() : request.getEntityId();

		if (setDTO.getInputAttributes() != null) {

			for (CorrelationSetDTO.CorrelationAttributeDTO attribute : setDTO.getInputAttributes()) {

				if (attribute == null || attribute.getAttributeId() == null) {
					continue;
				}

				ServiceCorrelationSet entity = new ServiceCorrelationSet();

				entity.setServiceId(serviceId);
				entity.setBaseServiceId(baseServiceId);
				entity.setSetId(setId);
				entity.setSourceType(sourceType);
				entity.setSkey(securityKey);

				entity.setAttrId(attribute.getAttributeId());

				entity.setTaskId(attribute.getTaskId());

				entity.setAttrSource(attribute.getAttributeSource());

				entity.setAttrSortOrder(attribute.getSortOrder());

				entity.setSetType("I");

				entity.setOutputSetId(null);

				entity.setTenantId(tenantId);
				entity.setEntityId(entityId);
				entity.setCrDate(currentDate);

				setEntities.add(entity);

				LOGGER.debug(
						"Prepared INPUT correlation attribute. " + "serviceId={}, setId={}, attrId={}, "
								+ "attrSource={}, sortOrder={}",
						serviceId, setId, attribute.getAttributeId(), attribute.getAttributeSource(),
						attribute.getSortOrder());
			}
		}

		if (setDTO.getOutputAttributes() != null) {

			for (CorrelationSetDTO.OutputAttributeDTO outputDTO : setDTO.getOutputAttributes()) {

				if (outputDTO == null || outputDTO.getAttributes() == null) {
					continue;
				}

				String outputSetId = outputDTO.getOutputSetid();

				for (CorrelationSetDTO.OutputAttributeItemDTO attribute : outputDTO.getAttributes()) {

					if (attribute == null || attribute.getAttributeId() == null) {
						continue;
					}

					ServiceCorrelationSet entity = new ServiceCorrelationSet();

					entity.setServiceId(serviceId);
					entity.setBaseServiceId(baseServiceId);
					entity.setSetId(setId);
					entity.setSourceType(sourceType);
					entity.setSkey(securityKey);

					entity.setAttrId(attribute.getAttributeId());

					entity.setTaskId(attribute.getTaskId());

					entity.setAttrSource(attribute.getAttributeSource());

					entity.setAttrSortOrder(null);

					entity.setSetType("O");

					entity.setOutputSetId(outputSetId);

					entity.setTenantId(tenantId);
					entity.setEntityId(entityId);
					entity.setCrDate(currentDate);

					setEntities.add(entity);

					LOGGER.debug(
							"Prepared OUTPUT correlation attribute. " + "serviceId={}, setId={}, outputSetId={}, "
									+ "attrId={}, attrSource={}",
							serviceId, setId, outputSetId, attribute.getAttributeId(), attribute.getAttributeSource());
				}
			}
		}

		if (setDTO.getSortingAttributes() != null) {

			for (CorrelationSetDTO.SortingAttributeDTO sortingDTO : setDTO.getSortingAttributes()) {

				if (sortingDTO == null || sortingDTO.getAttributeId() == null) {
					continue;
				}

				ServiceCorrelationSetOrder orderEntity = new ServiceCorrelationSetOrder();

				orderEntity.setServiceId(serviceId);
				orderEntity.setBaseServiceId(baseServiceId);
				orderEntity.setSetId(setId);

				orderEntity.setAttrId(sortingDTO.getAttributeId());

				orderEntity.setAttrSortOrder(sortingDTO.getSortOrder());

				orderEntity.setAttrOrderType(
						sortingDTO.getSortSequence() != null ? sortingDTO.getSortSequence().name() : null);

				orderEntity.setTenantId(tenantId);
				orderEntity.setEntityId(entityId);
				orderEntity.setCrDate(currentDate);

				orderEntities.add(orderEntity);

				LOGGER.debug(
						"Prepared SORTING attribute. " + "serviceId={}, setId={}, attrId={}, "
								+ "sortOrder={}, orderType={}",
						serviceId, setId, sortingDTO.getAttributeId(), sortingDTO.getSortOrder(),
						sortingDTO.getSortSequence());
			}
		}
	}
}