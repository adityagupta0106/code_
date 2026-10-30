package com.serviceplus.tracking.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "INSTANCECONFIGREGISTRY", path = "/configuration", url = "${instanceconfigregistry.service.url}")
public interface InstanceConfigRegistryFeignClient {

    @GetMapping(value = "/b/fetchTenantId", produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> fetchTenantId(@RequestHeader Map<String,String> headers);

}
