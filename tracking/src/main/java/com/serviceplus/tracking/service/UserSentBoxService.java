package com.serviceplus.tracking.service;

import static com.serviceplus.tracking.utility.CommonUtil.getUserSessionDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.dto.InboxFilterResolution;
import com.serviceplus.tracking.dto.MatchedOutputAttr;
import com.serviceplus.tracking.dto.SentBoxSearchRequest;
import com.serviceplus.tracking.dto.ServerSidePaginationRecord;
import com.serviceplus.tracking.dto.UserSentBoxDTO;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.entity.UserSentBox;
import com.serviceplus.tracking.repository.UserSentBoxRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserSentBoxService {

	private static final Logger logger = LoggerFactory.getLogger("userSentBoxService");

	private static final int MAX_PER_PAGE_COUNT = 50;

	private final UserSentBoxRepository repository;

	private final WorkflowInboxRedisService workflowInboxRedisService;
	private final DynamicInboxFilterService dynamicInboxFilterService;

	@Autowired
	public UserSentBoxService(UserSentBoxRepository repository, WorkflowInboxRedisService workflowInboxRedisService,
			DynamicInboxFilterService dynamicInboxFilterService) {
		this.repository = repository;
		this.workflowInboxRedisService = workflowInboxRedisService;
		this.dynamicInboxFilterService = dynamicInboxFilterService;
	}

	public ServerSidePaginationRecord<UserSentBoxDTO> getFilterApplications(SentBoxSearchRequest searchDTO,
			HttpServletRequest request) {

		UserSessionDTO sessionUser = getUserSessionDetails(request);

		int page = Math.max(searchDTO.getPage(), 0);
		int size = Math.min(searchDTO.getSize(), MAX_PER_PAGE_COUNT);
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "actionOn"));

		Set<String> holderIds = workflowInboxRedisService
				.getTokensByUserAndLocation(sessionUser.getUserID(), sessionUser.getLocationId().toString()).stream()
				.map(String::valueOf).collect(Collectors.toSet());

		boolean hasDynamicFilters = searchDTO.getFilters() != null && !searchDTO.getFilters().isEmpty();

		Map<String, List<MatchedOutputAttr>> outputAttrsByApplicationId = Collections.emptyMap();
		List<String> filteredApplicationIds = null;

		if (hasDynamicFilters) {
			logger.info("Dynamic filters supplied for sentbox | serviceId={}, taskId={}, filters={}",
					searchDTO.getServiceId(), searchDTO.getTaskId(), searchDTO.getFilters());

			InboxFilterResolution resolution = dynamicInboxFilterService.resolveInboxFilterResult(
					searchDTO.getServiceId(), searchDTO.getTaskId(), searchDTO.getFilters(), List.of('S', 'B'));

			filteredApplicationIds = resolution.getApplicationIds();
			outputAttrsByApplicationId = resolution.getOutputAttrsByApplicationId();

			if (filteredApplicationIds != null && filteredApplicationIds.isEmpty()) {
				logger.info("Dynamic filters matched no sentbox applications | serviceId={}, taskId={}",
						searchDTO.getServiceId(), searchDTO.getTaskId());
				return emptySentBoxResponse(page, size);
			}
		}

		Specification<UserSentBox> specification = buildSpecification(searchDTO, holderIds, sessionUser.getTenantId(),
				filteredApplicationIds);

		Page<UserSentBox> sentBoxPage = repository.findAll(specification, pageable);

		Map<String, List<MatchedOutputAttr>> finalOutputAttrs = outputAttrsByApplicationId;

		ServerSidePaginationRecord<UserSentBoxDTO> response = new ServerSidePaginationRecord<>();
		response.setData(sentBoxPage.getContent().stream().map(entity -> {
			UserSentBoxDTO dto = convertToDTO(entity);
			List<MatchedOutputAttr> matchedAttrs = finalOutputAttrs.get(dto.getApplId());
			if (matchedAttrs != null) {
				dto.setMatchedOutputAttrs(matchedAttrs);
			}
			return dto;
		}).toList());

		response.setTotalRecords(sentBoxPage.getTotalElements());
		response.setCurrentPage(sentBoxPage.getNumber());
		response.setPageSize(sentBoxPage.getSize());
		response.setTotalPages(sentBoxPage.getTotalPages());

		return response;
	}

	private Specification<UserSentBox> buildSpecification(SentBoxSearchRequest searchDTO, Set<String> holderIds,
			String tenantId, List<String> filteredApplicationIds) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(
					cb.or(
							cb.isFalse(root.get("calledback")),
							cb.isNull(root.get("calledback"))
					)
			);

			if (filteredApplicationIds != null) {
				predicates.add(root.get("applId").in(filteredApplicationIds));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private ServerSidePaginationRecord<UserSentBoxDTO> emptySentBoxResponse(int page, int size) {
		ServerSidePaginationRecord<UserSentBoxDTO> response = new ServerSidePaginationRecord<>();
		response.setData(Collections.emptyList());
		response.setTotalRecords(0);
		response.setCurrentPage(page);
		response.setPageSize(size);
		response.setTotalPages(0);
		return response;
	}

	public ServerSidePaginationRecord<?> getSentBox(SentBoxSearchRequest searchDTO, HttpServletRequest request) {

		UserSessionDTO sessionUser = getUserSessionDetails(request);

		int page = Math.max(searchDTO.getPage(), 0);

		int size = Math.min(searchDTO.getSize(), MAX_PER_PAGE_COUNT);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "actionOn"));

		Set<String> holderIds = workflowInboxRedisService
				.getTokensByUserAndLocation(sessionUser.getUserID(), sessionUser.getLocationId().toString()).stream()
				.map(String::valueOf).collect(Collectors.toSet());

		Specification<UserSentBox> specification = buildSpecification(searchDTO, holderIds, sessionUser.getTenantId());

		Page<UserSentBox> sentBoxPage = repository.findAll(specification, pageable);

		ServerSidePaginationRecord<UserSentBoxDTO> response = new ServerSidePaginationRecord<>();

		response.setData(sentBoxPage.getContent().stream().map(this::convertToDTO).toList());

		response.setTotalRecords(sentBoxPage.getTotalElements());
		response.setCurrentPage(sentBoxPage.getNumber());
		response.setPageSize(sentBoxPage.getSize());
		response.setTotalPages(sentBoxPage.getTotalPages());

		return response;
	}

	private Specification<UserSentBox> buildSpecification(SentBoxSearchRequest searchDTO, Set<String> holderIds,
			String tenantId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(
					cb.or(
							cb.isFalse(root.get("calledback")),
							cb.isNull(root.get("calledback"))
					)
			);

			predicates.add(cb.equal(root.get("tenantId"), tenantId));
			predicates.add(root.get("holderId").in(holderIds));

			if (searchDTO.getBaseServiceId() != null) {
				predicates.add(cb.equal(root.get("baseServiceId"), searchDTO.getBaseServiceId()));
			}

			if (searchDTO.getTaskId() != null && !searchDTO.getTaskId().isBlank()) {
				predicates.add(cb.equal(root.get("taskId"), searchDTO.getTaskId()));
			}

			if (searchDTO.getActionCode() != null) {
				predicates.add(cb.equal(root.get("actionCode"), searchDTO.getActionCode()));
			}

			if (searchDTO.getApplRefNo() != null && !searchDTO.getApplRefNo().isBlank()) {
				predicates.add(
						cb.like(cb.upper(root.get("applRefNo")), "%" + searchDTO.getApplRefNo().toUpperCase() + "%"));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private UserSentBoxDTO convertToDTO(UserSentBox entity) {

		UserSentBoxDTO dto = new UserSentBoxDTO();

		dto.setApplId(entity.getApplId());
		dto.setApplRefNo(entity.getApplRefNo());
		dto.setServiceId(entity.getServiceId());
		dto.setServiceName(entity.getServiceName());
		dto.setTaskId(entity.getTaskId());
		dto.setTaskName(entity.getTaskName());
		dto.setActionOn(entity.getActionOn());
		dto.setCurrentProcessId(entity.getProcessId());

		return dto;
	}
}
