package com.serviceplus.formDesigner.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.dto.RecommendedFormEventDTO;

@Service 
public class RecommendedFormKafkaProducer {
	private static final Logger kafkaLogger = LogManager.getLogger("KafkaLogger");
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	private static final String RECOMMENDED_FORM_TOPIC = "formmgmt-recommended-form-topic";

	public RecommendedFormKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public void publishRecommendedForm(RecommendedFormEventDTO dto) {
		String key = dto.getFormId();
		String message;

		try {
			message = objectMapper.writeValueAsString(dto);
		} catch (Exception e) {
			kafkaLogger.error("Failed to serialize RecommendedFormEventDTO for formId={}", key, e);
			return;
		}

		kafkaTemplate.send(RECOMMENDED_FORM_TOPIC, key, message).whenComplete((result, ex) -> {
			if (ex != null) {
				kafkaLogger.error("Kafka send failed for formId={}", key, ex);
			} else {
				kafkaLogger.info("Kafka send success for formId={} partition={} offset={}", key,
						result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
			}
		});
	}

}
