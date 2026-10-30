package com.serviceplus.metadata.correlationConfig.kafka.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.correlationConfig.dto.CorrelationSetDTO;
import com.serviceplus.metadata.correlationConfig.dto.CorrelationSetKafkaDTO;

@Service
public class CorrelationSetKafkaProducer {

	private static final Logger kafkaLogger = LogManager.getLogger("KafkaLogger");

	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper;

	private final String correlationSetTopic;

	private final String correlationSetGroupId;

	public CorrelationSetKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
			@Value("${correlation.set.kafka.topic}") String correlationSetTopic,
			@Value("${correlation.set.kafka.consumer.group-id}") String correlationSetGroupId) {

		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.correlationSetTopic = correlationSetTopic;
		this.correlationSetGroupId = correlationSetGroupId;
	}

	public void publishCorrelationSetDTO(List<CorrelationSetDTO> correlationDataSet) {

		if (correlationDataSet == null || correlationDataSet.isEmpty()) {

			kafkaLogger.warn("No correlation set data available for Kafka publishing");

			return;
		}

		CorrelationSetDTO firstSet = correlationDataSet.get(0);

		String key = String.valueOf(firstSet.getServiceId());

		CorrelationSetKafkaDTO kafkaDTO = new CorrelationSetKafkaDTO();

		kafkaDTO.setServiceId(firstSet.getServiceId());

		kafkaDTO.setBaseServiceId(firstSet.getBaseServiceId());

		kafkaDTO.setTenantId(firstSet.getTenantId());

		kafkaDTO.setEntityId(firstSet.getEntityId());

		kafkaDTO.setSets(correlationDataSet);

		String message;

		try {

			message = objectMapper.writeValueAsString(kafkaDTO);
			kafkaLogger.info("CorrelationDataSettDTOs : {}",message);

		} catch (Exception e) {

			kafkaLogger.error("Failed to serialize CorrelationSetKafkaDTO for serviceId={}", key, e);

			return;
		}

		kafkaLogger.info("Publishing correlation set to Kafka. topic={}, groupId={}, serviceId={}", correlationSetTopic,
				correlationSetGroupId, key);

		kafkaTemplate.send(correlationSetTopic, key, message).whenComplete((result, ex) -> {

			if (ex != null) {

				kafkaLogger.error("Kafka send failed for serviceId={}, topic={}, groupId={}", key, correlationSetTopic,
						correlationSetGroupId, ex);

			} else {

				kafkaLogger.info("Kafka send success for serviceId={}, topic={}, groupId={}, partition={}, offset={}",
						key, correlationSetTopic, correlationSetGroupId, result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset());
			}
		});
	}
}