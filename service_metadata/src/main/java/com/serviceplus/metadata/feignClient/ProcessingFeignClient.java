package com.serviceplus.metadata.feignClient;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PROCESSING", path = "/processing")
public interface ProcessingFeignClient {

    @PostMapping("/a/serviceList")
    List<Map<String, Object>> getServiceList( @RequestHeader("USER-DETAILS") String userDetails
    );
}