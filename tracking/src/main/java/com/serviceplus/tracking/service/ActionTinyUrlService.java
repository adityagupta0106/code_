package com.serviceplus.tracking.service;

import static com.serviceplus.tracking.utility.CommonUtil.getUserSessionDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.ActionTinyUrlRequestDTO;
import com.serviceplus.tracking.dto.ActionTinyUrlResponseDTO;
import com.serviceplus.tracking.dto.HandlerResponse;
import com.serviceplus.tracking.dto.ServiceMeta;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.entity.ApplicantInbox;
import com.serviceplus.tracking.entity.ApplicationActionTinyUrl;
import com.serviceplus.tracking.entity.ApplicationTracking;
import com.serviceplus.tracking.entity.UserInbox;
import com.serviceplus.tracking.feignClient.ProcessingFeignClient;
import com.serviceplus.tracking.repository.ApplicantInboxRepository;
import com.serviceplus.tracking.repository.ApplicationActionTinyUrlRepository;
import com.serviceplus.tracking.repository.ApplicationTrackingRepository;
import com.serviceplus.tracking.repository.UserInboxRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ActionTinyUrlService {

	private static final Logger logger = LoggerFactory.getLogger(ActionTinyUrlService.class);

	private final ApplicationActionTinyUrlRepository applicationActionTinyUrlRepository;

	private final UserInboxRepository userInboxRepository;

	private final ApplicantInboxRepository applicantInboxRepository;

	private final ApplicationTrackingRepository applicationTrackingRepository;

	private final WorkflowInboxRedisService workflowInboxRedisService;

	private final ProcessingFeignClient processingFeignClient;

	public ActionTinyUrlService(ApplicationActionTinyUrlRepository applicationActionTinyUrlRepository,
			UserInboxRepository userInboxRepository, ApplicantInboxRepository applicantInboxRepository,
			ApplicationTrackingRepository applicationTrackingRepository,
			WorkflowInboxRedisService workflowInboxRedisService, ProcessingFeignClient processingFeignClient) {

		this.applicationActionTinyUrlRepository = applicationActionTinyUrlRepository;

		this.userInboxRepository = userInboxRepository;

		this.applicantInboxRepository = applicantInboxRepository;

		this.applicationTrackingRepository = applicationTrackingRepository;

		this.workflowInboxRedisService = workflowInboxRedisService;

		this.processingFeignClient = processingFeignClient;
	}

	public ResponseEntity<?> resolveActionTinyUrl(ActionTinyUrlRequestDTO request, Map<String, Object> headers,
			HttpServletRequest httpRequest) {

		validateRequest(request);

		String urlKey = request.getUrlKey().trim();

		UserSessionDTO userSessionDetails = getUserSessionDetails(httpRequest);

		ApplicationActionTinyUrl actionTinyUrl = applicationActionTinyUrlRepository.findByUrlKey(urlKey)
				.orElseThrow(() -> new SPRuntimeError("Invalid action tiny URL", HttpStatus.NOT_FOUND));

		/*
		 * ========================================================= CHECK WHETHER
		 * APPLICATION IS PENDING WITH APPLICANT
		 * =========================================================
		 */
		Optional<ApplicantInbox> applicantInbox = applicantInboxRepository
				.findByApplicationIdAndTenantId(actionTinyUrl.getApplicationId(), actionTinyUrl.getTenantId());

		if (applicantInbox.isPresent()) {

			logger.info("Application {} is pending with applicant", actionTinyUrl.getApplicationId());

			Optional<ApplicationTracking> tracking = applicationTrackingRepository
					.findFirstByApplIdAndTaskIdAndTenantIdOrderByCreatedOnDesc(actionTinyUrl.getApplicationId(), "s_1",
							actionTinyUrl.getTenantId());

			if (tracking.isEmpty()) {

				logger.error("ApplicationTracking not found for applicationId={}", actionTinyUrl.getApplicationId());

				throw new SPRuntimeError("Application tracking details not found", HttpStatus.NOT_FOUND);
			}

			ApplicationTracking applicationTracking = tracking.get();

			Long appliedUserId = applicationTracking.getAppliedUserId();

			/*
			 * ===================================================== APPLICANT IS NOT LOGGED
			 * IN =====================================================
			 *
			 * If appliedUserId is null/0, login is not required.
			 *
			 * If appliedUserId has a valid user ID, login is mandatory.
			 */
			if (userSessionDetails == null) {

				if (appliedUserId != null && appliedUserId.longValue() != 0L) {

					logger.info("Login mandatory for applicationId={}, appliedUserId={}",
							actionTinyUrl.getApplicationId(), appliedUserId);

					throw new SPRuntimeError("LOGIN_MANDATORY", HttpStatus.UNAUTHORIZED);
				}

				logger.info("Application {} can be rendered without login", actionTinyUrl.getApplicationId());

				return renderApplicantApplication(actionTinyUrl, applicationTracking, headers, null,applicantInbox);
			}

			/*
			 * ===================================================== APPLICANT IS LOGGED IN
			 * =====================================================
			 *
			 * For a logged-in user:
			 *
			 * 1. appliedUserId must exist 2. logged-in user must be the same applicant
			 */
			if (appliedUserId == null || appliedUserId.longValue() == 0L) {

				logger.info("ACCESS DENIED for applicationId={} because " + "appliedUserId is 0",
						actionTinyUrl.getApplicationId());

				throw new SPRuntimeError("ACCESS DENIED", HttpStatus.FORBIDDEN);
			}

			Long loggedInUserId = userSessionDetails.getUserID();

			if (loggedInUserId == null || !appliedUserId.equals(loggedInUserId)) {

				logger.info(
						"ACCESS DENIED for applicationId={}. " + "Application applied by userId={}, "
								+ "logged-in userId={}",
						actionTinyUrl.getApplicationId(), appliedUserId, loggedInUserId);

				throw new SPRuntimeError("ACCESS DENIED", HttpStatus.FORBIDDEN);
			}

			logger.info("Application {} belongs to logged-in applicant userId={}", actionTinyUrl.getApplicationId(),
					loggedInUserId);

			return renderApplicantApplication(actionTinyUrl, applicationTracking, headers, userSessionDetails,applicantInbox);
		}

		/*
		 * ==== USER MUST BE LOGGED IN FOR OFFICIAL INBOX =====================================
		 */
		if (userSessionDetails == null || userSessionDetails.getUserID()==0l) {

			String actionUrl = actionTinyUrl.getTinyUrl();

			ActionTinyUrlResponseDTO response = new ActionTinyUrlResponseDTO();

			response.setSuccess(true);
			response.setAuthenticated(false);
			response.setRedirectUrl(actionUrl);

			return ResponseEntity.ok(response);
		}

		String tenantId = userSessionDetails.getTenantId();

		Long userId = userSessionDetails.getUserID();

		String locationId = String.valueOf(userSessionDetails.getLocationId());

		/*
		 * ========================================================= CHECK OFFICIAL /
		 * USER INBOX =========================================================
		 */
		Set<Object> tokens = workflowInboxRedisService.getTokensByUserAndLocation(userId, locationId);

		if (tokens != null && !tokens.isEmpty()) {

			logger.info("Fetched {} holder tokens for userId={} locationId={}", tokens.size(), userId, locationId);

			List<String> tokenKeys = tokens.stream().map(String::valueOf).collect(Collectors.toList());

			Optional<UserInbox> inboxRecord = getApplicationInboxForUser(actionTinyUrl.getApplicationId(), tokenKeys);

			if (inboxRecord.isPresent()) {

				UserInbox userInbox = inboxRecord.get();

				logger.info("Application {} is pending with userId={}", actionTinyUrl.getApplicationId(), userId);

				return renderApplication(actionTinyUrl, headers, userInbox);
			}
		}

		logger.info("ACCESS DENIED for applicationId={} userId={}", actionTinyUrl.getApplicationId(), userId);

		throw new SPRuntimeError("ACCESS DENIED", HttpStatus.FORBIDDEN);
	}

	private Optional<UserInbox> getApplicationInboxForUser(String applicationId, List<String> tokenKeys) {

		if (tokenKeys == null || tokenKeys.isEmpty()) {
			return Optional.empty();
		}

		List<UserInbox> inboxRecords = userInboxRepository.findByUserTokenInAndApplId(tokenKeys, applicationId);

		if (inboxRecords == null || inboxRecords.isEmpty()) {
			return Optional.empty();
		}

		return inboxRecords.stream().filter(inbox -> StringUtils.equalsIgnoreCase(applicationId, inbox.getApplId()))
				.findFirst();
	}

	private ResponseEntity<?> renderApplication(ApplicationActionTinyUrl actionTinyUrl, Map<String, Object> headers,
			UserInbox userInbox) {

		Integer serviceId = actionTinyUrl.getServiceId();

		Integer baseServiceId = actionTinyUrl.getServiceId() / 10000;

		return render(headers, serviceId, baseServiceId, userInbox.getApplId(), userInbox.getTaskId());
	}

	private ResponseEntity<?> renderApplicantApplication(ApplicationActionTinyUrl actionTinyUrl,
			ApplicationTracking applicationTracking, Map<String, Object> headers, UserSessionDTO userSessionDetails, Optional<ApplicantInbox> applicantInbox) {

		Integer serviceId = applicationTracking.getServiceId();

		Integer baseServiceId = applicationTracking.getBaseServiceId();

		String applicationId = applicationTracking.getApplId();

		String taskId = applicantInbox.get().getTaskId();

		logger.info("Rendering applicant applicationId={}, " + "serviceId={}, baseServiceId={}, taskId={}",
				applicationId, serviceId, baseServiceId, taskId);

		return render(headers, serviceId, baseServiceId, applicationId, taskId);
	}

	private ResponseEntity<?> render(Map<String, Object> headers, Integer serviceId, Integer baseServiceId,
			String applicationId, String taskId) {

		ServiceMeta serviceKeyDTO;

		String serviceKey;

		ResponseEntity<ServiceMeta> serviceKeyResponse = processingFeignClient.fetchServiceKey(headers, baseServiceId,
				applicationId, taskId, serviceId.toString());

		logger.info("Fetch service key response received " + "for serviceId:{}, status:{}", serviceId,
				serviceKeyResponse.getStatusCode());

		if (!serviceKeyResponse.getStatusCode().equals(HttpStatus.OK) || serviceKeyResponse.getBody() == null) {

			logger.error("Unable to fetch service key " + "for serviceId:{}", serviceId);

			throw new SPRuntimeError("Unable to fetch service key", HttpStatus.BAD_GATEWAY);
		}

		serviceKeyDTO = serviceKeyResponse.getBody();

		serviceKey = serviceKeyDTO.getServiceKey();

		logger.info("Service key fetched successfully " + "for serviceId:{}, serviceKey:{}", serviceId, serviceKey);

	
		ResponseEntity<HandlerResponse> renderResponse = processingFeignClient.render(headers, serviceKey,
				serviceId.toString());

		logger.info("Render response received " + "for serviceId:{}, status:{}", serviceId,
				renderResponse.getStatusCode());

		if (!renderResponse.getStatusCode().equals(HttpStatus.OK) || renderResponse.getBody() == null) {

			logger.error("Unable to render application " + "for serviceId:{}", serviceId);

			throw new SPRuntimeError("Unable to render application", HttpStatus.BAD_GATEWAY);
		}

		HandlerResponse handlerResponse = renderResponse.getBody();

		logger.info("Render successful " + "for serviceId:{}, txnId:{}", serviceId, handlerResponse.getTxnId());
		handlerResponse.setApplicationId(applicationId);
		handlerResponse.getData().put("serviceId", serviceId);
		handlerResponse.getData().put("serviceKey", serviceKey);
		return ResponseEntity.ok(handlerResponse);
	}

	private void validateRequest(ActionTinyUrlRequestDTO request) {

		if (request == null) {
			throw new SPRuntimeError("Request is required", HttpStatus.BAD_REQUEST);
		}
		if (request.getUrlKey() == null || request.getUrlKey().trim().isEmpty()) {
			throw new SPRuntimeError("URL key is required", HttpStatus.BAD_REQUEST);
		}
	}
}
