package com.serviceplus.tracking.controller;

import com.serviceplus.tracking.dto.ApplicationTrackingResponse;
import com.serviceplus.tracking.dto.ApplicationTrackingRequest;
import com.serviceplus.tracking.service.ApplicationTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/o")
public class PublicApiController {

    private final ApplicationTrackingService applicationTrackingService;

    @Autowired
    public PublicApiController(ApplicationTrackingService applicationTrackingService) {
        this.applicationTrackingService = applicationTrackingService;
    }

    @RequestMapping(value = "/appl/track/public" , method = RequestMethod.POST )
    public ApplicationTrackingResponse trackApplication(
            @RequestBody ApplicationTrackingRequest requestDTO, HttpServletRequest request) {

        return applicationTrackingService.trackPublicApplication(requestDTO,request);
    }
    
    @GetMapping("/appl/track/services")
    public ResponseEntity<List<Map<String, Object>>> getConfiguredServices(HttpServletRequest request) {
        List<Map<String, Object>> services =applicationTrackingService.getConfiguredServices(request);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/appl/track/services/config")
    public ResponseEntity<Map<String, Object>> getTrackingConfig(@RequestParam("serviceId") Long serviceId, HttpServletRequest request) {
        Map<String, Object> config =applicationTrackingService.getPublicTrackingConfig(serviceId, request);

        return ResponseEntity.ok(config);
    }
    @PostMapping("/appl/track/otp/send")
    public Map<String,Object> sendOtp(@RequestBody Map<String,Object> body,HttpServletRequest request) {
        return applicationTrackingService.sendServiceTrackingOtp(body,request);
    }
    @PostMapping("/appl/track/otp/verify")
    public Map<String,Object> verifyOtp(@RequestBody Map<String,Object> body,HttpServletRequest request) {
        return applicationTrackingService.verifyServiceTrackingOtp(body,request);
    }
    @PostMapping("/appl/track/attributes/verify")
    public Map<String,Object> verifyAttributes(@RequestBody Map<String,Object> body,HttpServletRequest request) {
        return applicationTrackingService.verifyServiceTrackingAttributes(body,request);
    }

    @GetMapping("/appl/track/services/{serviceId}/config")
    public ResponseEntity<Map<String, Object>> getTrackingConfigByServiceId(
            @PathVariable("serviceId") Long serviceId, HttpServletRequest request) {
        return ResponseEntity.ok(applicationTrackingService.getPublicTrackingConfig(serviceId, request));
    }
}
