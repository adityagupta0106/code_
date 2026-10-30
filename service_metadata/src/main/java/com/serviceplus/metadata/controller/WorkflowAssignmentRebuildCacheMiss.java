package com.serviceplus.metadata.controller;

import com.serviceplus.metadata.kafka.dto.WorkflowAssignmentKafkaEvent;
import com.serviceplus.metadata.service.WorkflowAssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/b")
public class WorkflowAssignmentRebuildCacheMiss {

    private final WorkflowAssignmentService workflowAssignmentService;

    @Autowired
    public WorkflowAssignmentRebuildCacheMiss(WorkflowAssignmentService workflowAssignmentService) {
        this.workflowAssignmentService = workflowAssignmentService;
    }

    @GetMapping("/workflow-assignment/cache/user/{userId}/location/{locationId}")
    public ResponseEntity<List<WorkflowAssignmentKafkaEvent>> getAssignmentsForUser(HttpServletRequest request,@PathVariable Long userId,@PathVariable Long locationId) {
        return ResponseEntity.ok(workflowAssignmentService.getAssignmentsForUser(request,userId,locationId));
    }

    @GetMapping("/workflow-assignment/cache/lookup")
    public ResponseEntity<WorkflowAssignmentKafkaEvent> lookupAssignment(@RequestParam Integer serviceId,
                                                                       @RequestParam String taskId,
                                                                       HttpServletRequest request) {

        return ResponseEntity.ok(workflowAssignmentService.getAssignment(serviceId, taskId,request));
    }

    @GetMapping("/workflow-assignment/cache/service/{serviceId}")
    public ResponseEntity<List<WorkflowAssignmentKafkaEvent>> getAssignmentsForService(@PathVariable Integer serviceId) {

        return ResponseEntity.ok(workflowAssignmentService.getAssignmentsForService(serviceId));
    }

}
