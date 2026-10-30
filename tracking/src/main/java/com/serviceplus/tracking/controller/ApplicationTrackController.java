package com.serviceplus.tracking.controller;

import com.serviceplus.tracking.dto.*;
import com.serviceplus.tracking.service.ApplicationTrackingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.serviceplus.tracking.utility.CommonUtil.getUserSessionDetails;
import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/a")
public class ApplicationTrackController {

    private final ApplicationTrackingService applicationTrackingService;

    @Autowired
    public ApplicationTrackController(ApplicationTrackingService applicationTrackingService) {
        this.applicationTrackingService = applicationTrackingService;
    }

    @PostMapping("/appl/track/fetch")
    public ApplicationTrackingResponse fetchTracking(
            @RequestBody ApplicationTrackingRequest requestDTO,
            HttpServletRequest request) {

        return applicationTrackingService.getTracking(requestDTO, request);
    }

    @PostMapping("/appl/track/list")
    public ServerSidePaginationRecord<Applications> trackList(
            @RequestBody ApplicationSearchRequest requestDTO,
            HttpServletRequest request) {

        return applicationTrackingService.trackList(requestDTO, request);
    }

    @GetMapping("/application/document")
    public DocumentDownloadResponse getDocument(
            @RequestParam("documentId") Long documentId,
            HttpServletRequest request) {

        UserSessionDTO user = getUserSessionDetails(request);
        return  applicationTrackingService.getDocument(documentId, user);

    }

    @PostMapping("/appl/applicant/inbox")
    public ServerSidePaginationRecord<?> getApplications(@RequestBody ApplicationTrackingFilter filter, HttpServletRequest request) {

        UserSessionDTO user = requireNonNull(getUserSessionDetails(request));
        return applicationTrackingService.getApplicantApplications(filter, user);
    }

}
