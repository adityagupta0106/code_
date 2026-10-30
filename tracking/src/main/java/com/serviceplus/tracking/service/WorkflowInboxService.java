package com.serviceplus.tracking.service;

import static com.serviceplus.tracking.utility.CommonUtil.getUserSessionDetails;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.InboxApplReqDTO;
import com.serviceplus.tracking.dto.InboxFilterResolution;
import com.serviceplus.tracking.dto.MatchedOutputAttr;
import com.serviceplus.tracking.dto.ServerSidePaginationRecord;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.dto.WorkflowInboxResponse;
import com.serviceplus.tracking.entity.UserInbox;
import com.serviceplus.tracking.repository.UserInboxRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class WorkflowInboxService {

    private static final Logger logger = LoggerFactory.getLogger("workflowInboxService");

    private static final int MAX_PER_PAGE_COUNT = 50;

    private final WorkflowInboxRedisService workflowInboxRedisService;

    private final DynamicInboxFilterService dynamicInboxFilterService;
    
    private final UserInboxRepository userInboxRepository;

    @Autowired
    public WorkflowInboxService(WorkflowInboxRedisService workflowInboxRedisService, UserInboxRepository userInboxRepository, DynamicInboxFilterService dynamicInboxFilterService) {
        this.workflowInboxRedisService = workflowInboxRedisService;
        this.userInboxRepository = userInboxRepository;
        this.dynamicInboxFilterService = dynamicInboxFilterService;
    }

    public ServerSidePaginationRecord<?> getInboxApplications(HttpServletRequest request, int page, int size) {

        UserSessionDTO userSessionDetails = getUserSessionDetails(request);

        if (userSessionDetails == null) {
            throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
        }

        Long userId = userSessionDetails.getUserID();

        String locationId = String.valueOf(userSessionDetails.getLocationId());

        Set<Object> tokens = workflowInboxRedisService.getTokensByUserAndLocation(userId, locationId);

        if (tokens == null || tokens.isEmpty()) {

            ServerSidePaginationRecord<WorkflowInboxResponse> response =
                    new ServerSidePaginationRecord<>();

            response.setData(
                    Collections.emptyList());

            response.setTotalRecords(0);
            response.setCurrentPage(page);
            response.setPageSize(size);
            response.setTotalPages(0);

            return response;
        }

        List<String> tokenKeys = tokens.stream().map(Object::toString).toList();

        Pageable pageable =
                PageRequest.of(
                        page,
                        Math.min(size, MAX_PER_PAGE_COUNT),
                        Sort.by(
                                Sort.Direction.DESC,
                                "applRecievedOn"));

        Page<UserInbox> inboxPage = userInboxRepository.findByUserTokenInOrderByIsPriorityDescLastActionOnDesc(tokenKeys, pageable);

        ServerSidePaginationRecord<WorkflowInboxResponse> response = new ServerSidePaginationRecord<WorkflowInboxResponse>();

        response.setData(
                inboxPage.getContent()
                        .stream()
                        .map(this::buildInboxResponse)
                        .toList());

        response.setTotalRecords(inboxPage.getTotalElements());
        response.setCurrentPage(inboxPage.getNumber());
        response.setPageSize(inboxPage.getSize());
        response.setTotalPages(inboxPage.getTotalPages());

        return response;
    }

    private WorkflowInboxResponse buildInboxResponse(UserInbox inbox) {

        WorkflowInboxResponse response = new WorkflowInboxResponse();

        response.setApplId(inbox.getApplId());
        response.setApplRefNo(inbox.getApplRefNo());
        response.setServiceId(inbox.getServiceId());
        response.setServiceName(inbox.getServiceName());
        response.setTaskId(inbox.getTaskId());
        response.setTaskName(inbox.getTaskName());
        response.setFormId(inbox.getFormId());
        response.setCurrentProcessId(inbox.getCurrentProcessId());
        response.setApplRecievedOn(inbox.getApplRecievedOn());
        response.setLocationId(inbox.getLocationId());
        response.setIsPriority(inbox.getIsPriority());
        
        return response;
    }

    public ServerSidePaginationRecord<WorkflowInboxResponse> getFilterApplications(
			InboxApplReqDTO inboxApplReqDTO, int page, int size, HttpServletRequest request) {

		UserSessionDTO userSessionDetails = getUserSessionDetails(request);

		if (userSessionDetails == null) {
			logger.warn("Invalid session while fetching filter applications");
			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
		}

		Long userId = userSessionDetails.getUserID();
		String locationId = String.valueOf(userSessionDetails.getLocationId());

		Set<Object> tokens = workflowInboxRedisService.getTokensByUserAndLocation(userId, locationId);

		if (tokens == null || tokens.isEmpty()) {
			logger.info("No tokens found for userId={}, locationId={} — returning empty result", userId, locationId);
			return emptyResponse(page, size);
		}

		List<String> tokenKeys = tokens.stream().map(Object::toString).toList();
		Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PER_PAGE_COUNT),
				Sort.by(Sort.Direction.DESC, "applRecievedOn"));

		Page<UserInbox> inboxPage;

		Map<String, List<MatchedOutputAttr>> outputAttrsByApplicationId = Collections.emptyMap();

		boolean hasDynamicFilters = inboxApplReqDTO != null && inboxApplReqDTO.getFilters() != null
				&& !inboxApplReqDTO.getFilters().isEmpty();

		if (!hasDynamicFilters) {
			logger.info("No dynamic filters supplied — using default token-based inbox query | userId={}", userId);

			inboxPage = userInboxRepository.findByUserTokenInOrderByIsPriorityDescLastActionOnDesc(tokenKeys, pageable);

		} else {
			logger.info("Dynamic filters supplied | serviceId={}, taskId={}, filters={}",
					inboxApplReqDTO.getServiceId(), inboxApplReqDTO.getTaskId(), inboxApplReqDTO.getFilters());

			InboxFilterResolution resolution = dynamicInboxFilterService.resolveInboxFilterResult(
					inboxApplReqDTO.getServiceId(), inboxApplReqDTO.getTaskId(), inboxApplReqDTO.getFilters(), List.of('I', 'B'));

			List<String> filteredApplicationIds = resolution.getApplicationIds();

			if (filteredApplicationIds != null && filteredApplicationIds.isEmpty()) {
				logger.info("Dynamic filters matched no applications | serviceId={}, taskId={}",
						inboxApplReqDTO.getServiceId(), inboxApplReqDTO.getTaskId());
				return emptyResponse(page, size);
			}

			inboxPage = (filteredApplicationIds == null)
					? userInboxRepository.findByUserTokenInOrderByIsPriorityDescLastActionOnDesc(tokenKeys, pageable)
					: userInboxRepository.findByUserTokenInAndApplIdInOrderByIsPriorityDescLastActionOnDesc(tokenKeys,
							filteredApplicationIds, pageable);

			outputAttrsByApplicationId = resolution.getOutputAttrsByApplicationId();
		}

		logger.info("Fetched inbox page | totalRecords={}, totalPages={}, currentPage={}", inboxPage.getTotalElements(),
				inboxPage.getTotalPages(), inboxPage.getNumber());

		Map<String, List<MatchedOutputAttr>> finalOutputAttrs = outputAttrsByApplicationId; // effectively-final for
																							// lambda capture

		ServerSidePaginationRecord<WorkflowInboxResponse> response = new ServerSidePaginationRecord<>();

		List<WorkflowInboxResponse> responseData = inboxPage.getContent().stream()
		        .map(inbox -> {
		            WorkflowInboxResponse dto = buildInboxResponse(inbox); // unchanged, original method
		            List<MatchedOutputAttr> matchedAttrs = finalOutputAttrs.get(inbox.getApplId());
		            if (matchedAttrs != null) {
		                dto.setMatchedOutputAttrs(matchedAttrs);
		            }
		            return dto;
		        })
		        .toList();

		response.setData(responseData);

		return response;
	}

    private ServerSidePaginationRecord<WorkflowInboxResponse> emptyResponse(int page, int size) {
        ServerSidePaginationRecord<WorkflowInboxResponse> response = new ServerSidePaginationRecord<>();
        response.setData(Collections.emptyList());
        response.setTotalRecords(0);
        response.setCurrentPage(page);
        response.setPageSize(size);
        response.setTotalPages(0);
        return response;
    }
}