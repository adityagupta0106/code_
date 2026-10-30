package com.serviceplus.metadata.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.dto.ActivityMapDTO;
import com.serviceplus.metadata.dto.MvelDetailsDTO;
import com.serviceplus.metadata.dto.OfficeDetailsDTO;
import com.serviceplus.metadata.dto.OfficeDetailsDTO.OfficeUnitData;
import com.serviceplus.metadata.dto.ResolveFormDTO;
import com.serviceplus.metadata.dto.ServiceJSONDTO;
import com.serviceplus.metadata.dto.ServiceProcessFlowDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.dto.WorkFlowDataDTO;
import com.serviceplus.metadata.entity.ServiceMetadata;
import com.serviceplus.metadata.repository.IServiceMetadataRepository;

@Service
public class ApplyService {
	
	private final IServiceMetadataRepository metadataRepository;
		
	public ApplyService(IServiceMetadataRepository metadataRepository) {
		super();
		this.metadataRepository = metadataRepository;
	}

	public ResponseEntity<?> resolveForm(Integer baseServiceId,Integer serviceId ,String taskId,
			UserSessionDTO userSessionDetails) {
		JSONObject responseJson=new JSONObject();
		try {
		    ResolveFormDTO dto = new ResolveFormDTO();
		    ServiceMetadata serviceMetadata = null;
		    ServiceJSONDTO metadataJson = null;

		    if(taskId == null || taskId.isBlank()) {
		        serviceMetadata = metadataRepository.findByBaseServiceIdAndTenantId(baseServiceId,userSessionDetails.getTenantId());//remove

		        metadataJson = serviceMetadata.getMetadataJson();
		        dto.setServiceName(metadataJson.getServiceName());
		        dto.setServiceId(metadataJson.getServiceId());
		        dto.setTaskType("A");
		        dto.setTaskId(metadataJson.getApplSubmissionTaskId());
		        dto.setFormId(metadataJson.getApplFormId());
		    } else {
		        serviceMetadata = metadataRepository.findByServiceIdAndTenantId(serviceId,userSessionDetails.getTenantId());
		        metadataJson = serviceMetadata.getMetadataJson();
		        dto.setServiceName(metadataJson.getServiceName());
		        dto.setServiceId(metadataJson.getServiceId());
		        dto.setTaskType("O");
		        dto.setTaskId(taskId);
		        dto.setFormId(metadataJson.getTaskFormMapping().get(taskId));
		    }

		    List<ActivityMapDTO> activities = metadataJson.getActivityMap().stream().filter(map -> map.getTaskId().equals(dto.getTaskId())).collect(Collectors.toList());
		    List<OfficeDetailsDTO> locations = metadataJson.getOfficeDetails().stream().filter(map -> map.getTaskId().equals(dto.getTaskId())).collect(Collectors.toList());
		    dto.setActivityMap(activities.isEmpty() ? new ActivityMapDTO() : activities.get(0));
		    dto.setLocations(locations.isEmpty()? new ArrayList<OfficeUnitData>(): locations.get(0).getAllowedOffices());
		    return ResponseEntity.ok(dto);

		}catch(Exception ex) {
			responseJson.clear();
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
	        responseJson.put("errorMessage", "Internal Server Error");
	        return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}

	public ResponseEntity<?> processFlow(Integer serviceId, String taskId, UserSessionDTO userSessionDetails) {
	    JSONObject responseJson = new JSONObject();
	    try {

	        Optional<ServiceMetadata> optional = metadataRepository.findById(serviceId);
	        if (optional.isEmpty()) {
	            responseJson.put("errorCode", "400");
	            responseJson.put("errorMessage", "Invalid service");
	            return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
	        }

	        ServiceMetadata serviceMetadata = optional.get();
	        ServiceProcessFlowDTO processFlowMap = serviceMetadata.getMetadataJson().getProcessFlowMap();
	        if (taskId != null && !taskId.isEmpty()) {
	            List<ServiceProcessFlowDTO.Data> filtered =processFlowMap.getData().stream().filter(d -> d.getNode().getId().equals(taskId)).collect(Collectors.toList());
	            processFlowMap.setData(filtered);
	            
	        }
	        return ResponseEntity.ok(processFlowMap);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        responseJson.put("errorCode", "500");
	        responseJson.put("errorMessage", "Internal Server Error");
	        return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	public ResponseEntity<?> applyAllowAction(Integer serviceId,String taskId,UserSessionDTO userSessionDetails) {

	    JSONObject responseJson = new JSONObject();

	    try {
	    	ServiceMetadata serviceMetadata = metadataRepository.findByServiceIdAndTenantId(serviceId,userSessionDetails.getTenantId());
	        if (Objects.isNull(serviceMetadata)) {
	            responseJson.put("errorCode", "400");
	            responseJson.put("errorMessage", "Invalid service");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson.toString());
	        }
	        List<WorkFlowDataDTO> workflowDetails =serviceMetadata.getMetadataJson().getWorkFlowDetails();

	        Optional<WorkFlowDataDTO> matchedWorkflow =workflowDetails.stream().filter(wf -> taskId.equals(wf.getTaskId())).findFirst();

	        if (matchedWorkflow.isEmpty()) {
	            responseJson.put("errorCode", "404");
	            responseJson.put("errorMessage", "Invalid taskId");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseJson.toString());
	        }

	        return ResponseEntity.ok(matchedWorkflow.get().getAllowedAction());

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        responseJson.put("errorCode", "500");
	        responseJson.put("errorMessage", "Internal Server Error");
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(responseJson.toString());
	    }
	}
	
	public ResponseEntity<?> mvelDetails(Integer serviceId, UserSessionDTO userSessionDetails) {
	    JSONObject responseJson = new JSONObject();
	    try {

	        Optional<ServiceMetadata> optional = metadataRepository.findById(serviceId);
	        if (optional.isEmpty()) {
	            responseJson.put("errorCode", "400");
	            responseJson.put("errorMessage", "Invalid service");
	            return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
	        }

	        ServiceMetadata serviceMetadata = optional.get();
	        ServiceJSONDTO serviceJsonDTO=serviceMetadata.getMetadataJson();
	        List<MvelDetailsDTO> mvelDetailsDTO = serviceJsonDTO.getMvelDetailsDTO();
	        return ResponseEntity.ok(mvelDetailsDTO);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        responseJson.put("errorCode", "500");
	        responseJson.put("errorMessage", "Internal Server Error");
	        return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	public ResponseEntity<?> getUsedAttributeSet(Integer serviceId, String module, UserSessionDTO userSessionDetails) {
	    JSONObject responseJson = new JSONObject();
	    Set<String> userAttrSet=new HashSet<String>();
	    try {

	        Optional<ServiceMetadata> optional = metadataRepository.findById(serviceId);
	        if (optional.isEmpty()) {
	            responseJson.put("errorCode", "400");
	            responseJson.put("errorMessage", "Invalid service");
	            return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
	        }

	        ServiceMetadata serviceMetadata = optional.get();
	        ServiceJSONDTO serviceJsonDTO=serviceMetadata.getMetadataJson();
			if ("mvel".equals(module)) {
				userAttrSet = serviceJsonDTO.getMvelUsedAttrList();
			} else if ("document".equals(module)) {
				userAttrSet = serviceJsonDTO.getDocumentUsedAttrList();
			} else if ("notification".equals(module)) {
				userAttrSet = serviceJsonDTO.getNotificationUsedAttrList();
			}else if("inboxSentboxFilter".equals(module)) {
				userAttrSet = serviceJsonDTO.getInboxSentboxFiltersUsedAttrList();
			}else if("correlationSet".equals(module)) {
				userAttrSet = serviceJsonDTO.getCorrelationAttributeIds();
			}
	        return ResponseEntity.ok(userAttrSet);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        responseJson.put("errorCode", "500");
	        responseJson.put("errorMessage", "Internal Server Error");
	        return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	public ResponseEntity<?> serviceMetaData(Integer serviceId, UserSessionDTO userSessionDetails) {
		JSONObject responseJson = new JSONObject();
		try {
			ServiceMetadata metadata = metadataRepository.findByServiceIdAndTenantId(serviceId,
					userSessionDetails.getTenantId());
			if (metadata == null) {
				responseJson.put("errorCode", "404");
				responseJson.put("errorMessage", "Service Metadata Not Found");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.NOT_FOUND);
			}
			return ResponseEntity.ok(metadata.getMetadataJson());
		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
