package com.serviceplus.metadata.feignClient;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PAYMENTSERVICE", path = "/payment")
public interface PaymentGatewayFeignClient {

    @GetMapping("/b/gateways/{gatewayCode}/configuration")
    ResponseEntity<JsonNode> getGatewayConfiguration(@PathVariable("gatewayCode") String gatewayCode);
}