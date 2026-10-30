package com.serviceplus.tracking.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.serviceplus.tracking.dto.ServerSidePaginationRecord;
import com.serviceplus.tracking.dto.ServiceDetailResponse;
import com.serviceplus.tracking.dto.TaskDetailResponse;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.dto.WorkflowInboxResponse;
import com.serviceplus.tracking.entity.TaskMaster;
import com.serviceplus.tracking.entity.UserInbox;
import com.serviceplus.tracking.feignClient.UserManagmentFeignClient;
import com.serviceplus.tracking.repository.UserInboxRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;


@Service
public class InboxUserService {
	
    private static final Logger logger = LoggerFactory.getLogger("inboxLogger");
    @PersistenceContext
    private EntityManager entityManager;
	private final UserInboxRepository userInboxRepository;
    private final UserManagmentFeignClient userMgmtFeignClient;
    private final WorkflowInboxRedisService workflowInboxRedisService;

    public InboxUserService(UserInboxRepository userInboxRepository,
                            UserManagmentFeignClient userMgmtFeignClient,
                            WorkflowInboxRedisService workflowInboxRedisService) {
        this.userInboxRepository = userInboxRepository;
        this.userMgmtFeignClient = userMgmtFeignClient;
        this.workflowInboxRedisService = workflowInboxRedisService;
    }
	
	public List<ServiceDetailResponse> getPendingServices(UserSessionDTO session) {
		logger.info("Fetching pending services. UserId: {}, TenantId: {}, LocationId: {}",
		        session.getUserID(), session.getTenantId(), session.getLocationId());
		Set<String> holderIds = workflowInboxRedisService
				.getTokensByUserAndLocation(session.getUserID(), String.valueOf(session.getLocationId())).stream()
				.map(String::valueOf).collect(Collectors.toSet());

		if (holderIds == null || holderIds.isEmpty()) {
			logger.warn("No holder tokens found for UserId={}, LocationId={}",
			        session.getUserID(), session.getLocationId());
			return Collections.emptyList();
		}
		logger.info("Holder tokens fetched: {}", holderIds.size());
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();

		Root<UserInbox> root = cq.from(UserInbox.class);
        cq.distinct(true);

		Subquery<Integer> maxServiceIdSubquery = cq.subquery(Integer.class);
		Root<UserInbox> maxRoot = maxServiceIdSubquery.from(UserInbox.class);

		maxServiceIdSubquery.select(cb.max(maxRoot.get("serviceId")));
		maxServiceIdSubquery.where(cb.and(cb.equal(maxRoot.get("baseServiceId"), root.get("baseServiceId")),
				cb.equal(maxRoot.get("tenantId"), session.getTenantId()), maxRoot.get("userToken").in(holderIds)));

		Subquery<Long> countSubquery = cq.subquery(Long.class);
		Root<UserInbox> countRoot = countSubquery.from(UserInbox.class);

		countSubquery.select(cb.count(countRoot));
		countSubquery.where(cb.and(cb.equal(countRoot.get("baseServiceId"), root.get("baseServiceId")),
				cb.equal(countRoot.get("tenantId"), session.getTenantId()), countRoot.get("userToken").in(holderIds)));

		cq.multiselect(root.get("serviceId").alias("serviceId"), root.get("serviceName").alias("serviceName"),
				root.get("baseServiceId").alias("baseServiceId"), countSubquery.alias("pendingCount"));

		cq.where(cb.and(cb.equal(root.get("tenantId"), session.getTenantId()), root.get("userToken").in(holderIds),
				cb.equal(root.get("serviceId"), maxServiceIdSubquery)));

		cq.orderBy(cb.asc(root.get("serviceName")));

		List<Tuple> result = entityManager.createQuery(cq).getResultList();
		logger.info("Found {} pending services.", result.size());
		return result.stream().map(tuple -> {
			ServiceDetailResponse response = new ServiceDetailResponse();

			response.setServiceId(tuple.get("serviceId", Integer.class));
			response.setServiceName(tuple.get("serviceName", String.class));
			response.setPendingCount(tuple.get("pendingCount", Long.class));

			return response;
		}).collect(Collectors.toList());
	}
	
