package com.serviceplus.metadata.service;

import static com.serviceplus.metadata.utility.CommonUtil.entityToString;
import static java.util.Objects.isNull;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.serviceplus.metadata.enums.VersionChangeStatus;
import com.serviceplus.metadata.versioning.entity.ServiceVersionChange;
import com.serviceplus.metadata.versioning.enums.ModificationStatus;
import com.serviceplus.metadata.versioning.repository.ServiceVersionChangeRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.Base64UploadRequest;
import com.serviceplus.metadata.dto.CommitRequest;
import com.serviceplus.metadata.dto.CreateUploadSessionsRequest;
import com.serviceplus.metadata.dto.CreateUploadSessionsResponse;
import com.serviceplus.metadata.dto.DSCSignRequest;
import com.serviceplus.metadata.dto.DSCSignResponse;
import com.serviceplus.metadata.dto.FileViewResponse;
import com.serviceplus.metadata.dto.MoveToProductionRequestDTO;
import com.serviceplus.metadata.dto.ServiceDefinitionDTO;
import com.serviceplus.metadata.dto.ServiceLaunchDocumentResponseDTO;
import com.serviceplus.metadata.dto.ServiceLaunchPendingDTO;
import com.serviceplus.metadata.dto.ServiceLaunchRequestDTO;
import com.serviceplus.metadata.dto.ServiceLaunchResponseDTO;
import com.serviceplus.metadata.dto.UploadStatusResponse;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.dto.FileViewResponse.Data;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.DocumentGenerationFeignClient;
import com.serviceplus.metadata.feignClient.FileMgmtFeignClient;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;

@Service
public class ServiceLaunchService {

	private final IServiceLogRepository serviceLogRepository;
	private final IServiceDefinitionRepository serviceDefinitionRepository;
	private final FileMgmtFeignClient fileMgmtFeignClient;
	private final ObjectMapper mapper;
	private final RestTemplate restTemplate;
	private final DocumentGenerationFeignClient documentGenerationFeignClient;
    private final ServiceVersionChangeRepository versionChangeRepository;
    private final ServiceMetadataService serviceMetadataService;

    public ServiceLaunchService(IServiceLogRepository serviceLogRepository, IServiceDefinitionRepository serviceDefinitionRepository, FileMgmtFeignClient fileMgmtFeignClient, ObjectMapper mapper, RestTemplate restTemplate, DocumentGenerationFeignClient documentGenerationFeignClient, ServiceVersionChangeRepository versionChangeRepository, ServiceMetadataService serviceMetadataService) {
        this.serviceLogRepository = serviceLogRepository;
        this.serviceDefinitionRepository = serviceDefinitionRepository;
        this.fileMgmtFeignClient = fileMgmtFeignClient;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
        this.documentGenerationFeignClient = documentGenerationFeignClient;
        this.versionChangeRepository = versionChangeRepository;
        this.serviceMetadataService = serviceMetadataService;
    }

    @Transactional
	public ServiceLaunchResponseDTO launchService(ServiceLaunchRequestDTO request, UserSessionDTO userSessionDetails) {

		if (request == null || request.getServiceId() == null) {

			throw new SPRuntimeError("Service ID is required", HttpStatus.BAD_REQUEST);
		}

		if (!Boolean.TRUE.equals(request.getDisclaimerAccepted())) {

			throw new SPRuntimeError("Please accept the launch disclaimer", HttpStatus.BAD_REQUEST);
		}

		if (userSessionDetails == null || userSessionDetails.getUserID() == null) {

			throw new SPRuntimeError("Invalid user session", HttpStatus.UNAUTHORIZED);
		}

		Integer serviceId = request.getServiceId();

		ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);

		if (serviceLog == null) {
			throw new SPRuntimeError("Service log not found", HttpStatus.NOT_FOUND);
		}

		if (serviceLog.getServiceStatus() == null || (serviceLog.getServiceStatus() != ServiceStatus.FROZEN.getCode()
				&& serviceLog.getServiceStatus() != ServiceStatus.ACTIVATED.getCode())) {

			throw new SPRuntimeError("Service cannot be launched in the current status", HttpStatus.BAD_REQUEST);
		}

