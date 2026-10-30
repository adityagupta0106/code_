package com.serviceplus.tracking.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.tracking.dto.FilterKafkaMessage;
import com.serviceplus.tracking.dto.FilterKafkaMessage.FilterKafkaDTO;
import com.serviceplus.tracking.entity.InboxSentBoxFilterConfig;
import com.serviceplus.tracking.repository.InboxSentBoxFilterConfigRepository;

@Service
public class ServiceFilterConfigService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceFilterConfigService.class);

	@Value("${service.filter.config.audit-log-enabled:true}")
	private boolean auditLogEnabled;

	private final InboxSentBoxFilterConfigRepository repository;

	public ServiceFilterConfigService(InboxSentBoxFilterConfigRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public void persistMessage(FilterKafkaMessage message) {

		if (message == null || message.getConfigurations() == null || message.getConfigurations().isEmpty()) {

			logger.warn("Received null or empty filter configuration message");
			return;
		}

		List<FilterKafkaDTO> configurations = message.getConfigurations();

		// All DTOs belong to the same filter
		FilterKafkaDTO first = configurations.get(0);

		Long serviceId = first.getServiceId();
		Long filterId = first.getFilterId();

		logger.info("Processing {} filter configuration records. serviceId={}, filterId={}", configurations.size(),
				serviceId, filterId);

		List<InboxSentBoxFilterConfig> existingConfigs = repository.findByServiceIdAndFilterId(serviceId, filterId);

		if (!existingConfigs.isEmpty()) {

			if (auditLogEnabled) {
				saveAuditLogs(existingConfigs, "UPDATE");
			}

			repository.deleteAllInBatch(existingConfigs);
			repository.flush();
		}

		List<InboxSentBoxFilterConfig> entities = configurations.stream().map(this::buildEntity).toList();

		repository.saveAll(entities);
		repository.flush();

		logger.info("Inserted {} configuration records. serviceId={}, filterId={}", entities.size(), serviceId,
				filterId);
	}

	private InboxSentBoxFilterConfig buildEntity(FilterKafkaDTO dto) {

		InboxSentBoxFilterConfig entity = new InboxSentBoxFilterConfig();

		entity.setFilterId(dto.getFilterId());
		entity.setBaseServiceId(dto.getBaseServiceId());
		entity.setServiceId(dto.getServiceId());
		entity.setTenantId(dto.getTenantId());

		entity.setTaskId(dto.getTaskId());
		entity.setFilterType(dto.getFilterType());
		entity.setAttrType(dto.getAttrType());
		if(dto.getAttrType().equals('O')) {
			entity.setAttrLabel(dto.getAttrLabel());
			
		}else {
			entity.setFilterCondition(dto.getFilterCondition());
			entity.setAttrFormId(dto.getAttrFormId());
		}
		entity.setAttrId(dto.getAttrId());
		entity.setAttrOrder(dto.getAttrOrder());
		entity.setCrDate(new Date());
		entity.setUpDate(new Date());

		return entity;
	}

	private void saveAuditLogs(List<InboxSentBoxFilterConfig> configs, String operation) {

		logger.info("Saved {} audit log record(s), operation={}", 0, operation);
	}
}