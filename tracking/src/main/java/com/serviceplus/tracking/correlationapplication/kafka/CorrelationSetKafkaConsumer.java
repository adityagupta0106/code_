package com.serviceplus.tracking.correlationapplication.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.tracking.correlationConfig.dto.CorrelationSetKafkaDTO;
import com.serviceplus.tracking.correlationmapping.service.ServiceCorrelationSetService;

@Service
public class CorrelationSetKafkaConsumer {

	private static final Logger kafkaLogger = LogManager.getLogger("KafkaLogger");

	private final ObjectMapper objectMapper;
	private final ServiceCorrelationSetService correlationSetService;

	public CorrelationSetKafkaConsumer(ObjectMapper objectMapper, ServiceCorrelationSetService correlationSetService) {

		this.objectMapper = objectMapper;
		this.correlationSetService = correlationSetService;
	}

	@KafkaListener(topics = "${correlation.set.kafka.topic}", groupId = "${correlation.set.kafka.consumer.group-id}")
	public void consumeCorrelationSet(String message) {

		kafkaLogger.info("Kafka message received from correlation-set-topic: {}", message);

		try {

			CorrelationSetKafkaDTO correlationSetKafkaDTO = objectMapper.readValue(message,
					CorrelationSetKafkaDTO.class);

			kafkaLogger.info("Correlation set received successfully. " + "serviceId={}, baseServiceId={}, setCount={}",
					correlationSetKafkaDTO.getServiceId(), correlationSetKafkaDTO.getBaseServiceId(),
					correlationSetKafkaDTO.getSets() != null ? correlationSetKafkaDTO.getSets().size() : 0);

			processCorrelationSet(correlationSetKafkaDTO);

			kafkaLogger.info("Correlation set processing completed successfully. " + "serviceId={}",
					correlationSetKafkaDTO.getServiceId());

		} catch (Exception e) {

			kafkaLogger.error("Failed to process correlation set Kafka message", e);
		}
	}

	private void processCorrelationSet(CorrelationSetKafkaDTO correlationSetKafkaDTO) {

		if (correlationSetKafkaDTO == null) {

			kafkaLogger.warn("Correlation set DTO is null. Skipping processing.");

			return;
		}

		Long serviceId = correlationSetKafkaDTO.getServiceId();

		Long baseServiceId = correlationSetKafkaDTO.getBaseServiceId();

		String tenantId = correlationSetKafkaDTO.getTenantId();

		Integer entityId = correlationSetKafkaDTO.getEntityId();

		kafkaLogger.info(
				"Correlation set processing started. " + "serviceId={}, baseServiceId={}, tenantId={}, entityId={}",
				serviceId, baseServiceId, tenantId, entityId);

		if (correlationSetKafkaDTO.getSets() == null || correlationSetKafkaDTO.getSets().isEmpty()) {

			kafkaLogger.warn("No correlation sets found. " + "serviceId={}, baseServiceId={}", serviceId,
					baseServiceId);

			return;
		}

		correlationSetService.saveOrUpdateCorrelationSet(correlationSetKafkaDTO);
	}
}