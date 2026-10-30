package com.serviceplus.tracking.repository;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.*;
import com.serviceplus.tracking.entity.ApplicationTracking;
import com.serviceplus.tracking.entity.ApplicationTrackingDocument;
import com.serviceplus.tracking.entity.ApplicationTrackingHolder;
import com.serviceplus.tracking.feignClient.FileManagementFeignClient;
import com.serviceplus.tracking.feignClient.InstanceConfigRegistryFeignClient;
import com.serviceplus.tracking.service.WorkflowInboxRedisService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.serviceplus.tracking.utility.CommonUtil.entityToString;

@Repository
public class ApplicationTrackingRepositoryCustomImpl {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTrackingRepositoryCustomImpl.class);

    private static final int MAX_PAGE_SIZE = 50;

    @PersistenceContext
    private EntityManager entityManager;

    private final ApplicationTrackingDocumentRepository applicationTrackingDocumentRepository;

    private final WorkflowInboxRedisService workflowInboxRedisService;

    private final ApplicationTrackingHolderRepository applicationTrackingHolderRepository;

    private final FileManagementFeignClient fileManagementFeignClient;

    private final ApplicationTrackingRepository applicationTrackingRepository;

    public ApplicationTrackingRepositoryCustomImpl(ApplicationTrackingDocumentRepository applicationTrackingDocumentRepository, WorkflowInboxRedisService workflowInboxRedisService, ApplicationTrackingHolderRepository applicationTrackingHolderRepository, FileManagementFeignClient fileManagementFeignClient, ApplicationTrackingRepository applicationTrackingRepository) {
        this.applicationTrackingDocumentRepository = applicationTrackingDocumentRepository;
        this.workflowInboxRedisService = workflowInboxRedisService;
        this.applicationTrackingHolderRepository = applicationTrackingHolderRepository;
        this.fileManagementFeignClient = fileManagementFeignClient;
        this.applicationTrackingRepository = applicationTrackingRepository;
    }

    public ServerSidePaginationRecord<Applications> search(ApplicationSearchRequest request, UserSessionDTO user) {

        logger.info("Application search request serviceId={}, refNo={}, page={}, size={}, tenantId={}, userId={}",
                request.getServiceId(), request.getApplicationRefNo(), request.getPage(), request.getSize(), user.getTenantId(), user.getUserID());

        request.setSize(Math.min(request.getSize(), MAX_PAGE_SIZE));
        request.setPage(Math.max(request.getPage(), 0));

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<ApplicationTracking> query = cb.createQuery(ApplicationTracking.class);
        Root<ApplicationTracking> root = query.from(ApplicationTracking.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("appliedUserId"), user.getUserID()));
        predicates.add(cb.equal(root.get("tenantId"), user.getTenantId()));
        predicates.add(cb.equal(root.get("previousTaskId"), ""));

        if (request.getServiceId() != null) {
            predicates.add(cb.equal(root.get("serviceId"), request.getServiceId()));
        }

        if (request.getApplicationRefNo() != null && !request.getApplicationRefNo().isBlank()) {
            predicates.add(cb.like(cb.upper(root.get("applRefNo")), "%" + request.getApplicationRefNo().toUpperCase() + "%"));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("applyDate")));

        TypedQuery<ApplicationTracking> typedQuery = entityManager.createQuery(query);

        typedQuery.setFirstResult(request.getPage() * request.getSize());
        typedQuery.setMaxResults(request.getSize());

        List<ApplicationTracking> entities = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);

        Root<ApplicationTracking> countRoot = countQuery.from(ApplicationTracking.class);

        List<Predicate> countPredicates = new ArrayList<>();

        countPredicates.add(cb.equal(countRoot.get("appliedUserId"), user.getUserID()));
        countPredicates.add(cb.isNull(countRoot.get("previousTaskId")));
        countPredicates.add(cb.equal(countRoot.get("tenantId"), user.getTenantId()));

        if (request.getServiceId() != null) {
            countPredicates.add(cb.equal(countRoot.get("serviceId"), request.getServiceId()));
        }

        if (request.getApplicationRefNo() != null && !request.getApplicationRefNo().isBlank()) {
            countPredicates.add(cb.like(cb.upper(countRoot.get("applRefNo")), "%" + request.getApplicationRefNo().toUpperCase() + "%"));
        }

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();

        logger.info("Applications fetched records={} totalRecords={}", entities.size(), totalRecords);

        ServerSidePaginationRecord<Applications> response = new ServerSidePaginationRecord<>();

        response.setData(entities.stream().map(this::convert).toList());
        response.setCurrentPage(request.getPage());
        response.setPageSize(request.getSize());
        response.setTotalRecords(totalRecords);
        response.setTotalPages((int) Math.ceil((double) totalRecords / request.getSize()));

        return response;
    }

    private Applications convert(ApplicationTracking entity) {

        Applications dto = new Applications();

        dto.setApplicationId(entity.getApplId());
        dto.setServiceId(entity.getServiceId());
        dto.setApplicationRefNo(entity.getApplRefNo());
        dto.setServiceName(entity.getServiceName());
        dto.setApplyDate(entity.getApplyDate());
        dto.setActionCode(entity.getActionCode());

        return dto;
    }

    public DocumentDownloadResponse getDocument(Long documentId, UserSessionDTO user) {

        ApplicationTrackingDocument document = applicationTrackingDocumentRepository.findById(documentId).orElseThrow(() -> new SPRuntimeError("Document not found", HttpStatus.NOT_FOUND));


        ApplicationTracking tracking =applicationTrackingRepository
                                        .findByProcessIdAndTenantId(
                                                document.getTracking().getProcessId(), user.getTenantId())
                                        .orElseThrow(() ->
                                                new SPRuntimeError("Application tracking not found", HttpStatus.NOT_FOUND)
                                        );

        List<ApplicationTracking> applicationTrackingList = applicationTrackingRepository.findByApplIdAndTenantIdOrderByCreatedOnAsc(tracking.getApplId(), user.getTenantId());

        boolean applicantAccess = applicationTrackingList.stream().anyMatch(process -> process.getAppliedUserId() != null && process.getAppliedUserId().equals(user.getUserID()));

        if (applicantAccess) {
            applicantAccess = document.getViewPermission() != null && document.getViewPermission().contains("applicant");
        }

        boolean officialAccess = false;

        if (!applicantAccess) {

            Set<Object> userHolderIds = workflowInboxRedisService.getTokensByUserAndLocation(user.getUserID(), String.valueOf(user.getLocationId()));

            Set<Object> serviceHolderIds = workflowInboxRedisService.getHolderIdsByService(tracking.getServiceId());

            if (userHolderIds != null && !userHolderIds.isEmpty() && serviceHolderIds != null && !serviceHolderIds.isEmpty()) {

                Set<String> serviceHolders = serviceHolderIds.stream().map(String::valueOf).collect(Collectors.toSet());

                officialAccess = userHolderIds.stream().map(String::valueOf).anyMatch(serviceHolders::contains);
            }

            officialAccess = officialAccess && document.getViewPermission() != null && document.getViewPermission().contains("official");
        }

        if (!applicantAccess && !officialAccess) {
            throw new SPRuntimeError("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("USER-DETAILS", entityToString(user));

        SignedUrlResponse signedUrl = fileManagementFeignClient.download(document.getDocumentId(), headers);

        DocumentDownloadResponse response = new DocumentDownloadResponse();

        response.setDocumentId(documentId);
        response.setDocumentName(document.getDocumentName());
        response.setDocumentType(document.getDocumentType());
        response.setDownloadUrl(signedUrl.getUrl());
        response.setExpiresIn(signedUrl.getExpiresIn());

        return response;
    }
}
