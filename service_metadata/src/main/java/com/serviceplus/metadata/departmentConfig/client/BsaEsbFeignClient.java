package com.serviceplus.metadata.departmentConfig.client;

import com.serviceplus.metadata.departmentConfig.dto.IntegrationRequest;
import com.serviceplus.metadata.departmentConfig.dto.IntegrationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Integration", path = "/esb")
public interface BsaEsbFeignClient {

    @PostMapping("/b/invoke")
    IntegrationResponse invoke(@RequestBody IntegrationRequest request);
}
