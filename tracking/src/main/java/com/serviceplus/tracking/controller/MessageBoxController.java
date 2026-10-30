package com.serviceplus.tracking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.tracking.service.MessageBoxService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/a/message-box")
public class MessageBoxController {

	private final MessageBoxService service;

	public MessageBoxController(MessageBoxService service) {
		this.service = service;
	}

	@GetMapping("/list")
	public ResponseEntity<?> getMessages(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) Integer serviceId,
			@RequestParam(required = false) Boolean isRead, @RequestParam(required = false) String applRefNo,
			HttpServletRequest request) {

		return ResponseEntity.ok(service.getMessages(request, page, size, serviceId, isRead, applRefNo));
	}

	@GetMapping("/detail")
	public ResponseEntity<?> detail(@RequestParam Long messageId, HttpServletRequest request) {

		return ResponseEntity.ok(service.getMessage(messageId, request));
	}

	@PostMapping("/read")
	public ResponseEntity<?> markRead(@RequestParam Long messageId, HttpServletRequest request) {

		service.markRead(messageId, request);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/clear")
	public ResponseEntity<?> clear(@RequestParam Long messageId, HttpServletRequest request) {

		service.clear(messageId, request);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/unread-count")
	public ResponseEntity<?> unreadCount(HttpServletRequest request) {

		return ResponseEntity.ok(service.getUnreadCount(request));
	}
}
