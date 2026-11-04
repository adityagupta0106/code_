package com.serviceplus.formDesigner.controller;

import com.serviceplus.formDesigner.dto.WorkflowAttributeRequest;
import com.serviceplus.formDesigner.service.ApplicationDataService;
import com.serviceplus.formDesigner.service.ApplicationTemplateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/b")
public class PrivateApiController {

    private static final Logger LOGGER = LogManager.getLogger("applicationDataLogger");

    private final ApplicationDataService applicationDataService;
    private final ApplicationTemplateService applicationTemplateService;

    public PrivateApiController(ApplicationDataService applicationDataService,
                                ApplicationTemplateService applicationTemplateService) {
        this.applicationDataService = applicationDataService;
        this.applicationTemplateService = applicationTemplateService;
    }

    @GetMapping("/catalogue/documents")
    public ResponseEntity<?> getCatalogueDocuments(
            @RequestParam("formId") String formId,
            @RequestParam("tenantId") String tenantId) throws Exception {
        return ResponseEntity.ok(applicationTemplateService.getCatalogueDocuments(formId, tenantId));
    }

    @PostMapping("/workflow/attributes")
    public ResponseEntity<?> fetchWorkflowAttributes(@RequestBody WorkflowAttributeRequest request) throws Exception {
        LOGGER.info("Workflow attributes API called | formId={} | dataId={}", request.getFormId(), request.getDataId());

        ResponseEntity<?> response = applicationDataService.fetchWorkflowAttributes(request);

        LOGGER.info("Workflow attributes API completed | formId={} | dataId={} | status={}", request.getFormId(), request.getDataId(), response.getStatusCode());

        return response;
    }
}
