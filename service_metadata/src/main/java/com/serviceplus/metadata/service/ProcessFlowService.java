package com.serviceplus.metadata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.serviceplus.metadata.dto.FormEncryptionKeyDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.FormDetail;
import com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO;
import com.serviceplus.metadata.dto.ProcessFlowSubmissionDetailsDTO;
import com.serviceplus.metadata.dto.ServiceTemplateMappingDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.entity.ServiceTemplate;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.FormDesignerFeignClient;
import com.serviceplus.metadata.formrecommendation.dto.ServiceSearchDocument;
import com.serviceplus.metadata.formrecommendation.dto.ServiceSearchDocument.AttachedFormDTO;
import com.serviceplus.metadata.formrecommendation.service.ServiceSearchElasticService;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceTemplateRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;

import feign.FeignException;
import com.serviceplus.metadata.versioning.enums.ServiceComponent;
import com.serviceplus.metadata.versioning.service.ServiceVersionTokenService;

import static java.util.Objects.isNull;

@Service
public class ProcessFlowService {
	
	@Autowired
	private IProcessFlowRepository processFlowRepository;
	
	@Autowired
	private IServiceLogRepository serviceLogRepository;
	
	@Autowired
	private IServiceTemplateRepository serviceTemplateRepository;
	
	@Autowired
    private ServiceMetadataService serviceMetadataService;
	
	@Autowired
	private FormDesignerFeignClient formFeignClient;
	
	@Autowired
	private IServiceDefinitionRepository serviceDefinitionRepository;
	@Autowired
	private ServiceSearchElasticService elasticService;
	@Autowired
	private ServiceVersionTokenService serviceVersionTokenService;
	@Autowired
	private com.fasterxml.jackson.databind.ObjectMapper objectMapper;
	
