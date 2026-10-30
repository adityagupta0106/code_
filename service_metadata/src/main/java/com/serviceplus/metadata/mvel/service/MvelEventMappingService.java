package com.serviceplus.metadata.mvel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.entity.ServiceTemplate;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO.FuncParamDTO;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO.TriggerFunctionDTO;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO.TriggerPointDTO;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingInvalidationMSGDTO;
import com.serviceplus.metadata.mvel.dto.MvelTriggerPointResponseDTO;
import com.serviceplus.metadata.mvel.entity.MvelEventMapping;
import com.serviceplus.metadata.mvel.entity.MvelFunction;
import com.serviceplus.metadata.mvel.repository.IMvelEventMappingRepository;
import com.serviceplus.metadata.mvel.repository.IMvelFunctionRepository;
import com.serviceplus.metadata.mvel.validator.MvelEventMappingValidator;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceTemplateRepository;

@Service
public class MvelEventMappingService {
	@Autowired
	private IServiceDefinitionRepository serviceDefinitionRepository;
	@Autowired
	private MvelEventMappingValidator mvelEventMappingValidator;
	@Autowired
	private IMvelEventMappingRepository eventMappingRepository;
	@Autowired
	private IServiceTemplateRepository serviceTemplateRepository;
	@Autowired
	private IMvelFunctionRepository mvelFunctionRepository;
	@Autowired
	private IProcessFlowRepository processFlowRepository;

