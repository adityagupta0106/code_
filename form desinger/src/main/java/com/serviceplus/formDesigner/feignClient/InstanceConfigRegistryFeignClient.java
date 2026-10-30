package com.serviceplus.formDesigner.feignClient;

import java.util.Map;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "INSTANCECONFIGREGISTRY",path = "/configuration")
public interface InstanceConfigRegistryFeignClient {

    @GetMapping(value = "/b/fetchTenantId", produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> fetchTenantId(@RequestHeader Map<String,String> headers);

}