	public List<TaskDetailResponse> getPendingTasks(Integer baseServiceId, UserSessionDTO session) {
		logger.info("Fetching pending tasks. BaseServiceId: {}, UserId: {}, TenantId: {}, LocationId: {}",
				baseServiceId, session.getUserID(), session.getTenantId(), session.getLocationId());

		Set<String> holderIds = workflowInboxRedisService
				.getTokensByUserAndLocation(session.getUserID(), String.valueOf(session.getLocationId())).stream()
				.map(String::valueOf).collect(Collectors.toSet());

		if (holderIds.isEmpty()) {
			logger.warn("No holder tokens found.");
			return Collections.emptyList();
		}
		logger.info("Holder tokens fetched: {}", holderIds.size());
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		cq.distinct(true);

		Root<UserInbox> root = cq.from(UserInbox.class);

		Root<TaskMaster> taskMaster = cq.from(TaskMaster.class);

		cq.multiselect(root.get("taskId").alias("taskId"), root.get("taskName").alias("taskName"),
				cb.count(root).alias("pendingCount"), taskMaster.get("taskProperty").alias("taskProperty"));

		cq.where(cb.and(cb.equal(root.get("tenantId"), session.getTenantId()),
				cb.equal(root.get("baseServiceId"), baseServiceId), root.get("userToken").in(holderIds),

				cb.equal(root.get("taskId"), taskMaster.get("taskId")),
				cb.equal(root.get("tenantId"), taskMaster.get("tenantId")),
				cb.equal(root.get("baseServiceId"), taskMaster.get("baseServiceId"))));

		cq.groupBy(root.get("taskId"), root.get("taskName"), taskMaster.get("taskProperty"));

		cq.orderBy(cb.asc(root.get("taskName")));

		List<Tuple> result = entityManager.createQuery(cq).getResultList();

		logger.info("Found {} pending tasks.", result.size());
		return result.stream().map(tuple -> {
			TaskDetailResponse response = new TaskDetailResponse();

			response.setTaskId(tuple.get("taskId", String.class));
			response.setTaskName(tuple.get("taskName", String.class));
			response.setPendingCount(tuple.get("pendingCount", Long.class));

			response.setTaskProperty(tuple.get("taskProperty", JsonNode.class));
			return response;
		}).collect(Collectors.toList());
	}
	
	public ServerSidePaginationRecord<WorkflowInboxResponse> getApplications(Integer baseServiceId, String taskId, String applRefNo,
			Integer pageNo, Integer pageSize, UserSessionDTO userSessionDetails) {
		logger.info("Fetching applications. UserId: {}, BaseServiceId: {}, TaskId: {}, PageNo: {}, PageSize: {}, ApplRefNo: {}",
				userSessionDetails.getUserID(),baseServiceId,taskId,pageNo,pageSize,applRefNo);
		Long userId = userSessionDetails.getUserID();
		String locationId = String.valueOf(userSessionDetails.getLocationId());

		Set<Object> tokens = workflowInboxRedisService.getTokensByUserAndLocation(userId, locationId);

		if (tokens == null || tokens.isEmpty()) {
			ServerSidePaginationRecord<WorkflowInboxResponse> response =new ServerSidePaginationRecord<>();
            response.setData(Collections.emptyList());
            response.setTotalRecords(0);
            response.setCurrentPage(pageNo);
            response.setPageSize(pageSize);
            response.setTotalPages(0);
		}
		logger.info("Fetched {} holder tokens.",tokens == null ? 0 : tokens.size());
		List<String> tokenKeys = tokens.stream().map(String::valueOf).collect(Collectors.toList());

		Pageable pageable = PageRequest.of(pageNo == null ? 0 : pageNo, pageSize == null ? 20 : pageSize,Sort.by(Sort.Direction.DESC, "applRecievedOn"));

		Page<UserInbox> inboxPage;

		if (StringUtils.hasText(applRefNo)) {
			inboxPage = userInboxRepository.findByUserTokenInAndBaseServiceIdAndTaskIdAndApplRefNoContainingIgnoreCase(
					tokenKeys, baseServiceId, taskId, applRefNo.trim(), pageable);
		} else {
			inboxPage = userInboxRepository.findByUserTokenInAndBaseServiceIdAndTaskId(tokenKeys, baseServiceId, taskId,
					pageable);
		}
		logger.info("Fetched {} records. TotalRecords: {}, TotalPages: {}",inboxPage.getNumberOfElements(),
			    inboxPage.getTotalElements(),inboxPage.getTotalPages());
		ServerSidePaginationRecord<WorkflowInboxResponse> response = new ServerSidePaginationRecord<WorkflowInboxResponse>();

        response.setData(inboxPage.getContent().stream().map(this::buildInboxResponse)
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

        return response;
    }
	

}
