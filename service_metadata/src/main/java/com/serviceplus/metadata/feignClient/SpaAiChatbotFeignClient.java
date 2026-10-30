package com.serviceplus.metadata.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.serviceplus.metadata.catalogue.dto.ServiceCatalogueChatbotRequestDTO;

@FeignClient(name = "AICHATBOT", path = "/aichatbot")
public interface SpaAiChatbotFeignClient {

    @PostMapping("/service-catalogue")
    ResponseEntity<?> updateServiceCatalogueConfiguration(
            @RequestBody ServiceCatalogueChatbotRequestDTO request);
}
