package com.serviceplus.formDesigner.feignClient;

import java.util.Set;

import com.serviceplus.formDesigner.service.ServiceMetadataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="SERVICEMETADATA",path="/services")
public interface ServiceMataDataFeignClient {

	@GetMapping("/apply/usedAttributeList")
    ResponseEntity<Set<String>> getUsedAttributes( @RequestParam Integer serviceId,@RequestParam String module);

    @PostMapping("/apply/serviceMetaData")
    ResponseEntity<ServiceMetadataDTO> getServiceMetadata(@RequestParam("serviceId") Integer serviceId);
    @GetMapping("/internal/tracking/configuration")
    java.util.Map<String,Object> getPublicTrackingConfiguration(@RequestParam("serviceId") Long serviceId,
        @RequestParam("tenantId") String tenantId);
}
