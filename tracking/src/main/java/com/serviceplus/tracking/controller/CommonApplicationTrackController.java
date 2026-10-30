package com.serviceplus.tracking.controller;

import com.serviceplus.tracking.dto.ApplicationTrackingRequest;
import com.serviceplus.tracking.dto.ApplicationTrackingResponse;
import com.serviceplus.tracking.service.ApplicationTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/m")
public class CommonApplicationTrackController {

    private final ApplicationTrackingService applicationTrackingService;

    public CommonApplicationTrackController(ApplicationTrackingService applicationTrackingService) {
        this.applicationTrackingService = applicationTrackingService;
    }

    @PostMapping("/appl/track/fetch")
    public ApplicationTrackingResponse fetchTracking(
            @RequestBody ApplicationTrackingRequest requestDTO,
            HttpServletRequest request) {

        return applicationTrackingService.commonTrack(requestDTO, request);
    }
}
