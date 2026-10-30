package com.serviceplus.tracking.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.dto.OfficialIntimationKafkaDTO;
import com.serviceplus.tracking.service.MessageBoxService;

@Service
public class OfficialIntimationHandler implements KafkaEventHandler<OfficialIntimationKafkaDTO> {

    @Value("${kafka.topic.official.intimation}")
    private String topic;
    
    private final MessageBoxService messageBoxService;

    public OfficialIntimationHandler(MessageBoxService messageBoxService) {
        this.messageBoxService = messageBoxService;
    }

    @Override
    public String getTopic() {
        return this.topic;
    }

    @Override
    public Class<OfficialIntimationKafkaDTO> getPayloadType() {
        return OfficialIntimationKafkaDTO.class;
    }

    @Override
    public void process(OfficialIntimationKafkaDTO dto) {
        messageBoxService.persistMessage(dto);

    }

}
