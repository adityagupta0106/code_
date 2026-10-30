package com.serviceplus.tracking.feignClient;

import com.serviceplus.tracking.dto.WorkflowAssignmentKafkaEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "SERVICEMETADATA", path = "/services", url = "${servicemetadata.service.url}")
public interface ServiceMetadataFeignClient {

    @GetMapping("/b/workflow-assignment/cache/user/{userId}/location/{locationId}")
    List<WorkflowAssignmentKafkaEvent> getAssignmentsForUser(@PathVariable("userId") Long userId, @PathVariable("locationId") String locationId);

    @GetMapping("/b/workflow-assignment/cache/service/{serviceId}")
    List<WorkflowAssignmentKafkaEvent> getAssignmentsForService(@PathVariable("serviceId") Integer serviceId);

    @GetMapping("/b/workflow-assignment/cache/lookup")
    WorkflowAssignmentKafkaEvent lookupAssignment(@RequestParam("serviceId") Integer serviceId, @RequestParam("taskId") String taskId, @RequestParam("userId") Long userId, @RequestParam("locationId") String locationId, @RequestHeader Map<String, String> headers);

    @GetMapping("/internal/tracking/configured-services")
    List<Map<String, Object>> getConfigureServices(@RequestParam("tenantId") String tenantId);

    @GetMapping("/internal/tracking/configuration")
    Map<String, Object> getPublicTrackingConfiguration(@RequestParam("serviceId") Long serviceId,
            @RequestParam("tenantId") String tenantId);
}
