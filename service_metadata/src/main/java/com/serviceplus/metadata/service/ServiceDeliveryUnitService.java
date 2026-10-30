package com.serviceplus.metadata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.metadata.dto.ProcessFlowDTO.DataDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.EdgeDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitDTO;
import com.serviceplus.metadata.dto.ServiceDeliveryUnitResDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDeliveryUnitDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceDeliveryUnitDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;
import com.serviceplus.metadata.versioning.enums.ServiceComponent;
import com.serviceplus.metadata.versioning.service.ServiceVersionTokenService;

@Service
public class ServiceDeliveryUnitService {
	@Autowired
	private IServiceDeliveryUnitDefinitionRepository serviceDeliveryUnitRepo;
	@Autowired
	private IServiceLogRepository serviceLogRepository;
	@Autowired
	private IProcessFlowRepository processFlowRepository;
	@Autowired
	private ServiceVersionTokenService serviceVersionTokenService;

	private static final Logger LOG = LogManager.getLogger("ServiceDeliveryUnitService");

	public ResponseEntity<?> getSubmissionLocation(Integer serviceId, UserSessionDTO userSessionDetails) {
		try {
			ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);
			if (Objects.isNull(serviceProcessFlow) || Objects.isNull(serviceProcessFlow.getProcessFlowJson())) {
				throw new SPRuntimeError("Service process flow not found.", HttpStatus.BAD_REQUEST);
			}
			List<NodeDTO> nextNode = getNextTasks(serviceProcessFlow);
			List<Object> responseData = new ArrayList<Object>();

			for (NodeDTO nodeDTO : nextNode) {
				DataDTO data = nodeDTO.getData();
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("taskId", nodeDTO.getId());
				map.put("taskname", data.getName());
				map.put("entities", data.getEntities());
				responseData.add(map);
			}

			return ResponseEntity.ok(responseData);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOG.error("Get Submission Location for service id {}, {}", serviceId, ex);
				ex.printStackTrace();
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getServiceDeliveryUnit(Integer serviceId, UserSessionDTO userSessionDetails) {
		try {
			ServiceDeliveryUnitDefinition serviceDeliveryUnitDefinition = serviceDeliveryUnitRepo.findByServiceIdAndUserId(serviceId,
					userSessionDetails.getUserID().longValue());
			ServiceLog servicelog = serviceLogRepository.findByServiceId(serviceId);
			if (Objects.isNull(serviceDeliveryUnitDefinition)) {
				throw new SPRuntimeError("Invalid Service Id", HttpStatus.BAD_REQUEST);
			}

			ServiceDeliveryUnitResDTO resDTO = new ServiceDeliveryUnitResDTO();
			resDTO.setServiceDeliveryUnits(serviceDeliveryUnitDefinition.getServiceDeliveryUnits());
			resDTO.setServiceId(serviceDeliveryUnitDefinition.getServiceId());
			resDTO.setSduSource(serviceDeliveryUnitDefinition.getSource());
			resDTO.setTablist(servicelog.getTablist());
			if (serviceVersionTokenService.isMinorModification(serviceId)) {
				ServiceDeliveryUnitDefinition baseline = serviceVersionTokenService.getBaselineDeliveryUnitDefinition(serviceId);
				if (baseline != null) {
					resDTO.setHistoryServiceDeliveryUnits(baseline.getServiceDeliveryUnits());
					resDTO.setOldServiceDeliveryUnits(baseline.getServiceDeliveryUnits());
					resDTO.setHistorySduSource(baseline.getSource());
					resDTO.setOldSduSource(baseline.getSource());
				}
			}
			return ResponseEntity.ok().body(resDTO);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOG.error("Get Service Coverage for service id {}, {}", serviceId, ex);
				ex.printStackTrace();
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> saveServiceDeliveryUnit(ServiceDeliveryUnitDTO serviceDeliveryUnitDTO,
			UserSessionDTO userSessionDetails) {
		JSONObject responseJson = new JSONObject();
		try {
			Integer serviceId = serviceDeliveryUnitDTO.getServiceId();

			// 1. Validate if DELIVERY_UNIT module is allowed for minor version modification
			serviceVersionTokenService.validateComponentAllowed(serviceId, ServiceComponent.DELIVERY_UNIT);

			ServiceDeliveryUnitDefinition serviceDeliveryUnitDefinition = serviceDeliveryUnitRepo.findByServiceIdAndUserId(serviceId,
					userSessionDetails.getUserID().longValue());
			if (serviceDeliveryUnitDefinition == null) {
				serviceDeliveryUnitDefinition = serviceDeliveryUnitRepo.findByServiceId(serviceId);
			}
			ServiceLog serviceLog = serviceLogRepository.findByServiceId(serviceId);
			if (Objects.isNull(serviceLog)) {
				throw new SPRuntimeError("Invalid Service", HttpStatus.BAD_REQUEST);
			}

			// 2. Enforce SDU deletion restriction if minor modification is active
			boolean isMinorVersion = serviceVersionTokenService.isMinorModification(serviceId);
			if (isMinorVersion) {
				serviceVersionTokenService.validateDeliveryUnitsMinorVersion(serviceId, serviceDeliveryUnitDTO, serviceDeliveryUnitDefinition);
			}

			List<ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON> oldUnits = (serviceDeliveryUnitDefinition != null) ? serviceDeliveryUnitDefinition.getServiceDeliveryUnits() : null;
			String oldSource = (serviceDeliveryUnitDefinition != null) ? serviceDeliveryUnitDefinition.getSource() : null;

			if (Objects.isNull(serviceDeliveryUnitDefinition))
				serviceDeliveryUnitDefinition = new ServiceDeliveryUnitDefinition();

			serviceDeliveryUnitDefinition.setServiceId(serviceId);
			serviceDeliveryUnitDefinition.setServiceDeliveryUnits(serviceDeliveryUnitDTO.getServiceDeliveryUnits());
			serviceDeliveryUnitDefinition.setCreationDate(new Date());
			serviceDeliveryUnitDefinition.setVersionNo(serviceId % 10000);
			serviceDeliveryUnitDefinition.setMinorVersionNo(serviceLog.getMinorVersionNo() != null ? serviceLog.getMinorVersionNo() : 0);
			serviceDeliveryUnitDefinition.setUserId(userSessionDetails.getUserID().longValue());
			serviceDeliveryUnitDefinition.setStateId(userSessionDetails.getClcId());
			serviceDeliveryUnitDefinition.setSource(serviceDeliveryUnitDTO.getSduSource());
			responseJson = saveDeliveryUnit(responseJson, serviceDeliveryUnitDefinition, serviceLog);

			// Record SDU changes in database audit log only for minor version modification
			if (isMinorVersion) {
				serviceVersionTokenService.recordDeliveryUnitChangeLog(serviceId, oldUnits, serviceDeliveryUnitDTO.getServiceDeliveryUnits(), oldSource, serviceDeliveryUnitDTO.getSduSource(), userSessionDetails);
			}

			return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOG.error("Save Service Coverage for service id {}, {}", serviceDeliveryUnitDTO.getServiceId(), ex);
				ex.printStackTrace();
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	private List<NodeDTO> getNextTasks(ServiceProcessFlow serviceProcessFlow) {
		List<NodeDTO> nextTasks = new ArrayList<>();
		List<NodeDTO> nodes = serviceProcessFlow.getProcessFlowJson().getNodes();
		List<EdgeDTO> edges = serviceProcessFlow.getProcessFlowJson().getEdges();

		Map<String, NodeDTO> nodeMap = nodes.stream().collect(Collectors.toMap(NodeDTO::getId, Function.identity()));

		NodeDTO submissionNode = nodes.stream()
				.filter(node -> ApplicationConstants.SUBMISSION_NODE_TYPE.equals(node.getType())).findFirst()
				.orElse(null);
		if (submissionNode != null) {
			EdgeDTO submissionEdge = edges.stream().filter(edge -> submissionNode.getId().equals(edge.getSource()))
					.findFirst().orElse(null);
			if (submissionEdge != null) {
				NodeDTO targetNode = nodeMap.get(submissionEdge.getTarget());
				if (targetNode != null) {
					if (ApplicationConstants.GATEWAY_NODE_TYPE.equals(targetNode.getType())) {
						nextTasks = edges.stream().filter(edge -> targetNode.getId().equals(edge.getSource()))
								.map(edge -> nodeMap.get(edge.getTarget())).filter(Objects::nonNull)
								.collect(Collectors.toList());

					} else {
						nextTasks.add(targetNode);
					}
				}
			}
		}
		return nextTasks;
	}

	@Transactional
	private JSONObject saveDeliveryUnit(JSONObject responseJson, ServiceDeliveryUnitDefinition serviceDeliveryUnitDefinition,
			ServiceLog serviceLog) {
		String tablist = serviceLog.getTablist();
		ServiceDeliveryUnitDefinition savedEntity = serviceDeliveryUnitRepo.save(serviceDeliveryUnitDefinition);
		if (!tablist.contains("2")) {
			serviceLog.setTablist(serviceLog.getTablist() + ",2");
			ServiceLog savedLogEntity = serviceLogRepository.save(serviceLog);
			tablist = savedLogEntity.getTablist();
		}
		responseJson.put("serviceId", savedEntity.getServiceId());
		responseJson.put("message", "Service Coverage saved successfully");
		responseJson.put("tabList", tablist);
		return responseJson;
	}

}
