package com.serviceplus.metadata.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.document.mapping.dto.TaskDocs;
import com.serviceplus.metadata.dto.ProcessFlowDTO.EdgeDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO;
import com.serviceplus.metadata.dto.ServiceOutputFormatDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceOutputFormat;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.enums.NotifType;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceOutputFormatRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;

@Service
public class ServiceOutputFormatService {
	
	private static final Logger LOG = LogManager.getLogger("ServiceOutputFormatService");
	@Autowired
	private IServiceOutputFormatRepository outputFormatRepository;
	@Autowired
	private IProcessFlowRepository processFlowRepository;

	public ResponseEntity<?> allCertificate(Integer serviceId) {
		List<ServiceOutputFormatDTO> dtos = new ArrayList<ServiceOutputFormatDTO>();
		try {
			ServiceOutputFormatDTO filledFormDto = new ServiceOutputFormatDTO();
			filledFormDto.setServiceId(serviceId);
			filledFormDto.setOutputFormatId(0L);
			filledFormDto.setTemplateName("Filled Form");
			dtos.add(filledFormDto);

			List<ServiceOutputFormat> formats = outputFormatRepository.findAllDocumentByServiceId(serviceId);
			for (ServiceOutputFormat format : formats) {
				if (format.getOutputFormatJson() != null
						&& "Filled Form".equalsIgnoreCase(format.getOutputFormatJson().getTemplateName())) {
					continue;
				}
				ServiceOutputFormatDTO dto = new ServiceOutputFormatDTO();
				dto.setServiceId(format.getServiceId());
				dto.setOutputFormatId(format.getId());
				if (format.getOutputFormatJson() != null) {
					dto.setTemplateName(format.getOutputFormatJson().getTemplateName());
				}
				dtos.add(dto);
			}
			return ResponseEntity.ok().body(dtos);
		} catch (Exception e) {
			LOG.error("ServiceId : {} ,Exception : {}", serviceId, e);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> allNotification(Integer serviceId, String type) {
		List<ServiceOutputFormatDTO> dtos = new ArrayList<ServiceOutputFormatDTO>();
		try {
			if (type == null) {
				List<ServiceOutputFormat> formats = outputFormatRepository.findAllNotificationsByServiceId(serviceId);
				for (ServiceOutputFormat format : formats) {
					ServiceOutputFormatDTO dto = new ServiceOutputFormatDTO();
					dto.setServiceId(format.getServiceId());
					dto.setOutputFormatId(format.getId());
					dto.setTemplateName(format.getOutputFormatJson().getTemplateName());
					dtos.add(dto);
				}
			} else {
				try {
					NotifType notifType = NotifType.fromId(type);
					type = notifType.getId();
				} catch (Exception e) {
					LOG.info("Invalid notification type {}, serviceId {}", type, serviceId);
					throw new SPRuntimeError("Invalid notification type", HttpStatus.BAD_REQUEST);
				}
				List<ServiceOutputFormat> formats = outputFormatRepository.findNotificationsByType(serviceId, type);
				for (ServiceOutputFormat format : formats) {
					ServiceOutputFormatDTO dto = new ServiceOutputFormatDTO();
					dto.setServiceId(format.getServiceId());
					dto.setOutputFormatId(format.getId());
					dto.setTemplateName(format.getOutputFormatJson().getTemplateName());
					dtos.add(dto);
				}
			}
			return ResponseEntity.ok().body(dtos);
		} catch (Exception e) {
			LOG.error("ServiceId : {} ,Exception : {}", serviceId, e);
			throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getPreviousTaskDoc(Integer serviceId, String taskId, UserSessionDTO userSessionDetails) {
		try {
			ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceIdAndUserId(serviceId,
					userSessionDetails.getUserID().longValue());
			if (serviceProcessFlow == null || serviceProcessFlow.getProcessFlowJson() == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			List<TaskDocs> resList = getPreviousTasksDocument(serviceProcessFlow, taskId);
			return ResponseEntity.ok(resList);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				LOG.error("Get all previous for service id {},taskId {}, {}", serviceId, taskId, ex);
				throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public List<TaskDocs> getPreviousTasksDocument(ServiceProcessFlow serviceProcessFlow, String taskId) {

		List<NodeDTO> nodes = serviceProcessFlow.getProcessFlowJson().getNodes();
		List<EdgeDTO> edges = serviceProcessFlow.getProcessFlowJson().getEdges();

		Map<String, NodeDTO> nodeMap = nodes.stream().collect(Collectors.toMap(NodeDTO::getId, Function.identity()));

		Map<String, List<String>> incomingMap = edges.stream().collect(
				Collectors.groupingBy(EdgeDTO::getTarget, Collectors.mapping(EdgeDTO::getSource, Collectors.toList())));

		Set<String> previousTaskIds = new LinkedHashSet<>();
		collectPreviousTasks(taskId, incomingMap, nodeMap, new HashSet<>(), previousTaskIds);

		List<TaskDocs> result = new ArrayList<>();

		for (String previousTaskId : previousTaskIds) {

			NodeDTO previousNode = nodeMap.get(previousTaskId);
			if (previousNode == null || previousNode.getData() == null) {
				continue;
			}

			TaskDocs previousTask = new TaskDocs();
			previousTask.setTaskId(previousNode.getId());
			previousTask.setTaskName(previousNode.getData().getName());

			List<TaskDocs.Document> documents = Optional.ofNullable(previousNode.getData().getDocumentMapping())
					.orElse(Collections.emptyList()).stream().filter(doc -> doc.getReferenceId() != null).map(doc -> {
						TaskDocs.Document document = new TaskDocs.Document();
						document.setReferenceId(doc.getReferenceId());
						document.setDocumentName(doc.getDocumentName());
						return document;
					}).collect(Collectors.toList());

			previousTask.setDocuments(documents);

			result.add(previousTask);
		}

		return result;
	}

	private void collectPreviousTasks(String nodeId, Map<String, List<String>> incomingMap,
			Map<String, NodeDTO> nodeMap, Set<String> visited, Set<String> result) {

		if (!visited.add(nodeId)) {
			return;
		}

		List<String> sources = incomingMap.get(nodeId);
		if (sources != null && !sources.isEmpty()) {
			for (String source : sources) {

				NodeDTO node = nodeMap.get(source);

				if (node == null) {
					continue;
				}

				if (ApplicationConstants.GATEWAY_NODE_TYPE.equals(node.getType())) {
					collectPreviousTasks(source, incomingMap, nodeMap, visited, result);
				} else {
					result.add(source);
				}
			}
		}
	}

}