	public ResponseEntity<?> save(Long userID, MvelEventMappingDTO mvelEventMappingDTO) {
		JSONObject responseJson = new JSONObject();
		try {
			ServiceDefinition serviceDefinition = serviceDefinitionRepository
					.findByServiceIdAndUserId(mvelEventMappingDTO.getServiceId().intValue(), userID.longValue());
			if (Objects.isNull(serviceDefinition)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Service Not Defined");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			mvelEventMappingDTO.setServiceId(serviceDefinition.getServiceId().longValue());

			MvelEventMappingInvalidationMSGDTO validate = mvelEventMappingValidator.validate(mvelEventMappingDTO);

			if (!Objects.isNull(validate)) {
				ObjectMapper mapper = new ObjectMapper();
				responseJson.put("errorCode", "404");
				responseJson.put("errorMessage", "Invalid data");
				responseJson.put("invalidationMessage", new JSONObject(mapper.writeValueAsString(validate)));
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			List<MvelEventMapping> list = eventMappingRepository.findByServiceId(serviceDefinition.getServiceId());
			int trigSeq = 0;
			for(MvelEventMapping eventMapping : list) {
				Integer funcId = mvelEventMappingDTO.getTriggerPoint().getTriggerFunction().getFuncId();

				Integer funcIdDB = eventMapping.getMvelEventMappingDTO().getTriggerPoint().getTriggerFunction().getFuncId();
				
				if(funcId.equals(funcIdDB)) {
					Integer trigSeqDB = eventMapping.getMvelEventMappingDTO().getTriggerPoint().getTriggerFunction().getTrigSeq();
					if(trigSeqDB >  trigSeq) {
						trigSeq = trigSeqDB;
					}
				}
			}
			
			mvelEventMappingDTO.getTriggerPoint().getTriggerFunction().setTrigSeq(++trigSeq);
			MvelEventMapping eventMapping = new MvelEventMapping();
			eventMapping.setCreatedBy(userID.longValue());
			eventMapping.setServiceId(serviceDefinition.getServiceId());
			eventMapping.setVersionNo(serviceDefinition.getVersionNo());
			eventMapping.setMvelEventMappingDTO(mvelEventMappingDTO);

			eventMappingRepository.save(eventMapping);

			responseJson.put("message", "Data saved successfully");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.CREATED);

		} catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> delete(Long userID, Integer serviceId, Long eventId) {
		JSONObject responseJson = new JSONObject();
		try {
			ServiceDefinition serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId,
					userID.longValue());
			if (Objects.isNull(serviceDefinition)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Service Not Defined");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			MvelEventMapping mvelEventMapping = eventMappingRepository.findByEventIdAndServiceId(eventId, serviceId);
			if (Objects.isNull(mvelEventMapping)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Invalid Data");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			eventMappingRepository.delete(mvelEventMapping);
			responseJson.put("message", "Data deleted successfully");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getAll(Long userID, Integer serviceId) {
		JSONObject responseJson = new JSONObject();
		List<MvelTriggerPointResponseDTO> responseList = new ArrayList<MvelTriggerPointResponseDTO>();
		
		try {
			ServiceDefinition serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId,userID.longValue());
			if (Objects.isNull(serviceDefinition)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Service Not Defined");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			
			List<MvelEventMapping> mvelEventMappingsList = eventMappingRepository.findByServiceId(serviceId);
			
			for(MvelEventMapping eventMapping : mvelEventMappingsList) {
				MvelTriggerPointResponseDTO dto = new MvelTriggerPointResponseDTO();
				String event = "";
				String triggerPointDetails = "";
				String funName = "";
				Integer trigSeq;
				List<Map<String,String>> parameters = new ArrayList<Map<String,String>>();
				
				MvelEventMappingDTO mvelEventMappingDTO = eventMapping.getMvelEventMappingDTO();
				TriggerPointDTO triggerPoint = mvelEventMappingDTO.getTriggerPoint();
				
				event = mvelEventMappingDTO.getEvent();
				String templId = triggerPoint.getTemplId();
				String taskId = triggerPoint.getTaskId();
				
				if(templId != null && !templId.isBlank()) {
					ServiceTemplate serviceTemplate = serviceTemplateRepository.findByIdAndServiceIdAndUserId(Integer.parseInt(templId),serviceId,userID.longValue());
					triggerPointDetails = serviceTemplate.getTemplateName();
				}else if(taskId != null && !taskId.isBlank()) {
					ServiceProcessFlow processFlow = processFlowRepository.findByServiceIdAndUserId(serviceId, userID.longValue());
					triggerPointDetails = processFlow.getProcessFlowJson().getNodes().stream().filter(node->node.getId().equals(taskId)).collect(Collectors.toList()).get(0).getData().getName();;
				}
				
				TriggerFunctionDTO triggerFunction = triggerPoint.getTriggerFunction();
				if(!Objects.isNull(triggerFunction)) {
					Long funcId = triggerFunction.getFuncId().longValue();
					MvelFunction mvelFunction = mvelFunctionRepository.findById(funcId);
					if(!Objects.isNull(mvelFunction)) {
						funName = mvelFunction.getName();
					}
					
					List<FuncParamDTO> funcParams = triggerFunction.getFuncParams();
					
					for(FuncParamDTO paramDTO:funcParams) {
						Map<String, String> obj = new HashMap<>();
						
						String paramAttrId = paramDTO.getAttrId();
						String paramTaskId = paramDTO.getTaskId();
						String paramConstant = paramDTO.getConstant();
						String paramDefParamTypeId = paramDTO.getDefParamTypeId();
						String paramType = "";
						
						String paramValue = "";
						
						if(!paramAttrId.isBlank()) {
							paramType = "attrId";
							paramValue = paramAttrId;
						}else if(!paramTaskId.isBlank()) {
							paramType = "taskId";
							paramValue = paramTaskId;
						}else if(!paramConstant.isBlank()) {
							paramType = "constant";
							paramValue = paramConstant;
						}else if(!paramDefParamTypeId.isBlank()) {
							paramType = "defParamTypeId";
							paramValue = paramDefParamTypeId;
						}
						
						obj.put(paramType, paramValue);
						
						parameters.add(obj);
						trigSeq = triggerFunction.getTrigSeq();
						dto.getParameters().setTrigSeq(trigSeq);
					}
				}
				
				
				dto.setEventId(eventMapping.getEventId());
				dto.setTriggerPoint(event);
				dto.setTriggerPointDetails(triggerPointDetails);
				dto.setFunctionName(funName);
				dto.getParameters().setParameters(parameters);
				
				
				responseList.add(dto);
				
			}
			
			return new ResponseEntity<>(responseList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
