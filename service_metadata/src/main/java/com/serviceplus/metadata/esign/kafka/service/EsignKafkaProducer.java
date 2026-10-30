package com.serviceplus.metadata.esign.kafka.service;

import com.serviceplus.metadata.esign.dto.EsignConfigurationKafkaDTO;
import com.serviceplus.metadata.kafka.KafkaProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EsignKafkaProducer {

    private static final Logger log = LogManager.getLogger("esignKafkaLogger");

    private final KafkaProducer kafkaProducer;
    
    private final String esignTopic;

    public EsignKafkaProducer(KafkaProducer kafkaProducer,
    		@Value("${kafka.topic.esign-configuration:service.esign.configuration}")String esignTopic) {
        this.kafkaProducer = kafkaProducer;
        this.esignTopic = esignTopic;
    }

    public void publishFreezeEvent(EsignConfigurationKafkaDTO event) {
        if (event == null || event.getServiceId() == null) {
            log.warn("Cannot publish eSign freeze event: event or serviceId is null");
            return;
        }
        event.setEventType("FREEZE");
        String key = String.valueOf(event.getServiceId());
        log.info("Publishing eSign FREEZE event for serviceId: {} to topic: {}", key, esignTopic);
        kafkaProducer.sendMessage(esignTopic, key, event);
    }

    public void publishUnfreezeEvent(Integer serviceId, String tenantId) {
        if (serviceId == null) {
            log.warn("Cannot publish eSign unfreeze event: serviceId is null");
            return;
        }
        EsignConfigurationKafkaDTO event = new EsignConfigurationKafkaDTO();
        event.setEventType("UNFREEZE");
        event.setServiceId(serviceId);
        event.setTenantId(tenantId);
        String key = String.valueOf(serviceId);
        log.info("Publishing eSign UNFREEZE event for serviceId: {} to topic: {}", key, esignTopic);
        kafkaProducer.sendMessage(esignTopic, key, event);
    }
}
