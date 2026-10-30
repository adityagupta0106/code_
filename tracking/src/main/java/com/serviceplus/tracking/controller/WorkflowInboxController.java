package com.serviceplus.tracking.controller;

import com.serviceplus.tracking.dto.InboxApplReqDTO;
import com.serviceplus.tracking.service.WorkflowInboxService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/a/workflow/inbox")
public class WorkflowInboxController {

    @Autowired
    private WorkflowInboxService workflowInboxService;

    @GetMapping("/list")
    public ResponseEntity<?> getInbox(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            HttpServletRequest request) {

        return ResponseEntity.ok(
                workflowInboxService.getInboxApplications(request,page,size));
    }

	@PostMapping("/filter/applications")
	public ResponseEntity<?> getFilterApplications(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "30") int size
			, @RequestBody InboxApplReqDTO inboxApplReqDTO, HttpServletRequest request) {
		return ResponseEntity.ok( workflowInboxService.getFilterApplications(inboxApplReqDTO, page, size, request));
	}
}
