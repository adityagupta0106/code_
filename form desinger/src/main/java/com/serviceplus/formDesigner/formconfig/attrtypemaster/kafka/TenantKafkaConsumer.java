package com.serviceplus.formDesigner.formconfig.attrtypemaster.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.service.TenantInitializationService;

@Component
public class TenantKafkaConsumer {
	private static final Logger LOG = LogManager.getLogger("tenantKafkaConsumer");
	
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TenantInitializationService service;

	@KafkaListener(topics = "kafka.topic.new-tenant-onboard", groupId = "kafka.consumer.group-id")
	public void onFormPublished(String message) {
		String tenantId = "";
		try {
			TenantDomainKafkaEvent event = objectMapper.readValue(message, TenantDomainKafkaEvent.class);
			tenantId = event.getTenantId();
			LOG.info("Received form-publish event for formId {}", tenantId);
			try {
				if ("CREATE".equalsIgnoreCase(event.getAction())) {
					service.initializeTenant(event.getTenantId());
		        }
			} catch (Exception e) {
				LOG.error("Failed processing form-publish event for formId {}: {}", tenantId,
						e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error processing Kafka message: {}", message, e);
		}
	}
}
