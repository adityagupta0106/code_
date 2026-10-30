package com.serviceplus.formDesigner.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.dto.PublishFormDTO;

@Service
public class FormPublishKafkaProducer {

	private static final Logger LOG = LogManager.getLogger("formSubscription");
	private static final String TOPIC = "form-publish-events";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public FormPublishKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public void publishFormPublishedEvent(PublishFormDTO dto) {
		try {
			String message = objectMapper.writeValueAsString(dto);

			kafkaTemplate.send(TOPIC, dto.getPublishFormId(), message).whenComplete((result, ex) -> {
				if (ex != null) {
					LOG.error("Failed to publish form-publish event for formId {}: {}", dto.getPublishFormId(),
							ex.getMessage());
				} else {
					LOG.info("Published form-publish event for formId {}", dto.getPublishFormId());
				}
			});
		} catch (Exception e) {
			LOG.error("Failed to serialize PublishFormDTO", e);
		}
	}
}
