package com.serviceplus.metadata.formSubscription.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.formSubscription.dto.PublishFormDTO;
import com.serviceplus.metadata.formSubscription.service.FormSubscriptionService;

@Component
public class FormPublishKafkaConsumer {

	private static final Logger LOG = LogManager.getLogger("formSubscription");

	@Autowired
	private FormSubscriptionService formSubscriptionService;

	@Autowired
	private ObjectMapper objectMapper;

	@KafkaListener(topics = "form-publish-events", groupId = "metadata-service-form-publish")
	public void onFormPublished(String message) {

		try {
			PublishFormDTO dto = objectMapper.readValue(message, PublishFormDTO.class);
			LOG.info("Received form-publish event for formId {}", dto.getPublishFormId());
			try {
				formSubscriptionService.formPublish(dto);
			} catch (Exception e) {
				LOG.error("Failed processing form-publish event for formId {}: {}", dto.getPublishFormId(),
						e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error processing Kafka message: {}", message, e);
		}
	}
}