	@SuppressWarnings("unchecked")
	public ResponseEntity<?> saveProcessFlow(ProcessFlowDTO processFlowDTO, UserSessionDTO userSessionDetails) {
		JSONObject responseJson = new JSONObject();
		try {
			Integer serviceId=processFlowDTO.getServiceId();
			ProcessFlowDTO savedProcessFlowDTO=null;
			Map<String, FormDetail> oldNodeFormMap = new HashMap<>();
			Set<String> currentNodeIdSet = new HashSet<>();

			// 1. Validate if PROCESS_FLOW module is allowed for minor version modification
			serviceVersionTokenService.validateComponentAllowed(serviceId, ServiceComponent.PROCESS_FLOW);

			ServiceProcessFlow serviceProcessFlow=processFlowRepository.findByServiceIdAndUserId(serviceId,userSessionDetails.getUserID().longValue());
			if (serviceProcessFlow == null) {
				serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);
			}
			ServiceLog serviceLog=serviceLogRepository.findByServiceId(serviceId);
			if(isNull(serviceLog)) {
				responseJson.put("errorCode", "400");
            	responseJson.put("errorMessage", "Invalid Service");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			if(isNull(serviceProcessFlow)) {
				serviceProcessFlow=new ServiceProcessFlow();
				serviceProcessFlow.setcDate(new Date());
			}else {
				if (serviceProcessFlow.getProcessFlowJson() != null) {
					try {
						savedProcessFlowDTO = objectMapper.readValue(
								objectMapper.writeValueAsString(serviceProcessFlow.getProcessFlowJson()),
								ProcessFlowDTO.class);
					} catch (Exception ignored) {
						savedProcessFlowDTO = serviceProcessFlow.getProcessFlowJson();
					}
				}
			}

			// 2. Enforce minute-level restrictions if minor modification is active
			boolean isMinorVersion = serviceVersionTokenService.isMinorModification(serviceId);
			if (isMinorVersion) {
				serviceVersionTokenService.validateProcessFlowMinorVersion(serviceId, processFlowDTO, savedProcessFlowDTO);
			}
			if (savedProcessFlowDTO != null && savedProcessFlowDTO.getNodes() != null) {
			    for (NodeDTO oldNode : savedProcessFlowDTO.getNodes()) {
			        if (oldNode.getData() != null && oldNode.getData().getFormDetail() != null) {
			            oldNodeFormMap.put(oldNode.getId(), oldNode.getData().getFormDetail());
			        }
			    }
			}
			if(serviceProcessFlow.getId()!=null) {
				serviceProcessFlow.setuDate(new Date());
			}
			ArrayList<AttachedFormDTO> attached = new ArrayList<ServiceSearchDocument.AttachedFormDTO>();
			if(!processFlowDTO.getNodes().isEmpty()) {
				
				for(NodeDTO node:processFlowDTO.getNodes()) {
					currentNodeIdSet.add(node.getId());
					if(ApplicationConstants.SUBMISSION_NODE_TYPE.equals(node.getType())){
						node.getData().setName(ApplicationConstants.SUBMISSION_NODE_NAME);
					}
					FormDetail formDetail = node.getData().getFormDetail();
					if(formDetail!=null) {
						ServiceTemplate serviceTemplate = serviceTemplateRepository.findByServiceIdAndTaskId(serviceId,node.getId());
						if(serviceTemplate==null) {
							serviceTemplate=new ServiceTemplate();
							serviceTemplate.setcDate(new Date());
						}
						formDetail.setIsSubscribed(true);
						// Form Subscription
						if (formDetail != null) {
							ResponseEntity<Map<String, String>> formDetails;
							try {
								String oldFormId = "";
								String newFormId = formDetail.getFormId();
								if (oldNodeFormMap.containsKey(node.getId())) {
									FormDetail oldFormDetail = oldNodeFormMap.get(node.getId());
									oldFormId = oldFormDetail.getFormId();
									if(!newFormId.equals(oldFormId)) {
										try {
										formFeignClient.formUnSubscribe(oldFormId, false);
										}catch (Exception ex) {
											ex.printStackTrace();
										}
									}
								}
								
								if (!isNull(formDetail.getIsSubscribed()) && formDetail.getIsSubscribed() && !newFormId.equals(oldFormId)) {
									formDetails = (ResponseEntity<Map<String, String>>) formFeignClient
											.formSubscribe(formDetail.getFormId());
									if (formDetails != null && formDetails.getStatusCode().equals(HttpStatus.OK)) {
										Map<String, String> body = formDetails.getBody();
										formDetail.setFormId(body.get("formId"));
										formDetail.setHolderId(body.get("holderId"));
									}
								}
							} catch (FeignException ex) {
                                ex.printStackTrace();
								return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
							} catch (Exception ex) {
								ex.printStackTrace();
								responseJson.put("errorCode", "500");
								responseJson.put("errorMessage", "Internal Server Error");
								return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
										.body(responseJson.toString());
							}
						}
						
						serviceTemplate.setFormId(formDetail.getFormId());
						serviceTemplate.setHolderId(formDetail.getHolderId());
						serviceTemplate.setTaskId(node.getId());
						serviceTemplate.setServiceId(serviceId);
						serviceTemplate.setVersionNo(serviceId%10000);
						serviceTemplate.setMinorVersion(serviceLog.getMinorVersionNo() != null ? serviceLog.getMinorVersionNo() : 0);
						serviceTemplate.setUserId(userSessionDetails.getUserID().longValue());
						serviceTemplate.setBaseServiceId(serviceId/10000);
						serviceTemplate.setTemplateName(formDetail.getTemplateName());
						FormEncryptionKeyDTO formEncryptionDTO=new FormEncryptionKeyDTO(serviceId,formDetail,node.getId());
						ResponseEntity<Map<String, Object>> saveFormEncryptionDetail = formFeignClient.saveFormEncryptionKey(formEncryptionDTO);
						if(saveFormEncryptionDetail.getStatusCode().equals(HttpStatus.OK)) {
							serviceTemplateRepository.save(serviceTemplate);
						}
						if(formDetail.getStandalone()!=null && formDetail.getStandalone()) {
							ServiceTemplateMappingDTO templateMappingDto=new ServiceTemplateMappingDTO();
							templateMappingDto.setCreatedBy(userSessionDetails.getUserID().longValue());
							templateMappingDto.setDepartmentId(userSessionDetails.getEntityId());
							templateMappingDto.setStateId(userSessionDetails.getClcId());
							templateMappingDto.setLocationId(userSessionDetails.getLocationId());
							templateMappingDto.setInstantiable("N");
							templateMappingDto.setFormId(formDetail.getFormId());
							
							ResponseEntity<Map<String, Object>> saveInstantiableForm = formFeignClient.saveInstantiableForm(templateMappingDto);
							if(saveInstantiableForm.getStatusCode().equals(HttpStatus.OK)) {
								Map<String, Object> responseBody = saveInstantiableForm.getBody();
								String formId=(String) responseBody.get("formId");
								formDetail.setFormId(formId);
							}
						}
						
						// set form data form Elastic-search
						AttachedFormDTO attachedFormDTO = new AttachedFormDTO();
						attachedFormDTO.setTaskName(node.getData().getName());
						attachedFormDTO.setFormId(formDetail.getFormId());
						attachedFormDTO.setFormName(formDetail.getParentForm());
						attached.add(attachedFormDTO);
						
					}else {
						if (oldNodeFormMap.containsKey(node.getId())) {
							serviceMetadataService.deleteTemplate(serviceId,node.getId(),userSessionDetails);
				        }
					}
				}
			}
			

			for (String oldNodeId : oldNodeFormMap.keySet()) {
			    if (!currentNodeIdSet.contains(oldNodeId)) {
			        serviceMetadataService.deleteTemplate(serviceId, oldNodeId, userSessionDetails);
			    }
			}
			responseJson =validateProcessFlowJson(responseJson,processFlowDTO,userSessionDetails);
			if(responseJson.getBoolean("status")) {
				serviceProcessFlow.setServiceId(serviceId);
				serviceProcessFlow.setBaseServiceId(serviceId/10000);
				serviceProcessFlow.setProcessFlowJson(processFlowDTO);
				serviceProcessFlow.setVersionNo(serviceId%10000);
				serviceProcessFlow.setMinorVersionNo(serviceLog.getMinorVersionNo() != null ? serviceLog.getMinorVersionNo() : 0);
				serviceProcessFlow.setUserId(userSessionDetails.getUserID().longValue());
				serviceProcessFlow.setStateId(userSessionDetails.getClcId());
				serviceProcessFlow.setDepartmentId(userSessionDetails.getEntityId());
				responseJson=saveProcessFlow(responseJson,serviceProcessFlow,serviceLog,"");
				
				try {
					if(attached.size() > 0) {
						ServiceDefinition serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
						ServiceSearchDocument serviceSearchDocument = new ServiceSearchDocument();
						serviceSearchDocument.setServiceId(serviceProcessFlow.getServiceId());
						serviceSearchDocument.setServiceName(serviceDefinition.getDefinitionJson().getName());
						serviceSearchDocument.setServiceDescription(serviceDefinition.getDefinitionJson().getDescription());
						serviceSearchDocument.setTenantId(serviceLog.getTenantId());
						serviceSearchDocument.setDepartmentId(serviceDefinition.getDepartmentId().toString());
						serviceSearchDocument.setAttachedForm(attached);
						elasticService.upsert(serviceSearchDocument);
					}else {
						elasticService.delete(serviceProcessFlow.getServiceId().longValue());					
					}
				}catch (Exception e) {
					e.printStackTrace();
				}

				// Record detailed process flow changes in database audit log
				if (isMinorVersion) {
					serviceVersionTokenService.recordProcessFlowChangeLog(serviceId, savedProcessFlowDTO, processFlowDTO, userSessionDetails);
				}
				
				responseJson.remove("status");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				responseJson.put("errorCode", "500");
				responseJson.put("errorMessage", "Internal Server Error");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@Transactional
	private JSONObject saveProcessFlow(JSONObject responseJson, ServiceProcessFlow serviceProcessFlow,
			ServiceLog serviceLog,String draftFlag) {
		try {
			String tablist=serviceLog.getTablist();
			ServiceProcessFlow savedEntity = processFlowRepository.save(serviceProcessFlow);
			if(!draftFlag.equals("D") && !tablist.contains("4")) {
				serviceLog.setTablist(serviceLog.getTablist()+",4");
				ServiceLog savedLogEntity = serviceLogRepository.save(serviceLog);
				tablist=savedLogEntity.getTablist();
			}
			responseJson.put("processFlowId", savedEntity.getId());
			responseJson.put("message", "Process Flow saved successfully");
			responseJson.put("tabList", tablist);
			return responseJson;
		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return responseJson;
		}
	}

	private JSONObject validateProcessFlowJson(JSONObject responseJson, ProcessFlowDTO processFlowDTO,
			UserSessionDTO userSessionDetails) {
		responseJson.put("status", true);
		return responseJson;
	}

	public ResponseEntity<?> getProcessFlow(Integer serviceId, UserSessionDTO userSessionDetails, String draft) {
		JSONObject responseJson = new JSONObject();
		try {
			ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID());
			if (serviceProcessFlow == null) {
				serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);
			}
			ServiceLog servicelog = serviceLogRepository.findByServiceId(serviceId);
			if (isNull(servicelog)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Invalid Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			if(draft.equalsIgnoreCase("D")) {
				JsonNode jsonNode = serviceProcessFlow != null ? serviceProcessFlow.getProcessFlowObject() : null;
				if(jsonNode==null) {
					responseJson.put("errorCode", "404");
					responseJson.put("errorMessage", "Draft not created");
					return new ResponseEntity<>(responseJson.toString(), HttpStatus.NOT_FOUND);
				}
				return ResponseEntity.ok().body(jsonNode);
			}else {
				ProcessFlowDTO dto = serviceProcessFlow != null ? serviceProcessFlow.getProcessFlowJson() : null;
				if(dto==null)
					dto=new ProcessFlowDTO();
				if (serviceProcessFlow != null) {
					dto.setServiceId(serviceProcessFlow.getServiceId());
				} else {
					dto.setServiceId(serviceId);
				}
				dto.setTablist(servicelog.getTablist());
				if (serviceVersionTokenService.isMinorModification(serviceId)) {
					ProcessFlowDTO.ProcessFlowRestrictions restrictions = serviceVersionTokenService.buildProcessFlowRestrictions(serviceId, dto);
					dto.setRestrictions(restrictions);
					ProcessFlowDTO baseline = serviceVersionTokenService.getBaselineProcessFlow(serviceId);
					dto.setHistoryProcessFlow(baseline);
					dto.setOldProcessFlow(baseline);
				}
				return ResponseEntity.ok().body(dto);
			}
			
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			}
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> saveDraftProcessFlow(JsonNode processFlowDTO, UserSessionDTO userSessionDetails) {
		JSONObject responseJson = new JSONObject();
		try {
			JSONObject processFlowJson= new JSONObject(processFlowDTO.toString());
			Integer serviceId=processFlowJson.getInt("serviceId");
			ServiceProcessFlow serviceProcessFlow=processFlowRepository.findByServiceIdAndUserId(serviceId,userSessionDetails.getUserID().longValue());
			ServiceLog serviceLog=serviceLogRepository.findByServiceId(serviceId);
			if(isNull(serviceLog)) {
				responseJson.put("errorCode", "400");
            	responseJson.put("errorMessage", "Invalid Service");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			if(isNull(serviceProcessFlow)) {
				serviceProcessFlow=new ServiceProcessFlow();
				serviceProcessFlow.setcDate(new Date());
			}
			if(serviceProcessFlow.getId()!=null) {
				serviceProcessFlow.setuDate(new Date());
			}
		
			serviceProcessFlow.setServiceId(serviceId);
			serviceProcessFlow.setBaseServiceId(serviceId/10000);
			serviceProcessFlow.setProcessFlowObject((JsonNode) processFlowDTO);
			serviceProcessFlow.setVersionNo(serviceId%10000);
			serviceProcessFlow.setMinorVersionNo(serviceLog.getMinorVersionNo() != null ? serviceLog.getMinorVersionNo() : 0);
			serviceProcessFlow.setUserId(userSessionDetails.getUserID().longValue());
			serviceProcessFlow.setStateId(userSessionDetails.getClcId());
			serviceProcessFlow.setDepartmentId(userSessionDetails.getEntityId());
			responseJson=saveProcessFlow(responseJson,serviceProcessFlow,serviceLog,"D");
			responseJson.remove("status");
			responseJson.remove("tabList");
			
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	public ProcessFlowSubmissionDetailsDTO getSubmissionNodeDetails(Integer serviceId,
			UserSessionDTO userSessionDetails) {

		ServiceProcessFlow serviceProcessFlow = processFlowRepository.findByServiceIdAndUserId(serviceId,
				userSessionDetails.getUserID().longValue());

		if (serviceProcessFlow == null) {
			serviceProcessFlow = processFlowRepository.findByServiceId(serviceId);
		}

		if (serviceProcessFlow == null || serviceProcessFlow.getProcessFlowJson() == null
				|| serviceProcessFlow.getProcessFlowJson().getNodes() == null) {
			return null;
		}

		ProcessFlowDTO processFlowDTO = serviceProcessFlow.getProcessFlowJson();

		for (ProcessFlowDTO.NodeDTO node : processFlowDTO.getNodes()) {

			if (node != null && "s_1".equals(node.getId()) && node.getData() != null) {

				ProcessFlowDTO.DataDTO data = node.getData();

				ProcessFlowSubmissionDetailsDTO response = new ProcessFlowSubmissionDetailsDTO();

				response.setMode(data.getMode() != null ? data.getMode() : java.util.Collections.emptyList());

				response.setKioskType(
						data.getKioskType() != null ? data.getKioskType() : java.util.Collections.emptyList());

				response.setIdentifiedUserSetIds(data.getIdentifiedUserSetIds() != null ? data.getIdentifiedUserSetIds()
						: java.util.Collections.emptyList());

				return response;
			}
		}

		return null;
	}
	
}
