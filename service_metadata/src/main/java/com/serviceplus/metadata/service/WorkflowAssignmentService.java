package com.serviceplus.metadata.service;

import static com.serviceplus.metadata.utility.ApplicationConstants.WORKFLOW_ASSIGNMENT_TOKEN_PREFIX;
import static com.serviceplus.metadata.utility.CommonUtil.generateUniqueToken;
import static com.serviceplus.metadata.utility.CommonUtil.getLocationName;
import static com.serviceplus.metadata.utility.CommonUtil.getTaskName;
import static com.serviceplus.metadata.utility.CommonUtil.getUserSessionDetails;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.metadata.dto.AssignedServiceDTO;
import com.serviceplus.metadata.dto.AssignedServiceDeliveryUnitDTO;
import com.serviceplus.metadata.dto.DesignatedOfficerAssignmentDTO;
import com.serviceplus.metadata.dto.FetchTaskHolders;
import com.serviceplus.metadata.dto.LevelEntityRequestDTO;
import com.serviceplus.metadata.dto.LevelWiseUnitsDTO;
import com.serviceplus.metadata.dto.OfficeDetailsDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitMappingDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitsResponseDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.dto.WorkflowAssignmentDTO;
import com.serviceplus.metadata.dto.WorkflowAssignmentTokenRequest;
import com.serviceplus.metadata.dto.WorkflowAssignmentTokenResponse;
import com.serviceplus.metadata.entity.ServiceDeliveryUnits;
import com.serviceplus.metadata.entity.ServiceDesignatedOfficerAssignment;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceMetadata;
import com.serviceplus.metadata.entity.WorkflowAssignment;
import com.serviceplus.metadata.entity.WorkflowAssignmentHistory;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.LgdFeignClient;
import com.serviceplus.metadata.kafka.WorkflowAssignmentKafkaProducer;
import com.serviceplus.metadata.kafka.dto.WorkflowAssignmentKafkaEvent;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceMetadataRepository;
import com.serviceplus.metadata.repository.ServiceDeliveryUnitsRepository;
import com.serviceplus.metadata.repository.ServiceDesignatedOfficerAssignmentRepository;
import com.serviceplus.metadata.repository.WorkflowAssignmentHistoryRepository;
import com.serviceplus.metadata.repository.WorkflowAssignmentRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class WorkflowAssignmentService {

    private final ServiceDeliveryUnitsRepository serviceDeliveryUnitsRepository;

    private static final Logger logger = LoggerFactory.getLogger("workflowAssignmentLogger");

    private final UserService userService;

    private final IServiceLogRepository serviceLogRepository;

    private final IServiceMetadataRepository serviceMetadataRepository;

    private final WorkflowAssignmentKafkaProducer workflowAssignmentKafkaProducer;

    private final WorkflowAssignmentRepository workflowAssignmentRepository;

    private final WorkflowAssignmentHistoryRepository workflowAssignmentHistoryRepository;
 
    private final ServiceDesignatedOfficerAssignmentRepository serviceDesignatedOfficerAssignmentRepository;
    
    private final LgdFeignClient lgdFeignClient;

    @Autowired
    public WorkflowAssignmentService(UserService userService,LgdFeignClient lgdFeignClient, IServiceLogRepository serviceLogRepository, IServiceMetadataRepository serviceMetadataRepository, WorkflowAssignmentKafkaProducer workflowAssignmentKafkaProducer, WorkflowAssignmentRepository workflowAssignmentRepository, WorkflowAssignmentHistoryRepository workflowAssignmentHistoryRepository, ServiceDesignatedOfficerAssignmentRepository serviceDesignatedOfficerAssignmentRepository, ServiceDeliveryUnitsRepository serviceDeliveryUnitsRepository) {
        this.userService = userService;
        this.serviceLogRepository = serviceLogRepository;
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.workflowAssignmentKafkaProducer = workflowAssignmentKafkaProducer;
        this.workflowAssignmentRepository = workflowAssignmentRepository;
        this.workflowAssignmentHistoryRepository = workflowAssignmentHistoryRepository;
		this.serviceDesignatedOfficerAssignmentRepository = serviceDesignatedOfficerAssignmentRepository;
		this.serviceDeliveryUnitsRepository = serviceDeliveryUnitsRepository;
		this.lgdFeignClient = lgdFeignClient;
    }

    @Transactional
    public WorkflowAssignmentTokenResponse create(WorkflowAssignmentTokenRequest request, HttpServletRequest httpRequest) {

        logger.info("Workflow assignment request received for serviceId : {}", request.getServiceId());

        UserSessionDTO sessionUser = Objects.requireNonNull(getUserSessionDetails(httpRequest));

        validateRequest(request, sessionUser);

        ServiceMetadata metadata = serviceMetadataRepository.findByServiceIdAndTenantId(request.getServiceId(), sessionUser.getTenantId());

        WorkflowAssignmentTokenRequest response = new WorkflowAssignmentTokenRequest();

        response.setServiceId(request.getServiceId());

        Map<String, WorkflowAssignmentTokenRequest.TaskAssignmentNode> validNodes = new LinkedHashMap<>();

        for (Map.Entry<String, WorkflowAssignmentTokenRequest.TaskAssignmentNode> entry : request.getNode().entrySet()) {

            String taskId = entry.getKey();
            
            WorkflowAssignmentTokenRequest.TaskAssignmentNode taskNode = entry.getValue();

            validateTask(taskId, taskNode, metadata);

            for (WorkflowAssignmentTokenRequest.TaskAssignmentNode.Users user : taskNode.getUsers()) {

                if(!isNull(user.getId())){
                    validateUserAssignment(user, request.getServiceId(), taskNode.getLocationId(), taskId, taskNode.getTaskName());
                }
                
                List<String> holderIds = user.getHolderIds();

                if (isNull(holderIds) || holderIds.isEmpty()) {

                    /*
                     * Scenario 1
                     * holder missing
                     * create new holder
                     */

                    createAssignment(request.getServiceId(), taskId, taskNode, user, sessionUser);

                    if(taskId.equals(metadata.getMetadataJson().getApplSubmissionTaskId())) {
                    	userService.addDEORole(user.getId());
                    }
                    else{
                        addWorkflowRoleIfRequired(user.getId(),sessionUser.getTenantId());
                    }

                    continue;
                }

                for (String holderId : holderIds) {

                    holderId = holderId == null ? "" : holderId.trim();

                    if (holderId.isBlank()) {
                        continue;
                    }

                    WorkflowAssignment holder = workflowAssignmentRepository
                            .findByHolderIdAndTenantId(holderId,sessionUser.getTenantId())
                            .orElseThrow(
                                    () -> new SPRuntimeError("Invalid holder id", HttpStatus.BAD_REQUEST)
                            );

                    /*
                     * Scenario 3
                     * holder present
                     * user absent
                     */

                    if (isNull(user.getId())) {

                        if (holder.getUserId() != null) {

                            Long previousUser = holder.getUserId();

                            moveToHistory(holder, sessionUser.getUserID());

                            holder.setUserId(null);
                            holder.setAssignedBy(sessionUser.getUserID());
                            holder.setUpdOn(new Date());

                            workflowAssignmentRepository.save(holder);

                            workflowAssignmentKafkaProducer.publishUserRemovedEvent(
                                    holder.getHolderId(),
                                    request.getServiceId(),
                                    taskId,
                                    taskNode.getLocationId(),
                                    sessionUser.getTenantId(),
                                    List.of(previousUser)
                            );

                            removeWorkflowRoleIfRequired(previousUser,sessionUser.getTenantId());
                            logger.info("User removed from holder {}", holderId);
                        }

                        continue;
                    }

                    /*
                     * Scenario 2
                     * holder + user present
                     */

                    Long previousUser = holder.getUserId();

                    if (Objects.equals(previousUser, user.getId())) {
                        logger.info("No change for holder {}", holderId);
                        continue;
                    }

                    if (previousUser != null) {

                        moveToHistory(holder, sessionUser.getUserID());

                        workflowAssignmentKafkaProducer.publishUserRemovedEvent(
                                holder.getHolderId(),
                                request.getServiceId(),
                                taskId,
                                taskNode.getLocationId(),
                                sessionUser.getTenantId(),
                                List.of(previousUser)
                        );

                        removeWorkflowRoleIfRequired(previousUser,sessionUser.getTenantId());
                    }

                    holder.setUserId(user.getId());
                    holder.setAssignedBy(sessionUser.getUserID());
                    holder.setUpdOn(new Date());

                    workflowAssignmentRepository.save(holder);

                    workflowAssignmentKafkaProducer.publishUserAddedEvent(
                            holder.getHolderId(),
                            request.getServiceId(),
                            taskId,
                            taskNode.getLocationId(),
                            sessionUser.getTenantId(),
                            List.of(user.getId())
                    );
                    if(taskId.equals(metadata.getMetadataJson().getApplSubmissionTaskId())) {
                        userService.addDEORole(user.getId());
                    }
                    else {
                        addWorkflowRoleIfRequired(user.getId(), sessionUser.getTenantId());
                    }

                    logger.info("Holder {} assigned to user {}", holderId, user.getId());
                }
            }

            validNodes.put(taskId, taskNode);
        }

        response.setNode(validNodes);
        response.setServiceId(request.getServiceId());

        logger.info("Workflow assignment completed for serviceId={}", request.getServiceId());

        return new WorkflowAssignmentTokenResponse(response);
    }

    private void addWorkflowRoleIfRequired(Long userId,String tenantId) {

        long count = workflowAssignmentRepository.countByUserId(userId);

        if (count == 1) {
            userService.addWorkflowRole(userId);
            logger.info("Assigning workflow role to user {}", userId);
        }
    }

    private void removeWorkflowRoleIfRequired(Long userId,String tenantId) {

        long count = workflowAssignmentRepository.countByUserId(userId);

        if (count == 0) {
            userService.removeWorkflowRole(userId);
            logger.info("Removing workflow role from user {}", userId);
        }
    }

    private void createAssignment(Integer serviceId, String taskId, WorkflowAssignmentTokenRequest.TaskAssignmentNode taskNode, WorkflowAssignmentTokenRequest.TaskAssignmentNode.Users assignedUser, UserSessionDTO sessionUser) {

        String holderId = generateUniqueToken(WORKFLOW_ASSIGNMENT_TOKEN_PREFIX);
        Date now  =  new Date();

        WorkflowAssignment entity = new WorkflowAssignment();

        entity.setHolderId(holderId);
        entity.setServiceId(serviceId);
        entity.setBaseServiceId(serviceId / 10000);
        entity.setTaskId(taskId);
        entity.setLocationId(taskNode.getLocationId());
        entity.setUserId(assignedUser.getId());
        entity.setAssignedBy(sessionUser.getUserID());
        entity.setCrtOn(now);
        entity.setUpdOn(now);
        entity.setTenantId(sessionUser.getTenantId());

        workflowAssignmentRepository.save(entity);

        workflowAssignmentKafkaProducer.publishUserAddedEvent(
                holderId,
                serviceId,
                taskId,
                taskNode.getLocationId(),
                sessionUser.getTenantId(),
                List.of(assignedUser.getId())
        );

        logger.info("Assignment created holderId : {} userId : {}", holderId, assignedUser.getId());
    }

    private void moveToHistory(WorkflowAssignment assignment, Long actionBy) {

        WorkflowAssignmentHistory history = new WorkflowAssignmentHistory();

        history.setHolderId(assignment.getHolderId());
        history.setServiceId(assignment.getServiceId());
        history.setBaseServiceId(assignment.getBaseServiceId());
        history.setLocationId(assignment.getLocationId());
        history.setTaskId(assignment.getTaskId());
        history.setUserId(assignment.getUserId());
        history.setAssignedBy(assignment.getAssignedBy());
        history.setCrtOn(assignment.getCrtOn());
        history.setUpdOn(assignment.getUpdOn());
        history.setTenantId(assignment.getTenantId());

        workflowAssignmentHistoryRepository.save(history);

        logger.info("Assignment moved to history holderId={} userId={}", assignment.getHolderId(), assignment.getUserId());
    }

    private void validateRequest(WorkflowAssignmentTokenRequest request,UserSessionDTO user) {

        if(isNull(user)){
            throw new SPRuntimeError("Invalid login",HttpStatus.FORBIDDEN);
        }

        if (isNull(request.getServiceId())) {
            throw new SPRuntimeError("Service Id is required",HttpStatus.BAD_REQUEST);
        }

        if (isNull(request.getNode()) || request.getNode().isEmpty()) {
            throw new SPRuntimeError("Node configuration is required",HttpStatus.BAD_REQUEST);
        }

        List<Integer> status = new ArrayList<Integer>();
        status.add(ServiceStatus.ACTIVATED.getCode());
        status.add(ServiceStatus.LAUNCHED.getCode());
        status.add(ServiceStatus.FROZEN.getCode());

        ServiceLog serviceLog = serviceLogRepository.findByServiceIdAndTenantIdAndServiceStatusIn(request.getServiceId(),user.getTenantId(), status);

        if (isNull(serviceLog)) {
            throw new SPRuntimeError("Invalid or inactive service",HttpStatus.CONFLICT);
        }
    }

    private void validateTask(String taskId, WorkflowAssignmentTokenRequest.TaskAssignmentNode node,ServiceMetadata metadata) {

        if (taskId == null || taskId.isBlank()) {
            throw new SPRuntimeError("Task Id is invalid", HttpStatus.BAD_REQUEST);
        }

        if (node.getLocationId() == null || node.getLocationId().isBlank()) {
            throw new SPRuntimeError("Location Id is required", HttpStatus.BAD_REQUEST);
        }

        if (node.getUsers() == null || node.getUsers().isEmpty()) {
            throw new SPRuntimeError("Users missing for task " + taskId,HttpStatus.BAD_REQUEST);
        }

        isTaskLocationValid(metadata,node,taskId);
    }

    private void validateUserAssignment(WorkflowAssignmentTokenRequest.TaskAssignmentNode.Users user,
                                        Integer serviceId, String locationId, String taskId, String taskName) {

        boolean valid = userService.canAssignUserToTask(user,serviceId,locationId,taskId);

        if (!valid) {
            throw new SPRuntimeError("User " + user.getName() + " cannot be assigned to task " + taskName,HttpStatus.BAD_REQUEST);
        }
    }

    private void isTaskLocationValid(ServiceMetadata metadata,WorkflowAssignmentTokenRequest.TaskAssignmentNode node,String taskId){

        if(isNull(metadata)
                || isNull(metadata.getMetadataJson())
                || isNull(metadata.getMetadataJson().getOfficeDetails())
                || metadata.getMetadataJson().getOfficeDetails().isEmpty()) {

            throw new SPRuntimeError("Office details not configured",HttpStatus.PRECONDITION_FAILED);
        }

        //CALL SPGD API TO CHECK USER LOCATION IS VALID TO TAKE ACTION IN THIS LOCATION
        boolean valid = metadata.getMetadataJson()
                .getOfficeDetails()
                .stream()
                .filter(office -> taskId.equals(office.getTaskId()))
                .filter(office -> office.getAllowedOffices() != null)
                .flatMap(office -> office.getAllowedOffices().stream())
                .anyMatch(office -> node.getLocationId().equals(String.valueOf(office.getOrgUnitCode())));

        if(!valid) {
            throw new SPRuntimeError("Location " + node.getLocationName() + " is not allowed for task " + node.getTaskName(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<WorkflowAssignmentKafkaEvent> getAssignmentsForUser(HttpServletRequest request, Long userId, Long locationId) {

        List<WorkflowAssignment> assignments = workflowAssignmentRepository
                .findByUserIdAndLocationId(
                        userId,
                        locationId.toString()
                );

        return assignments.stream().map(this::buildCacheDTO).toList();
    }

    public WorkflowAssignmentKafkaEvent getAssignment(Integer serviceId, String taskId, HttpServletRequest request) {

        UserSessionDTO sessionUser = Objects.requireNonNull(getUserSessionDetails(request));

        WorkflowAssignment assignment = workflowAssignmentRepository
                .findByServiceIdAndTaskIdAndLocationIdAndUserId(
                        serviceId,
                        taskId,
                        sessionUser.getLocationId().toString(),
                        sessionUser.getUserID()
                ).orElse(null);

        if (assignment == null) {
            return null;
        }

        return buildCacheDTO(assignment);
    }

    private WorkflowAssignmentKafkaEvent buildCacheDTO(WorkflowAssignment assignment) {

        WorkflowAssignmentKafkaEvent dto = new WorkflowAssignmentKafkaEvent();

        dto.setTokenKey(assignment.getHolderId());
        dto.setServiceId(assignment.getServiceId());
        dto.setBaseServiceId(assignment.getBaseServiceId());
        dto.setTaskId(assignment.getTaskId());
        dto.setLocationId(assignment.getLocationId());
        dto.setUserIds(List.of(assignment.getUserId()));
        dto.setTenantId(assignment.getTenantId());

        return dto;
    }

    public WorkflowAssignmentTokenResponse fetchAssignments(Integer serviceId, String taskId, Integer levelId, String locationId, HttpServletRequest request) {

        UserSessionDTO sessionUser = getUserSessionDetails(request);

        if (sessionUser == null) {
            throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
        }

        List<WorkflowAssignment> assignments;
        
        if(levelId == null) {
        	levelId = sessionUser.getEntityLevelId();
        }

        if(locationId == null) {
        	locationId = sessionUser.getLocationId().toString();
        }
        
        Boolean validLocationId = validateLocationId(sessionUser, levelId, locationId);
        
        if(!validLocationId) {
        	throw new SPRuntimeError("Invalid Location", HttpStatus.BAD_REQUEST);
        }
        
        if (taskId != null && !taskId.isBlank()) {
            assignments = workflowAssignmentRepository.findAllByServiceIdAndTaskIdAndLocationIdAndTenantId(serviceId, taskId,
            		locationId.toString(), sessionUser.getTenantId());

        } else {
            assignments = workflowAssignmentRepository.findAllByServiceIdAndLocationIdAndTenantId(serviceId,locationId.toString(),
                    sessionUser.getTenantId());
        }

        ServiceMetadata metadata = serviceMetadataRepository.findByServiceIdAndTenantId(serviceId, sessionUser.getTenantId());

        Set<Long> userIds = assignments.stream().map(WorkflowAssignment::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, String> userNameMap = new HashMap<>();

        WorkflowAssignmentTokenRequest response = new WorkflowAssignmentTokenRequest();
        response.setServiceId(serviceId);

        Map<String, WorkflowAssignmentTokenRequest.TaskAssignmentNode> nodes = new LinkedHashMap<>();

        for (WorkflowAssignment assignment : assignments) {

            String currentTaskId = assignment.getTaskId();

            WorkflowAssignmentTokenRequest.TaskAssignmentNode node = nodes.computeIfAbsent(currentTaskId, key -> createNode(metadata, assignment));

            WorkflowAssignmentTokenRequest.TaskAssignmentNode.Users dto = new WorkflowAssignmentTokenRequest.TaskAssignmentNode.Users();
            dto.setHolderIds(List.of(assignment.getHolderId()));

            if (assignment.getUserId() != null) {

                dto.setId(assignment.getUserId());
                dto.setName(userService.getUserName(assignment.getUserId()));

            } else {

                WorkflowAssignmentHistory history = workflowAssignmentHistoryRepository
                        .findTopByHolderIdAndTenantIdOrderByUpdOnDesc(
                                assignment.getHolderId(),sessionUser.getTenantId())
                        .orElse(null);

                if (history != null && history.getUserId() != null) {
                    WorkflowAssignmentTokenRequest.TaskAssignmentNode.Users.PreviousAssignment previous =
                            new WorkflowAssignmentTokenRequest.TaskAssignmentNode.Users.PreviousAssignment();

                    previous.setId(history.getUserId());
                    previous.setName(userService.getUserName(history.getUserId()));

                    dto.setHistory(previous);
                }
            }

            node.getUsers().add(dto);
        }

        response.setNode(nodes);

        logger.info("Workflow assignments fetched successfully for serviceId : {} taskId : {} totalAssignments : {}", serviceId, taskId, assignments.size());

        return new WorkflowAssignmentTokenResponse(response);
    }

    private Boolean validateLocationId(UserSessionDTO sessionUser, Integer levelId, String locationId) {
    	LevelEntityRequestDTO request = new LevelEntityRequestDTO();
        request.setLevelCode(levelId);
        request.setLocationCode(Long.parseLong(locationId));
        request.setEntityCode(sessionUser.getEntityId());
        request.setClc(sessionUser.getClcId() != null ? sessionUser.getClcId().longValue() : null);
        request.setParentUnitCode(sessionUser.getLocationId().longValue());
        
        boolean flag = false;
		if (!request.getParentUnitCode().equals(request.getLocationCode())) {
			List<LevelWiseUnitsDTO> units = lgdFeignClient.getLevelWiseUnits(request);
			for (LevelWiseUnitsDTO unit : units) {
				if (locationId.equals(unit.getEntityUnitCode().toString())) {
					flag = true;
					break;
				}
			}
		} else {
			flag = true;
		}
        
        return flag;
	}

	private WorkflowAssignmentTokenRequest.TaskAssignmentNode createNode(ServiceMetadata metadata, WorkflowAssignment assignment) {

        WorkflowAssignmentTokenRequest.TaskAssignmentNode node = new WorkflowAssignmentTokenRequest.TaskAssignmentNode();

        node.setTaskName(getTaskName(metadata, assignment.getTaskId()));
        node.setLocationId(assignment.getLocationId());
        node.setLocationName(getLocationName(metadata, assignment.getTaskId(), assignment.getLocationId()));
        node.setUsers(new ArrayList<>());

        return node;
    }


    

    public List<WorkflowAssignmentDTO> getAssignments(Integer serviceId, String taskId, String locationId) {

        return workflowAssignmentRepository.findAllByServiceIdAndTaskIdAndLocationId(serviceId, taskId, locationId)
                .stream().map(this::convertToDto).toList();
    }

    private WorkflowAssignmentDTO convertToDto(WorkflowAssignment entity) {
        WorkflowAssignmentDTO dto = new WorkflowAssignmentDTO();

        dto.setHolderId(entity.getHolderId());
        dto.setBaseServiceId(entity.getBaseServiceId());
        dto.setServiceId(entity.getServiceId());
        dto.setTaskId(entity.getTaskId());
        dto.setLocationId(entity.getLocationId());
        dto.setUserId(entity.getUserId());
        dto.setAssignedBy(entity.getAssignedBy());
        dto.setVersionNo(entity.getVersionNo());
        dto.setCrtOn(entity.getCrtOn());
        dto.setUpdOn(entity.getUpdOn());
        dto.setTenantId(entity.getTenantId());

        return dto;
    }

    public FetchTaskHolders fetchLocationsHolders(Integer serviceId, String taskId, HttpServletRequest request) {

        UserSessionDTO sessionUser = getUserSessionDetails(request);

        if (sessionUser == null) {
            throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
        }

        List<WorkflowAssignment> assignments;

        if (taskId != null && !taskId.isBlank()) {
            assignments = workflowAssignmentRepository.findAllByServiceIdAndTaskIdAndTenantId(serviceId, taskId, sessionUser.getTenantId());
        } else {
            assignments = workflowAssignmentRepository.findAllByServiceIdAndTenantId(serviceId, sessionUser.getTenantId());
        }

        ServiceMetadata metadata = serviceMetadataRepository.findByServiceIdAndTenantId(serviceId, sessionUser.getTenantId());

        FetchTaskHolders response = new FetchTaskHolders();
        response.setServiceId(serviceId);

        Map<String, List<FetchTaskHolders.UserNode>> nodes = new LinkedHashMap<>();

        for (WorkflowAssignment assignment : assignments) {

            FetchTaskHolders.UserNode dto = new FetchTaskHolders.UserNode();

            String locationName = getLocationName(metadata, assignment.getTaskId(), assignment.getLocationId());

            if (assignment.getUserId() != null) {
                locationName = locationName + " (" + userService.getUserName(assignment.getUserId()) + ")";
            }

            dto.setName(locationName);
            dto.setHolderId(assignment.getHolderId());
            dto.setLocationId(Long.parseLong(assignment.getLocationId()));

            nodes.computeIfAbsent(assignment.getTaskId(), k -> new ArrayList<>()).add(dto);
        }
        
        response.setNode(nodes);

        logger.info("Workflow location holders fetched successfully for serviceId:{} taskId:{} totalAssignments:{}", serviceId, taskId, assignments.size());

        return response;
    }

	@Transactional
	public ResponseEntity<?> assignDesignatedOfficer(DesignatedOfficerAssignmentDTO request,
			UserSessionDTO userSessionDetails) {

		JSONObject response = new JSONObject();

		try {
			ServiceMetadata metadata = serviceMetadataRepository.findByServiceIdAndTenantId(request.getServiceId(),
					userSessionDetails.getTenantId());
			if (metadata == null || metadata.getMetadataJson() == null) {
				response.put("message", "Service metadata not found.");
				return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
			}
			List<ServiceDeliveryUnitMappingDTO> mappings = metadata.getMetadataJson()
					.getServiceDeliveryUnitMappingDetails();
			if (mappings == null || mappings.isEmpty()) {
				response.put("message", "Service Delivery Unit mapping not found.");
				return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
			}
			ServiceDeliveryUnitMappingDTO mapping = mappings.stream()
					.filter(m -> m.getDeliveryUnitLevelId().equals(request.getDeliveryUnitLevelId())).findFirst()
					.orElse(null);
			if (mapping == null) {
				response.put("message", "Invalid Delivery Unit.");
				return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
			}
			//User level Id check?
			serviceDesignatedOfficerAssignmentRepository.deleteByTenantIdAndServiceIdAndDeliveryUnitLevelId(
					userSessionDetails.getTenantId(), request.getServiceId(), request.getDeliveryUnitLevelId());
			Date now = new Date();
			for (OfficeDetailsDTO.OfficeUnitData office : mapping.getOfficeUnits()) {
				ServiceDesignatedOfficerAssignment entity = new ServiceDesignatedOfficerAssignment();
				entity.setTenantId(userSessionDetails.getTenantId());
				entity.setServiceId(request.getServiceId());
				entity.setBaseServiceId(request.getServiceId()/10000);
				entity.setDeliveryUnitLevelId(request.getDeliveryUnitLevelId());
				entity.setLocationId(office.getOrgUnitCode());
				entity.setUserId(request.getUserId());
				entity.setDesignationId(userSessionDetails.getDesignationId());
				entity.setCreatedBy(userSessionDetails.getUserID().longValue());
				entity.setCreatedOn(now);
				entity.setDeletedFlag(false);
				serviceDesignatedOfficerAssignmentRepository.save(entity);
			}
			addDORoleIfRequired(request.getUserId(),userSessionDetails.getTenantId());
			response.put("message", "Designated Officer assigned successfully.");
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			logger.error("Error assigning Designated Officer", ex);
			response.put("message", "Internal Server Error");
			return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	public ResponseEntity<?> unassignDesignatedOfficer(DesignatedOfficerAssignmentDTO request,
			UserSessionDTO userSessionDetails) {
		JSONObject response = new JSONObject();
		try {
			serviceDesignatedOfficerAssignmentRepository.deleteByTenantIdAndServiceIdAndDeliveryUnitLevelId(
					userSessionDetails.getTenantId(), request.getServiceId(), request.getDeliveryUnitLevelId());
			response.put("message", "Designated Officer unassigned successfully.");
			return ResponseEntity.ok(response.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error unassigning Designated Officer", ex);
			response.put("message", "Internal Server Error");
			return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void addDORoleIfRequired(Long userId, String tenantId) {
		userService.addDesignatedOfficerRole(userId);
	}

	public ResponseEntity<?> getAssignedServices(Integer status, UserSessionDTO userSessionDetails) {

	    try {

	        List<Integer> serviceIds = serviceDesignatedOfficerAssignmentRepository.findAssignedServiceIds(
	                userSessionDetails.getTenantId(),
	                userSessionDetails.getUserID().longValue());

	        if (serviceIds.isEmpty()) {
	            return ResponseEntity.ok(Collections.emptyList());
	        }

	        List<ServiceLog> serviceLogs = (status != null)
	                ? serviceLogRepository.findByServiceIdInAndServiceStatus(serviceIds, status)
	                : serviceLogRepository.findByServiceIdIn(serviceIds);

	        if (serviceLogs.isEmpty()) {
	            return ResponseEntity.ok(Collections.emptyList());
	        }

	        Map<Integer, ServiceLog> serviceLogMap = serviceLogs.stream()
	                .collect(Collectors.toMap(
	                        ServiceLog::getServiceId,
	                        Function.identity(),
	                        (existing, replacement) -> existing));

	        List<ServiceMetadata> services = serviceMetadataRepository.findByTenantIdAndServiceIdIn(
	                userSessionDetails.getTenantId(),
	                new ArrayList<>(serviceLogMap.keySet()));

	        List<AssignedServiceDTO> response = services.stream()
	                .map(service -> {
	                    AssignedServiceDTO dto = new AssignedServiceDTO();
	                    dto.setServiceId(service.getServiceId());

	                    if (service.getMetadataJson() != null) {
	                        dto.setServiceName(service.getMetadataJson().getServiceName());
	                        dto.setServiceAbbrevation(service.getMetadataJson().getServiceAbbrevation());
	                    }

	                    ServiceLog serviceLog = serviceLogMap.get(service.getServiceId());
	                    if (serviceLog != null && serviceLog.getServiceStatus() != null) {
	                        dto.setStatus(ServiceStatus.fromCode(serviceLog.getServiceStatus()).name());
	                    }

	                    return dto;
	                })
	                .collect(Collectors.toList());

	        return ResponseEntity.ok(response);

	    } catch (Exception ex) {
	        logger.error("Error fetching assigned services", ex);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Internal Server Error");
	    }
	}

	public ResponseEntity<?> getAssignedServiceDeliveryUnits(Integer serviceId, UserSessionDTO userSessionDetails) {
		try {
			ServiceMetadata metadata = serviceMetadataRepository.findByServiceIdAndTenantId(serviceId,
					userSessionDetails.getTenantId());
			if (metadata == null || metadata.getMetadataJson() == null) {
				return ResponseEntity.ok(Collections.emptyList());
			}
			List<Integer> assignedLevels = serviceDesignatedOfficerAssignmentRepository.findAssignedDeliveryUnitLevels(
					userSessionDetails.getTenantId(), serviceId, userSessionDetails.getUserID().longValue());

			if (assignedLevels.isEmpty()) {
				return ResponseEntity.ok(Collections.emptyList());
			}
			List<AssignedServiceDeliveryUnitDTO> response = metadata.getMetadataJson()
					.getServiceDeliveryUnitMappingDetails().stream()
					.filter(mapping -> assignedLevels.contains(mapping.getDeliveryUnitLevelId())).map(mapping -> {
						AssignedServiceDeliveryUnitDTO dto = new AssignedServiceDeliveryUnitDTO();

						dto.setDeliveryUnitLevelId(mapping.getDeliveryUnitLevelId());

						dto.setDeliveryUnitLevelName(mapping.getDeliveryUnitLevelName());

						return dto;

					}).collect(Collectors.toList());

			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			logger.error("Error fetching assigned Service Delivery Units", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	public ResponseEntity<?> getAssignedOfficeDetails(Integer serviceId, Integer deliveryUnitLevelId, Integer page,
			Integer size, String search, UserSessionDTO userSessionDetails, Integer status) {

		Pageable pageable = PageRequest.of(page, size);

		List<ServiceDesignatedOfficerAssignment> assignments = serviceDesignatedOfficerAssignmentRepository
				.findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndUserIdAndDeletedFlagFalse(
						userSessionDetails.getTenantId(), serviceId, deliveryUnitLevelId,
						userSessionDetails.getUserID().longValue());

		if (assignments.isEmpty()) {
			return ResponseEntity.ok(Page.empty(pageable));
		}

		Set<Integer> assignedLocations = assignments.stream().map(ServiceDesignatedOfficerAssignment::getLocationId)
				.collect(Collectors.toSet());

		Page<ServiceDeliveryUnits> officePage;

		if (search != null && !search.trim().isEmpty()) {
			officePage = serviceDeliveryUnitsRepository
					.findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndLocationNameContainingIgnoreCaseAndDeletedFlagFalse(
							userSessionDetails.getTenantId(), serviceId, deliveryUnitLevelId, search.trim(), pageable);
		} else {
			officePage = serviceDeliveryUnitsRepository.findByTenantIdAndServiceIdAndDeliveryUnitLevelIdAndDeletedFlagFalse(
							userSessionDetails.getTenantId(), serviceId, deliveryUnitLevelId, pageable);
		}

		List<ServiceDeliveryUnitsResponseDTO> content = officePage.getContent().stream()
				.filter(x -> assignedLocations.contains(x.getLocationId()))
				.filter(x -> status == null || x.getActivationStatus().equals(status)).map(x -> {
					ServiceDeliveryUnitsResponseDTO dto = new ServiceDeliveryUnitsResponseDTO();
					dto.setServiceId(x.getServiceId());
					dto.setDeliveryUnitLevelId(x.getDeliveryUnitLevelId());
					dto.setLocationId(x.getLocationId());
					dto.setLocationName(x.getLocationName());
					dto.setActivationStatus(x.getActivationStatus());
					return dto;
				}).collect(Collectors.toList());

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("content", content);
		response.put("page", officePage.getNumber());
		response.put("size", officePage.getSize());
		response.put("totalElements", officePage.getTotalElements()); 
		response.put("totalPages", officePage.getTotalPages());

		return ResponseEntity.ok(response);
	}

    public List<WorkflowAssignmentKafkaEvent> getAssignmentsForService(Integer serviceId) {

        List<WorkflowAssignment> assignments = workflowAssignmentRepository.findAllByServiceIdAndUserIdIsNotNull(serviceId);

        return assignments.stream().map(this::buildCacheDTO).toList();
    }
}