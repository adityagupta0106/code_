package com.serviceplus.tracking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.tracking.dto.SentBoxSearchRequest;
import com.serviceplus.tracking.dto.ServerSidePaginationRecord;
import com.serviceplus.tracking.service.UserSentBoxService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/a/sentbox")
public class UserSentBoxController {

    private final UserSentBoxService service;

    @Autowired
    public UserSentBoxController(UserSentBoxService service) {
        this.service = service;
    }

    @PostMapping("/fetch")
    public ServerSidePaginationRecord<?> getSentBox(
            @RequestBody SentBoxSearchRequest requestDTO,
            HttpServletRequest request) {

        return service.getSentBox(
                requestDTO,
                request);
    }
    
	@PostMapping("/filter/list")
	public ServerSidePaginationRecord<?> getFilterApplications(@RequestBody SentBoxSearchRequest requestDTO,
			HttpServletRequest request) {

		return service.getFilterApplications(requestDTO, request);
	}
}
