package com.serviceplus.tracking.controller;

import static com.serviceplus.tracking.utility.CommonUtil.extractHeaders;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.tracking.dto.ActionTinyUrlRequestDTO;
import com.serviceplus.tracking.dto.DocumentTinyUrlRequestDTO;
import com.serviceplus.tracking.dto.DocumentTinyUrlResponseDTO;
import com.serviceplus.tracking.service.ActionTinyUrlService;
import com.serviceplus.tracking.service.DocumentTinyUrlService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/m")
public class TinyUrlController {

	private final DocumentTinyUrlService documentTinyUrlService;
	
	private final ActionTinyUrlService actionTinyUrlService;

	public TinyUrlController(DocumentTinyUrlService documentTinyUrlService, ActionTinyUrlService actionTinyUrlService) {
		this.documentTinyUrlService = documentTinyUrlService;
		this.actionTinyUrlService = actionTinyUrlService;
	}

	@PostMapping("/tiny-url/document")
	public ResponseEntity<?> resolveDocumentTinyUrl(@RequestBody DocumentTinyUrlRequestDTO request,
			HttpServletRequest httpServletRequest) {

		Map<String, Object> headers = extractHeaders(httpServletRequest);

		return documentTinyUrlService.resolveDocumentTinyUrl(request, headers, httpServletRequest);
	}
	
	@PostMapping("/tiny-url/action")
	public ResponseEntity<?> resolveActionTinyUrl(@RequestBody ActionTinyUrlRequestDTO request,
			HttpServletRequest httpRequest) {

		Map<String, Object> headers = extractHeaders(httpRequest);

		return actionTinyUrlService.resolveActionTinyUrl(request, headers, httpRequest);
	}

	
}
