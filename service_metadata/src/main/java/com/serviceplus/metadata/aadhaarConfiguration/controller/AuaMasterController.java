package com.serviceplus.metadata.aadhaarConfiguration.controller;

import com.serviceplus.metadata.aadhaarConfiguration.dto.AuaMastersResponse;
import com.serviceplus.metadata.aadhaarConfiguration.service.AuaMasterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/a")
public class AuaMasterController {

    private final AuaMasterService auaMasterService;

    public AuaMasterController(AuaMasterService auaMasterService) {
        this.auaMasterService = auaMasterService;
    }

    @GetMapping("/aua/masters")
    public ResponseEntity<AuaMastersResponse> getMasters(HttpServletRequest request) {
        return ResponseEntity.ok(auaMasterService.getMasters(request));
    }
}
