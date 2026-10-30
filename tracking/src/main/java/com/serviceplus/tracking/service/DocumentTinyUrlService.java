package com.serviceplus.tracking.service;

import static com.serviceplus.tracking.utility.CommonUtil.getUserSessionDetails;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.DocumentTinyUrlRequestDTO;
import com.serviceplus.tracking.dto.DocumentTinyUrlResponseDTO;
import com.serviceplus.tracking.dto.SignedUrlResponse;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.entity.ApplicationDocumentTinyUrl;
import com.serviceplus.tracking.feignClient.FileManagementFeignClient;
import com.serviceplus.tracking.repository.ApplicationDocumentTinyUrlRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class DocumentTinyUrlService {

    private final ApplicationDocumentTinyUrlRepository
            applicationDocumentTinyUrlRepository;

    private final FileManagementFeignClient fileManagementFeignClient;

    public DocumentTinyUrlService(
            ApplicationDocumentTinyUrlRepository
                    applicationDocumentTinyUrlRepository,
            FileManagementFeignClient fileManagementFeignClient) {

        this.applicationDocumentTinyUrlRepository =
                applicationDocumentTinyUrlRepository;

        this.fileManagementFeignClient =
                fileManagementFeignClient;
    }

	public ResponseEntity<?> resolveDocumentTinyUrl(DocumentTinyUrlRequestDTO request,
			Map<String, Object> headers, HttpServletRequest httpRequest) {

		UserSessionDTO userSessionDetails = getUserSessionDetails(httpRequest);

		if (userSessionDetails == null) {
			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
		}

		validateRequest(request);

		String urlKey = request.getUrlKey().trim();

		ApplicationDocumentTinyUrl tinyUrl = applicationDocumentTinyUrlRepository
				.findByUrlKeyAndTenantId(urlKey, userSessionDetails.getTenantId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid document tiny URL"));

		validateAccess(tinyUrl, request);

		SignedUrlResponse signedUrl = fileManagementFeignClient.download(tinyUrl.getDocumentId(), headers);

		if (signedUrl == null) {
			throw new IllegalStateException("Unable to generate document signed URL");
		}

		DocumentTinyUrlResponseDTO response = new DocumentTinyUrlResponseDTO();

		response.setSuccess(true);
		response.setApplicationId(tinyUrl.getApplicationId());
		response.setProcessId(tinyUrl.getProcessId());
		response.setDocumentId(tinyUrl.getDocumentId());

		response.setDocumentUrl(signedUrl.getUrl());
		tinyUrl.setAccessFlag(true);
		applicationDocumentTinyUrlRepository.save(tinyUrl);
		
		return ResponseEntity.ok(response);
	}

	private void validateRequest(DocumentTinyUrlRequestDTO request) {

		if (request == null) {
			throw new IllegalArgumentException("Request is required");
		}

		if (request.getUrlKey() == null || request.getUrlKey().trim().isEmpty()) {
			throw new IllegalArgumentException("URL key is required");
		}
	}

	private void validateAccess(ApplicationDocumentTinyUrl tinyUrl, DocumentTinyUrlRequestDTO request) {

		if (!tinyUrl.isAccessFlag()) {
			return;
		}
		
		if (request.getReferenceNo() == null || request.getReferenceNo().trim().isEmpty()) {
			throw new SPRuntimeError("Reference number is required",HttpStatus.OK);
		}
		
		if (tinyUrl.getReferenceNo() == null || !tinyUrl.getReferenceNo().equals(request.getReferenceNo().trim())) {
			throw new SPRuntimeError("Invalid reference number",HttpStatus.OK);
		}
	}
}
