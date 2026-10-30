package com.serviceplus.tracking.correlationapplication.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.tracking.correlationmapping.dto.ServiceCombinationConfigurationDTO;
import com.serviceplus.tracking.correlationmapping.service.ServiceCombinationService;

@Service
public class ServiceCombinationKafkaConsumer {

	private static final Logger kafkaLogger = LogManager.getLogger("KafkaLogger");

	private final ObjectMapper objectMapper;
	private final ServiceCombinationService service;

	public ServiceCombinationKafkaConsumer(ObjectMapper objectMapper, ServiceCombinationService service) {

		this.objectMapper = objectMapper;
		this.service = service;
	}

	@KafkaListener(topics = "${correlation.combination.kafka.topic}", groupId = "${correlation.combination.kafka.consumer.group-id}")
	public void consumeServiceCombination(String message) {

		kafkaLogger.info("Kafka message received from correlation-combination-topic: {}", message);

		try {

			ServiceCombinationConfigurationDTO configurationDTO = objectMapper.readValue(message,
					ServiceCombinationConfigurationDTO.class);

			kafkaLogger.info(
					"Service combination received successfully. "
							+ "serviceId={}, combinationId={}, taskId={}, setCount={}",
					configurationDTO.getServiceId(), configurationDTO.getCombinationId(), configurationDTO.getTaskId(),
					configurationDTO.getSets() != null ? configurationDTO.getSets().size() : 0);

			service.saveServiceCombination(configurationDTO);

			kafkaLogger.info(
					"Service combination processed successfully. " + "serviceId={}, combinationId={}, taskId={}",
					configurationDTO.getServiceId(), configurationDTO.getCombinationId(), configurationDTO.getTaskId());

		} catch (Exception e) {

			kafkaLogger.error("Failed to process service combination Kafka message", e);

			throw new RuntimeException("Failed to process service combination", e);
		}
	}
}