package com.serviceplus.metadata.feignClient;

import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarConfiguration;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarConfigurationResponse;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarDefinerConfigurationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.serviceplus.metadata.dto.ConfigurationDTO;


@FeignClient(name = "InstanceConfigRegistry", path = "/configuration")
public interface TenantConfigurationFeignClient {
    final public static String HOST_HEADER = "SP-Client-Domain";

    @PostMapping("/a/find")
    ResponseEntity<ConfigurationDTO> getConfig(@RequestParam(required = false) String tenantId, @RequestHeader(value = HOST_HEADER, required = false) String host);

    @GetMapping("/a/aua/get")
    ResponseEntity<AadhaarConfiguration> getAadhaarConfiguration(@RequestParam String configurationId, @RequestHeader(value = "USER-DETAILS", required = false) String userSession);

    @GetMapping("/a/aua/definer-configuration")
    ResponseEntity<AadhaarDefinerConfigurationResponse> getAadhaarDefinerConfiguration(
            @RequestParam String configurationId,
            @RequestHeader(value = "USER-DETAILS", required = false) String userSession
    );


}
