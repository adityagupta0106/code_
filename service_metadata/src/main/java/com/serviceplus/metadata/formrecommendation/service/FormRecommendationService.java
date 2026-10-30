package com.serviceplus.metadata.formrecommendation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.dto.ProcessFlowDTO.FormDetail;
import com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.enums.ServiceStatus;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.formrecommendation.dto.FormTaskDTO;
import com.serviceplus.metadata.formrecommendation.dto.GlobalSearchRequestDTO;
import com.serviceplus.metadata.formrecommendation.dto.ServiceDTO;
import com.serviceplus.metadata.formrecommendation.dto.UniqueFormResponseDTO;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;

@Service
public class FormRecommendationService {

	@Autowired
	private IServiceDefinitionRepository serviceDefinitionRepository;
	@Autowired
	private IServiceLogRepository serviceLogRepository;
	@Autowired
	private IProcessFlowRepository processFlowRepository;
	@Autowired
	private GlobalSearchService globalSearchService;

	public ResponseEntity<?> getServiceList(UserSessionDTO userSessionDetails) {
		List<ServiceDTO> dtos = new ArrayList<ServiceDTO>();
		try {
			List<ServiceDefinition> activeServices = serviceDefinitionRepository.findActiveServicesAndStatus(
					userSessionDetails.getTenantId(),
					List.of(ServiceStatus.FROZEN.getCode(), ServiceStatus.LAUNCHED.getCode()));
			for (ServiceDefinition serviceObj : activeServices) {
				ServiceDTO dto = new ServiceDTO();
				dto.setServiceId(serviceObj.getServiceId());
				dto.setServiceName(serviceObj.getDefinitionJson().getName());
				dtos.add(dto);
			}
			return ResponseEntity.ok(dtos);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getAllFormsOfService(UserSessionDTO userSessionDetails, Integer srvid) {
		JSONObject responseJson = new JSONObject();
		List<FormTaskDTO> formTaskDTOs = new ArrayList<FormTaskDTO>();
		try {
			List<Integer> status = new ArrayList<Integer>();
			status.add(ServiceStatus.FROZEN.getCode());
			status.add(ServiceStatus.LAUNCHED.getCode());
			ServiceLog serviceLog = serviceLogRepository.findByServiceIdAndTenantIdAndServiceStatusIn(srvid,
					userSessionDetails.getTenantId(), status);
			if (Objects.isNull(serviceLog)) {
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
            	responseJson.put("errorMessage", "Service not found");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceId(serviceLog.getServiceId());
			if (Objects.isNull(serviceProcessFlow) || Objects.isNull(serviceProcessFlow.getProcessFlowJson())) {
				responseJson.put("errorCode", HttpStatus.BAD_REQUEST.value());
				responseJson.put("errorMessage", "Service Process Flow not found");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			List<NodeDTO> nodes = serviceProcessFlow.getProcessFlowJson().getNodes();
			for (NodeDTO nodeDTO : nodes) {
				FormDetail formDetails = nodeDTO.getData() != null
						? nodeDTO.getData().getFormDetail() != null ? nodeDTO.getData().getFormDetail() : null
						: null;
				if (!Objects.isNull(formDetails)) {
					String taskName = nodeDTO.getData() != null ? nodeDTO.getData().getName() : "";
					String formId = formDetails.getFormId();
					String formName = formDetails.getParentForm();
					formTaskDTOs.add(new FormTaskDTO(formId, formName, taskName));
				}

			}
			return ResponseEntity.ok(formTaskDTOs);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> searchforms(UserSessionDTO userSessionDetails, String searchQuery) {
		try {
			String tenantId = userSessionDetails.getTenantId();
			GlobalSearchRequestDTO globalSearchRequestDTO = new GlobalSearchRequestDTO();
			globalSearchRequestDTO.setKeyword(searchQuery);
			globalSearchRequestDTO.setTenantId(tenantId);
			List<UniqueFormResponseDTO> globalSearch = globalSearchService.globalSearch(globalSearchRequestDTO);
			return ResponseEntity.ok(globalSearch);
		} catch (Exception ex) {
		ex.printStackTrace();
		throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	}

}
