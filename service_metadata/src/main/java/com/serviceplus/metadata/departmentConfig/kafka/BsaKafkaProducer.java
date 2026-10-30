package com.serviceplus.metadata.departmentConfig.kafka;

import com.serviceplus.metadata.departmentConfig.dto.BsaConfigurationKafkaDTO;
import com.serviceplus.metadata.kafka.KafkaProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BsaKafkaProducer {

    private static final Logger log = LogManager.getLogger("bsaKafkaLogger");

    private final KafkaProducer kafkaProducer;
    private final String bsaTopic;

    public BsaKafkaProducer(KafkaProducer kafkaProducer,
                            @Value("${kafka.topic.bsa-configuration:service.bsa.configuration}") String bsaTopic) {
        this.kafkaProducer = kafkaProducer;
        this.bsaTopic = bsaTopic;
    }

    public void publishFreezeEvent(BsaConfigurationKafkaDTO event) {
        if (event == null || event.getServiceId() == null) {
            log.warn("Cannot publish BSA freeze event: event or serviceId is null");
            return;
        }
        event.setEventType("FREEZE");
        String key = String.valueOf(event.getServiceId());
        log.info("Publishing BSA FREEZE event for serviceId: {} to topic: {}", key, bsaTopic);
        kafkaProducer.sendMessage(bsaTopic, key, event);
    }

    public void publishUnfreezeEvent(Integer serviceId, String tenantId) {
        if (serviceId == null) {
            log.warn("Cannot publish BSA unfreeze event: serviceId is null");
            return;
        }
        BsaConfigurationKafkaDTO event = new BsaConfigurationKafkaDTO();
        event.setEventType("UNFREEZE");
        event.setServiceId(serviceId);
        event.setTenantId(tenantId);
        String key = String.valueOf(serviceId);
        log.info("Publishing BSA UNFREEZE event for serviceId: {} to topic: {}", key, bsaTopic);
        kafkaProducer.sendMessage(bsaTopic, key, event);
    }
}
