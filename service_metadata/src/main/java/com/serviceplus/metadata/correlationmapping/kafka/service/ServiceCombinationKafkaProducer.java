package com.serviceplus.metadata.correlationmapping.kafka.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.correlationmapping.dto.ServiceCombinationConfigurationDTO;

@Service
public class ServiceCombinationKafkaProducer {

	private static final Logger kafkaLogger = LogManager.getLogger("KafkaLogger");

	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper;

	private final String correlationCombinationTopic;

	private final String correlationCombinationGroupId;

	public ServiceCombinationKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
			@Value("${correlation.combination.kafka.topic}") String correlationCombinationTopic,
			@Value("${correlation.combination.kafka.consumer.group-id}") String correlationCombinationGroupId) {

		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.correlationCombinationTopic = correlationCombinationTopic;
		this.correlationCombinationGroupId = correlationCombinationGroupId;
	}

	public void publishServiceCombination(List<ServiceCombinationConfigurationDTO> configurations) {

		if (configurations == null || configurations.isEmpty()) {

			kafkaLogger.warn("No service combination configuration available for Kafka publishing");

			return;
		}

		for (ServiceCombinationConfigurationDTO configuration : configurations) {

			if (configuration == null) {
				continue;
			}

			String key = String.valueOf(configuration.getServiceId());

			String message;

			try {

				message = objectMapper.writeValueAsString(configuration);
				kafkaLogger.info("ServiceCombinationConfigurationDTO : {}",message);
			} catch (Exception e) {

				kafkaLogger.error("Failed to serialize ServiceCombinationConfigurationDTO for serviceId={}", key, e);

				continue;
			}

			kafkaLogger.info("Publishing service combination to Kafka. topic={}, groupId={}, serviceId={}",
					correlationCombinationTopic, correlationCombinationGroupId, key);

			kafkaTemplate.send(correlationCombinationTopic, key, message).whenComplete((result, ex) -> {

				if (ex != null) {

					kafkaLogger.error("Kafka send failed for serviceId={}, topic={}, groupId={}", key,
							correlationCombinationTopic, correlationCombinationGroupId, ex);

				} else {

					kafkaLogger.info(
							"Kafka send success for serviceId={}, topic={}, groupId={}, partition={}, offset={}", key,
							correlationCombinationTopic, correlationCombinationGroupId,
							result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
				}
			});
		}
	}
}