		ServiceDefinition serviceDefinition = serviceDefinitionRepository.findById(serviceId)
				.orElseThrow(() -> new SPRuntimeError("Service definition not found", HttpStatus.NOT_FOUND));

		String launchDocumentHtml = generateLaunchDocument(serviceDefinition, serviceLog,userSessionDetails);
		
		Map<String, String> pdfRequest = new HashMap<>();

		pdfRequest.put("htmlContent", launchDocumentHtml);

		ResponseEntity<Map<String, Object>> pdfResponse = documentGenerationFeignClient.convertHtmlToPdf(pdfRequest);

		if (pdfResponse == null || pdfResponse.getBody() == null) {

			throw new SPRuntimeError("Unable to generate PDF", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Map<String, Object> pdfResult = pdfResponse.getBody();

		String pdfStatus = String.valueOf(pdfResult.get("status"));

		String pdfBase64 = String.valueOf(pdfResult.get("base64"));

		if (!"200".equals(pdfStatus) || pdfBase64 == null || pdfBase64.isBlank()
				|| "null".equalsIgnoreCase(pdfBase64)) {

			throw new SPRuntimeError("Unable to generate launch document PDF", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		String fileId = uploadLaunchDocument(pdfBase64, userSessionDetails, serviceId);
		if (fileId == null || fileId.isBlank()) {
			throw new SPRuntimeError("Unable to upload launch document", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Date currentDate = new Date();
		serviceLog.setLaunchDocFileId(fileId);
		serviceLog.setLaunchedBy(userSessionDetails.getUserID());
		serviceLog.setuDate(currentDate);
		serviceLogRepository.save(serviceLog);

		return new ServiceLaunchResponseDTO(serviceId, pdfBase64,"The launch document has been generated successfully.");
	}

	@Transactional(readOnly = true)
	public List<ServiceLaunchPendingDTO> getPendingLaunches(UserSessionDTO userSessionDetails) {

		if (userSessionDetails == null || userSessionDetails.getUserID() == null) {
			throw new SPRuntimeError("Invalid user session", HttpStatus.UNAUTHORIZED);
		}

		List<ServiceLog> serviceLogs = serviceLogRepository.findByServiceStatus(ServiceStatus.PENDING_APPROVAL.getCode());

		return serviceLogs.stream().map(serviceLog -> {
			ServiceDefinition serviceDefinition = serviceDefinitionRepository.findById(serviceLog.getServiceId()).orElse(null);
			if (serviceDefinition == null) {
				return null;
			}
			if (!isAuthorizedDepartmentAdmin(serviceDefinition, userSessionDetails)) {
				return null;
			}

			ServiceLaunchPendingDTO response = new ServiceLaunchPendingDTO();

			response.setServiceId(serviceDefinition.getServiceId());
			response.setServiceName(serviceDefinition.getServiceName());
			response.setServiceStatus(ServiceStatus.fromCode(serviceLog.getServiceStatus()).name());

			ServiceDefinitionDTO definitionJson = serviceDefinition.getDefinitionJson();

			if (definitionJson != null) {
				response.setLaunchDocumentHtml(generateLaunchDocument(serviceDefinition, serviceLog,userSessionDetails));
			}
			

			return response;
		}).filter(Objects::nonNull).toList();
	}
	
	@Transactional(readOnly = true)
	public ServiceLaunchDocumentResponseDTO getLaunchDocument(Integer serviceId, UserSessionDTO userSessionDetails) {
		String previewUrl = "";
		if (serviceId == null) {
			throw new SPRuntimeError("Service ID is required", HttpStatus.BAD_REQUEST);
		}

		if (userSessionDetails == null || userSessionDetails.getUserID() == null) {

			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
		}

		ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);

		if (serviceLog == null) {
			throw new SPRuntimeError("Service log not found", HttpStatus.NOT_FOUND);
		}

		String fileId = serviceLog.getLaunchDocFileId();

		if (fileId == null || fileId.trim().isEmpty()) {

			throw new SPRuntimeError("Launch document not found", HttpStatus.NOT_FOUND);
		}

		List<String> fileIds = new ArrayList<>();
		fileIds.add(fileId);
		try {
			ResponseEntity<FileViewResponse> fileViewResp = fileMgmtFeignClient
					.filePreview(entityToString(userSessionDetails), new CommitRequest(fileIds));
			FileViewResponse body = fileViewResp.getBody();
			if (isNull(body) || isNull(body.getResults())) {
				throw new SPRuntimeError("Failed to fetch file", HttpStatus.FAILED_DEPENDENCY);
			}
			Map<String, Data> results = body.getResults();
			if (results.containsKey(fileId)) {
				previewUrl = mapper.writeValueAsString(results.get(fileId));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Failed to fetch file", HttpStatus.FAILED_DEPENDENCY);
		}
		String base64 = getFileBase64(previewUrl);

		return new ServiceLaunchDocumentResponseDTO(serviceId, fileId, base64, "application/pdf","Launch document retrieved successfully");
	}
	
	public String getFileBase64(String presignedUrl) {
		try {
			ResponseEntity<byte[]> response = restTemplate.getForEntity(URI.create(presignedUrl), byte[].class);
			if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
				throw new RuntimeException("Presigned URL download failed: " + response.getStatusCode());
			}
			byte[] bytes = response.getBody();
			return Base64.getEncoder().encodeToString(bytes);
		} catch (Exception ex) {
			throw new RuntimeException("Unable to download file from presigned URL", ex);
		}
	}

	@Transactional
	public ResponseEntity<?> rejectLaunch(Integer serviceId, UserSessionDTO userSessionDetails) {

		if (serviceId == null) {
			throw new SPRuntimeError("Service ID is required", HttpStatus.BAD_REQUEST);
		}

		if (userSessionDetails == null || userSessionDetails.getUserID() == null) {

			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
		}
		ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);

		if (serviceLog == null) {
			throw new SPRuntimeError("Service log not found", HttpStatus.NOT_FOUND);
		}

		if (serviceLog.getServiceStatus() == null
				|| serviceLog.getServiceStatus() != ServiceStatus.PENDING_APPROVAL.getCode()) {

			throw new SPRuntimeError("Service is not pending for approval", HttpStatus.BAD_REQUEST);
		}

		ServiceDefinition serviceDefinition = serviceDefinitionRepository.findById(serviceId).orElseThrow(() -> new SPRuntimeError("Service definition not found", HttpStatus.NOT_FOUND));

		if (!isAuthorizedDepartmentAdmin(serviceDefinition, userSessionDetails)) {

			throw new SPRuntimeError("You are not authorized to reject this launch request", HttpStatus.FORBIDDEN);
		}
		serviceLog.setServiceStatus(ServiceStatus.FROZEN.getCode());
		serviceLog.setLaunchFlag('N');
		serviceLog.setLaunchApprovedBy(userSessionDetails.getUserID());
		serviceLog.setuDate(new Date());

		serviceLogRepository.save(serviceLog);
		return ResponseEntity.ok("Service launch request rejected successfully");
	}

	private String generateLaunchDocument(ServiceDefinition serviceDefinition, ServiceLog serviceLog, UserSessionDTO userSessionDetails) {

		try {

			ClassPathResource resource = new ClassPathResource("templates/serviceLaunchDocument.html");
			String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			ServiceDefinitionDTO definitionJson = serviceDefinition.getDefinitionJson();
			if (definitionJson == null) {
				throw new SPRuntimeError("Service definition configuration not found", HttpStatus.BAD_REQUEST);
			}

			html = html.replace("{{Service Name}}", safe(definitionJson.getName()));

			html = html.replace("{{Service Abbreviation}}", safe(definitionJson.getAbbreviation()));

			html = html.replace("{{Service Category Name}}", safe(definitionJson.getCategory().getLabel()));

			html = html.replace("{{Type of Service}}", safe(definitionJson.getType().getLabel()));

//			html = html.replace("{{Registration Configuration}}", getRegistrationConfiguration(definitionJson));

			html = html.replace("{{Service Delivery Act Configuration}}",
					getServiceDeliveryActConfiguration(definitionJson));

			html = html.replace("{{SDG Goals}}", getGoals(definitionJson));

			html = html.replace("{{FIFO Configuration}}", getFifoConfiguration(definitionJson));

			html = html.replace("{{eKYC Configuration}}", getEkYcConfiguration(definitionJson));

			html = html.replace("{{Feedback Configuration}}", getFeedbackConfiguration(definitionJson));

			html = html.replace("{{FAQ Configuration}}", getFaqConfiguration(definitionJson));

			html = html.replace("{{Service Definer Name}}", String.valueOf(userSessionDetails.getUserName()));

			html = html.replace("{{Service Definer Department Name}}",
					String.valueOf(userSessionDetails.getLocationName()));
			
			html = html.replace("{{Designation of Service Definer}}", "");

			html = html.replace("{{Contact Number of Service Definer}}", safe(userSessionDetails.getMobileNo()));

			html = html.replace("{{Email address of Service Definer}}", safe(userSessionDetails.getEmailId()));
			
			html = html.replace("{{Department Admin Name}}", "");

			html = html.replace("{{Designation of Department Admin}}", "");

			html = html.replace("{{Office name of Department Admin}}", "");

			html = html.replace("{{Contact number of Department Admin}}", "");

			html = html.replace("{{email address of Department Admin}}", "");

			return html;

		} catch (IOException e) {

			throw new SPRuntimeError("Unable to generate launch document", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	private String getRegistrationConfiguration(ServiceDefinitionDTO definitionJson) {
//
//		if ("Y".equalsIgnoreCase(definitionJson.getRegisterToAvail())) {
//
//			return "Applicant(s) are required to register to avail this service.";
//		}
//
//		return "Applicant(s) are not required to register to avail this service.";
//	}

	private String getServiceDeliveryActConfiguration(ServiceDefinitionDTO definitionJson) {

		if (!"Y".equalsIgnoreCase(definitionJson.getServiceDeliveryAct())) {

			return "This service is not covered under the 'Service Delivery Act'.";
		}

		return "This service is covered under the 'Service Delivery Act'. " + getSlaConfiguration(definitionJson);
	}

	private String getSlaConfiguration(ServiceDefinitionDTO definitionJson) {

		if (definitionJson.getServiceLevel() == null) {
			return "No SLA has been defined for this service.";
		}

		if (definitionJson.getServiceLevel().getUnit() == null) {
			return "No SLA has been defined for this service.";
		}

		String quantity = definitionJson.getServiceLevel().getQuantity();

		if (quantity == null || quantity.trim().isEmpty()) {

			return "No SLA has been defined for this service.";
		}

		String unitValue = definitionJson.getServiceLevel().getUnit().getValue();

		String unitLabel = definitionJson.getServiceLevel().getUnit().getLabel();

		if ("d".equalsIgnoreCase(unitValue) && "1".equals(quantity)) {

			return "The SLA is Same Day.";
		}

		if ("d".equalsIgnoreCase(unitValue)) {

			return "The SLA is " + quantity + " days.";
		}

		if ("w".equalsIgnoreCase(unitValue)) {

			return "The SLA is " + quantity + " " + unitLabel + ".";
		}

		return "The SLA is " + quantity + " " + unitLabel + ".";
	}

	private String getGoals(ServiceDefinitionDTO definitionJson) {

		if (definitionJson.getGoals() == null || definitionJson.getGoals().isEmpty()) {

			return "None";
		}

		StringBuilder goals = new StringBuilder();

		definitionJson.getGoals().forEach(goal -> {

			if (goal.getLabel() == null || goal.getLabel().trim().isEmpty()) {

				return;
			}

			if (goals.length() > 0) {
				goals.append(", ");
			}

			goals.append(goal.getLabel());
		});

		return goals.length() == 0 ? "None" : goals.toString();
	}

	private String getFifoConfiguration(ServiceDefinitionDTO definitionJson) {

		if ("Y".equalsIgnoreCase(definitionJson.getFifo())) {

			return "FIFO (First in First out) has been enabled for this service.";
		}

		return "FIFO (First in First out) has not been enabled for this service.";
	}

	private String getEkYcConfiguration(ServiceDefinitionDTO definitionJson) {

		if ("Y".equalsIgnoreCase(definitionJson.geteKYC())) {

			return "e-KYC has been enabled as a requirement for this service.";
		}

		return "e-KYC has not been enabled as a requirement for this service.";
	}

	private String getFeedbackConfiguration(ServiceDefinitionDTO definitionJson) {

		if ("Y".equalsIgnoreCase(definitionJson.getFeedback())) {

			return "Feedback from the user has been enabled for this service.";
		}

		return "Feedback from the user has not been enabled for this service.";
	}

	private String getFaqConfiguration(ServiceDefinitionDTO definitionJson) {

		if (definitionJson.getFaqs() == null || definitionJson.getFaqs().isEmpty()) {

			return "No Frequently Asked Questions have been configured for this service.";
		}

		StringBuilder faqResult = new StringBuilder();

		definitionJson.getFaqs().forEach(faq -> {

			String question = faq.getQuestion();

			String answer = faq.getAnswer();

			if ((question == null || question.trim().isEmpty()) && (answer == null || answer.trim().isEmpty())) {

				return;
			}

			if (faqResult.length() > 0) {
				faqResult.append("<br><br>");
			}

			faqResult.append("Frequently Asked Question — \"").append(safe(question)).append("\" — answered as \"")
					.append(safe(answer)).append("\"");
		});

		if (faqResult.length() == 0) {
			return "No Frequently Asked Questions have been configured for this service.";
		}

		return faqResult.toString();
	}

	private String uploadLaunchDocument(String pdfBase64, UserSessionDTO userSessionDetails, Integer serviceId) {

		String userDetails = entityToString(userSessionDetails);
		CreateUploadSessionsRequest uploadRequest = new CreateUploadSessionsRequest();

		uploadRequest.setUserId(userSessionDetails.getUserID());

		uploadRequest.setSourceService("metadata");

		CreateUploadSessionsRequest.FileUploadRequest file = new CreateUploadSessionsRequest.FileUploadRequest();

		file.setReferenceId(String.valueOf(serviceId));

		file.setCategory("SERVICELAUNCH");

		file.setFileName("Service_Launch_" + serviceId + ".pdf");

		file.setAllowedMime(List.of("application/pdf"));

		file.setChunkedUpload(true);

		file.setExpiresInMinutes(60);
		
		file.setMaxFileSize(50000000l);
		
		file.setMinFileSize(1000l);

		file.setFunctionality("servicelaunch");

		uploadRequest.setFiles(List.of(file));

		ResponseEntity<CreateUploadSessionsResponse> uploadSessionResponse = fileMgmtFeignClient
				.generateCreateUploadSessionsResponse(userDetails, uploadRequest);

		if (uploadSessionResponse == null || uploadSessionResponse.getBody() == null) {

			throw new SPRuntimeError("Unable to create file upload session", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		CreateUploadSessionsResponse uploadSession = uploadSessionResponse.getBody();

	    String uploadId =uploadSession.getUploads().get(0).getUploadId();
	    String uploadToken =uploadSession.getUploads().get(0).getUploadToken();

		if (uploadId == null || uploadId.isBlank()) {
			throw new SPRuntimeError("File Management did not return upload ID", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		Base64UploadRequest base64Request = new Base64UploadRequest();

		base64Request.setBase64(pdfBase64);

		ResponseEntity<UploadStatusResponse> uploadResponse = fileMgmtFeignClient.uploadBase64File(userDetails,uploadId,
				"Bearer "+uploadToken,base64Request);

		if (uploadResponse == null || uploadResponse.getBody() == null) {

			throw new SPRuntimeError("Unable to upload launch document", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		UploadStatusResponse result = uploadResponse.getBody();

		return uploadId;
	}

	@Transactional
	public ResponseEntity<?> moveToProduction(MoveToProductionRequestDTO request, UserSessionDTO userSessionDetails) {

		if (request == null || request.getServiceId() == null) {
			throw new SPRuntimeError("Service ID is required", HttpStatus.BAD_REQUEST);
		}
		if (request.getSignedBase64() == null || request.getSignedBase64().isBlank()) {
			throw new SPRuntimeError("Signed document is required", HttpStatus.BAD_REQUEST);
		}
		if (request.getKey() == null || request.getKey().isBlank()) {
			throw new SPRuntimeError("dsc key is required", HttpStatus.BAD_REQUEST);
		}
		if (userSessionDetails == null || userSessionDetails.getUserID() == null) {
			throw new SPRuntimeError("Invalid user session", HttpStatus.UNAUTHORIZED);
		}
		Integer serviceId = request.getServiceId();
		ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);
		if (serviceLog == null) {
			throw new SPRuntimeError("Service log not found", HttpStatus.NOT_FOUND);
		}
		
		DSCSignRequest dscSignRequest = new DSCSignRequest();
		dscSignRequest.setFileId(serviceLog.getLaunchDocFileId());
		dscSignRequest.setUserId(userSessionDetails.getUserID());
		dscSignRequest.setSignedBase64(request.getSignedBase64());
		dscSignRequest.setKey(request.getKey());

		ResponseEntity<DSCSignResponse> signResponse = fileMgmtFeignClient.signDocument(entityToString(userSessionDetails), dscSignRequest);

		if (signResponse == null || signResponse.getBody() == null) {
			throw new SPRuntimeError("Document signing failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		DSCSignResponse dscResponse = signResponse.getBody();

		if (!dscResponse.isSuccess()) {
			throw new SPRuntimeError(
					dscResponse.getMessage() != null ? dscResponse.getMessage() : "Document signing failed",
					HttpStatus.BAD_REQUEST);
		}

		serviceLog.setLaunchDocFileId(dscResponse.getFileId());
		serviceLog.setLaunchApprovedBy(userSessionDetails.getUserID());
		serviceLog.setuDate(new Date());
		serviceLog.setServiceStatus(ServiceStatus.LAUNCHED.getCode());

        ServiceVersionChange serviceVersionChange = versionChangeRepository.findByTargetServiceIdAndStatusAndTenantId(
                                                            request.getServiceId(), ModificationStatus.IN_PROGRESS, userSessionDetails.getTenantId()
                                                    );

        if(serviceVersionChange != null){
            serviceVersionChange.setStatus(ModificationStatus.LAUNCHED);
            versionChangeRepository.save(serviceVersionChange);
            serviceMetadataService.freezeService(serviceId,userSessionDetails);
        }

        serviceLogRepository.save(serviceLog);

		return ResponseEntity.ok(new ServiceLaunchResponseDTO(serviceId, ServiceStatus.LAUNCHED.getCode(), "The service has been successfully launched."));
	}


	private boolean isAuthorizedDepartmentAdmin(ServiceDefinition serviceDefinition,
			UserSessionDTO userSessionDetails) {

		if (userSessionDetails == null || userSessionDetails.getUserID() == null) {
			return false;
		}
		List<UserSessionDTO.Roles> roles = userSessionDetails.getRoles();

		if (roles == null || roles.isEmpty()) {
			return false;
		}
		
		boolean isDepartmentAdmin = roles.stream().anyMatch(role -> role != null && role.getRoleId() == 18);

		if (!isDepartmentAdmin) {
			return false;
		}
		
		Long userEntityId =userSessionDetails.getEntityId().longValue();

	    Long serviceDefinerDepartmentId =serviceDefinition.getDepartmentId();

	    if (userEntityId == null || !userEntityId.equals(serviceDefinerDepartmentId)) {
	        return false;
	    }
		
		return true;
	}

	private String safe(String value) {

		return value == null ? "" : value;
	}
}