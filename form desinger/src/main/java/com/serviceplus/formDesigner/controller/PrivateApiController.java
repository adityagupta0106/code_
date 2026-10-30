package com.serviceplus.formDesigner.controller;

import com.serviceplus.formDesigner.dto.WorkflowAttributeRequest;
import com.serviceplus.formDesigner.service.ApplicationDataService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/b")
public class PrivateApiController {

    private static final Logger LOGGER = LogManager.getLogger("applicationDataLogger");

    private final ApplicationDataService applicationDataService;

    public PrivateApiController(ApplicationDataService applicationDataService) {
        this.applicationDataService = applicationDataService;
    }

    @PostMapping("/workflow/attributes")
    public ResponseEntity<?> fetchWorkflowAttributes(@RequestBody WorkflowAttributeRequest request) throws Exception {
        LOGGER.info("Workflow attributes API called | formId={} | dataId={}", request.getFormId(), request.getDataId());

        ResponseEntity<?> response = applicationDataService.fetchWorkflowAttributes(request);

        LOGGER.info("Workflow attributes API completed | formId={} | dataId={} | status={}", request.getFormId(), request.getDataId(), response.getStatusCode());

        return response;
    }
}
