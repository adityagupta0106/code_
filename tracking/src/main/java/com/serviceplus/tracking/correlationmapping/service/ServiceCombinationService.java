package com.serviceplus.tracking.correlationmapping.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.tracking.correlationmapping.dto.ServiceCombinationConfigurationDTO;
import com.serviceplus.tracking.correlationmapping.dto.ServiceCombinationConfigurationDTO.AttributeMappingDTO;
import com.serviceplus.tracking.correlationmapping.dto.ServiceCombinationConfigurationDTO.CombinationSetDTO;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationAttrMapping;
import com.serviceplus.tracking.correlationmapping.entity.ServiceCorrelationMapping;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationAttrMappingRepository;
import com.serviceplus.tracking.correlationmapping.repository.ServiceCorrelationMappingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ServiceCombinationService {

	private static final Logger LOGGER = LogManager.getLogger(ServiceCombinationService.class);

	private final ServiceCorrelationMappingRepository mappingRepository;

	private final ServiceCorrelationAttrMappingRepository attrMappingRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public ServiceCombinationService(ServiceCorrelationMappingRepository mappingRepository,
			ServiceCorrelationAttrMappingRepository attrMappingRepository) {

		this.mappingRepository = mappingRepository;
		this.attrMappingRepository = attrMappingRepository;
	}

	@Transactional
	public void saveServiceCombination(ServiceCombinationConfigurationDTO request) {

		validateRequest(request);

		Long serviceId = request.getServiceId();
		String taskId = request.getTaskId();
		Long combinationId = request.getCombinationId();
		String tenantId = request.getTenantId();
		Integer entityId = request.getEntityId();

		LOGGER.info(
				"Service Combination Save START - serviceId={}, taskId={}, combinationId={}, tenantId={}, entityId={}",
				serviceId, taskId, combinationId, tenantId, entityId);

		try {

			long deletedAttrMappings = attrMappingRepository
					.deleteByServiceIdAndTaskIdAndCombinationIdAndTenantIdAndEntityId(serviceId, taskId, combinationId,
							tenantId, entityId);

			LOGGER.info("Deleted {} attribute mappings", deletedAttrMappings);

			long deletedMappings = mappingRepository.deleteByServiceIdAndTaskIdAndCombinationIdAndTenantIdAndEntityId(
					serviceId, taskId, combinationId, tenantId, entityId);

			LOGGER.info("Deleted {} correlation mappings", deletedMappings);

			entityManager.flush();

			LOGGER.info("Old correlation mappings flushed successfully");

			List<ServiceCorrelationMapping> mappings = new ArrayList<>();

			List<ServiceCorrelationAttrMapping> attrMappings = new ArrayList<>();

			Date now = new Date();

			if (request.getSets() != null) {

				for (CombinationSetDTO set : request.getSets()) {

					if (set == null) {
						continue;
					}

					ServiceCorrelationMapping mapping = prepareCorrelationMapping(request, set, now);

					mappings.add(mapping);

					prepareAttributeMappings(request, set, "I", set.getInputAttributes(), attrMappings, now);

					prepareAttributeMappings(request, set, "O", set.getOutputAttributes(), attrMappings, now);
				}
			}

			LOGGER.info("Prepared correlation mappings={}, attribute mappings={}", mappings.size(),
					attrMappings.size());

			if (!mappings.isEmpty()) {

				List<ServiceCorrelationMapping> savedMappings = mappingRepository.saveAll(mappings);

				entityManager.flush();

				LOGGER.info("Saved {} service correlation mappings", savedMappings.size());
			}

			if (!attrMappings.isEmpty()) {

				List<ServiceCorrelationAttrMapping> savedAttrMappings = attrMappingRepository.saveAll(attrMappings);

				entityManager.flush();

				LOGGER.info("Saved {} service correlation attribute mappings", savedAttrMappings.size());
			}

			LOGGER.info("Service Combination Save SUCCESS - serviceId={}, taskId={}, combinationId={}", serviceId,
					taskId, combinationId);

		} catch (RuntimeException e) {

			LOGGER.error("Service Combination Save FAILED - serviceId={}, taskId={}, combinationId={}", serviceId,
					taskId, combinationId, e);

			throw e;
		}
	}

	private ServiceCorrelationMapping prepareCorrelationMapping(ServiceCombinationConfigurationDTO request,
			CombinationSetDTO set, Date now) {

		ServiceCorrelationMapping entity = new ServiceCorrelationMapping();

		entity.setBaseServiceId(request.getBaseServiceId());

		entity.setServiceId(request.getServiceId());

		entity.setTaskId(request.getTaskId());

		entity.setCombinationId(request.getCombinationId());

		entity.setMappedBaseServiceId(set.getBaseServiceId());

		entity.setMappedServiceId(set.getServiceId());

		entity.setMappedSetId(set.getSetId());

		entity.setOutputSetId(set.getOutputSetId());

		entity.setSourceType(set.getSourceType());

		entity.setPriority(set.getPriority());

		entity.setSearchNext(request.getSearchAfterDataFound());

		if (set.getApplicationStatus() != null) {

			entity.setApplStatus(set.getApplicationStatus().getValue());

			entity.setMapppedCompletedTaskId(set.getApplicationStatus().getTaskId());
		}

		entity.setTenantId(request.getTenantId());

		entity.setEntityId(request.getEntityId());

		entity.setCrDate(now);

		return entity;
	}

	private void prepareAttributeMappings(ServiceCombinationConfigurationDTO request, CombinationSetDTO set,
			String mappingType, List<AttributeMappingDTO> attributes, List<ServiceCorrelationAttrMapping> result,
			Date now) {

		if (attributes == null || attributes.isEmpty()) {
			return;
		}

		for (AttributeMappingDTO attribute : attributes) {

			if (attribute == null) {
				continue;
			}

			ServiceCorrelationAttrMapping entity = new ServiceCorrelationAttrMapping();

			entity.setBaseServiceId(request.getBaseServiceId());

			entity.setServiceId(request.getServiceId());

			entity.setTaskId(request.getTaskId());

			entity.setCombinationId(request.getCombinationId());

			entity.setMappedBaseServiceId(set.getBaseServiceId());

			entity.setMappedServiceId(set.getServiceId());

			entity.setMappingType(mappingType);

			entity.setMappedSetId(set.getSetId());

			entity.setOutputSetId(set.getOutputSetId());

			entity.setAttrId(attribute.getAttributeId());

			entity.setMappedAttrId(attribute.getMappedAttributeId());

			entity.setTenantId(request.getTenantId());

			entity.setEntityId(request.getEntityId());

			entity.setCrDate(now);

			result.add(entity);
		}
	}

	private void validateRequest(ServiceCombinationConfigurationDTO request) {

		if (request == null) {
			throw new IllegalArgumentException("Service combination request cannot be null");
		}

		if (request.getServiceId() == null) {
			throw new IllegalArgumentException("serviceId is required");
		}

		if (request.getBaseServiceId() == null) {
			throw new IllegalArgumentException("baseServiceId is required");
		}

		if (request.getTaskId() == null || request.getTaskId().trim().isEmpty()) {

			throw new IllegalArgumentException("taskId is required");
		}

		if (request.getCombinationId() == null) {
			throw new IllegalArgumentException("combinationId is required");
		}

		if (request.getTenantId() == null || request.getTenantId().trim().isEmpty()) {

			throw new IllegalArgumentException("tenantId is required");
		}

		if (request.getEntityId() == null) {
			throw new IllegalArgumentException("entityId is required");
		}

		if (request.getSets() == null || request.getSets().isEmpty()) {

			throw new IllegalArgumentException("At least one set is required");
		}
	}
}