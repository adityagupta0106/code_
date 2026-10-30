package com.serviceplus.metadata.formrecommendation.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.service.RecommendedServiceFormKafkaDTO;

@Service
public class RecommendedFormKafkaProducer {
	private static final Logger kafkaLogger = LogManager.getLogger("KafkaLogger");
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	private static final String RECOMMENDED_FORM_TOPIC = "servicemgmt-recommended-form-topic";

	public RecommendedFormKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public void publishRecommendedForm(RecommendedServiceFormKafkaDTO dto) {
		String key = String.valueOf(dto.getServiceId());
		String message;

		try {
			message = objectMapper.writeValueAsString(dto);
		} catch (Exception e) {
			kafkaLogger.error("Failed to serialize RecommendedServiceFormKafkaDTO for serviceId={}", key, e);
			return;
		}

		kafkaTemplate.send(RECOMMENDED_FORM_TOPIC, key, message).whenComplete((result, ex) -> {
			if (ex != null) {
				kafkaLogger.error("Kafka send failed for serviceId={}", key, ex);
			} else {
				kafkaLogger.info("Kafka send success for serviceId={} partition={} offset={}", key,
						result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
			}
		});
	}

}
