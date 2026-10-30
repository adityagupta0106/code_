package com.serviceplus.metadata.mvel.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;
import org.mvel2.MVEL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceMasterData;
import com.serviceplus.metadata.mvel.dto.MVELParameterType;
import com.serviceplus.metadata.mvel.dto.MVELTriggerPoint;
import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO;
import com.serviceplus.metadata.mvel.dto.MvelInvalidationMSGDTO;
import com.serviceplus.metadata.mvel.dto.MvelListResponseDTO;
import com.serviceplus.metadata.mvel.entity.MvelFunction;
import com.serviceplus.metadata.mvel.repository.IMvelFunctionRepository;
import com.serviceplus.metadata.mvel.validator.MvelFunctionDefValidator;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceMasterDataRepository;
import com.serviceplus.metadata.utility.MvelSanitizer;

@Service
public class MvelFunctionService {
	@Autowired
    private  IServiceDefinitionRepository serviceDefinitionRepository;
	
	@Autowired
	private MvelFunctionDefValidator mvelFunctionValidation;
	
	@Autowired 
	private IMvelFunctionRepository mvelFunctionRepository;
	
	@Autowired
	private IServiceMasterDataRepository masterDataRepository;
	
	@Autowired
	private  IServiceLogRepository serviceLogRepository;
	
	@Autowired
	private com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService;
	
	public void compileExpression(String fnBody) {
		try {
			Serializable compiled = MVEL.compileExpression(fnBody);
			if (compiled == null) {
				throw new IllegalArgumentException("Expression could not be compiled.");
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("Compilation failed: " + ex.getMessage(), ex);
		}
	}
	
	
	public ResponseEntity<?> getMvelFunParameterTypes() {
		JSONObject responseJson = new JSONObject();
		try {
			ServiceMasterData masterData = masterDataRepository.findByName("MVEL Parameter Type").orElseThrow(() -> new RuntimeException("MVEL Parameter Type not found"));
			ObjectMapper mapper = new ObjectMapper();
			List<MVELParameterType> mvelParameterTypes = mapper.readValue(masterData.getData(),
					new TypeReference<List<MVELParameterType>>() {
			});
			return ResponseEntity.ok().body(mvelParameterTypes);
			
		}catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	public ResponseEntity<?> getFunctionParam(UserSessionDTO userSessionDetails, Long id) {
		JSONObject responseJson = new JSONObject();
		try {
			MvelFunction mvelFunction = mvelFunctionRepository.findById(id);
			if (Objects.isNull(mvelFunction)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Invalid Data");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return ResponseEntity.ok().body(mvelFunction.getMvelFunctionJson().getParameters());
			
		}catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> addMvelFunction(UserSessionDTO userSessionDetails,MvelFunctionDTO requestData) {
		JSONObject responseJson = new JSONObject();
		MvelFunction mvelFunction;
		try {
			MvelInvalidationMSGDTO invalidationMsg = mvelFunctionValidation.validate(requestData);
			try {
			    MvelSanitizer.validate(requestData.getFunctionBody());
			} catch (Exception ex) {
			    responseJson.put("errorCode", "400");
			    responseJson.put("errorMessage", ex.getMessage());
			    return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			if (!Objects.isNull(invalidationMsg)) {
				ObjectMapper mapper = new ObjectMapper();
				responseJson.put("errorCode", "404");
				responseJson.put("errorMessage", "Invalid data");
				responseJson.put("invalidationMsg", new JSONObject(mapper.writeValueAsString(invalidationMsg)));
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			ServiceDefinition serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(
					requestData.getServiceId().intValue(), userSessionDetails.getUserID().longValue());
			
			ServiceLog serviceLog=serviceLogRepository.findByServiceId(requestData.getServiceId().intValue());
			if(Objects.isNull(serviceLog)) {
				responseJson.put("errorCode", "400");
            	responseJson.put("errorMessage", "Invalid Service");
            	return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			String tablist = serviceLog.getTablist();
			if (!tablist.contains("9")) {
				serviceLog.setTablist(serviceLog.getTablist() + ",9");
				ServiceLog savedLogEntity = serviceLogRepository.save(serviceLog);
				tablist = savedLogEntity.getTablist();
			}
			if (Objects.isNull(serviceDefinition)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Service Not Defined.");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			Integer minorVer = (serviceLog.getMinorVersionNo() != null) ? serviceLog.getMinorVersionNo() : 0;
			MvelFunctionDTO oldMvelDTO = null;

			if (Objects.isNull(requestData.getId())) {
				mvelFunction = new MvelFunction();
				mvelFunction.setServiceId(requestData.getServiceId().intValue());
				mvelFunction.setName(requestData.getFunctionName());
				mvelFunction.setCreatedBy(userSessionDetails.getUserID().longValue());
				mvelFunction.setMvelFunctionJson(requestData);
				mvelFunction.setVersionNo(serviceDefinition.getVersionNo());
				mvelFunction.setMinorVersionNo(minorVer);

			} else {
				mvelFunction = mvelFunctionRepository.findByIdAndServiceIdAndCreatedBy(requestData.getId(),
						requestData.getServiceId().intValue(), userSessionDetails.getUserID().longValue());
				if (Objects.isNull(mvelFunction)) {
					responseJson.put("errorCode", "400");
					responseJson.put("errorMessage", "Invalid Id");
					return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
				}
				oldMvelDTO = mvelFunction.getMvelFunctionJson();

				mvelFunction.setName(requestData.getFunctionName());
				mvelFunction.setMvelFunctionJson(requestData);
				mvelFunction.setVersionNo(serviceDefinition.getVersionNo());
				mvelFunction.setMinorVersionNo(minorVer);
				mvelFunction.setModifiedBy(userSessionDetails.getUserID().longValue());
			}
			mvelFunctionRepository.save(mvelFunction);

			boolean isMinorVersion = serviceVersionTokenService.isMinorModification(requestData.getServiceId().intValue());
			if (isMinorVersion) {
				MvelFunctionDTO baseline = serviceVersionTokenService.getBaselineMvelFunction(requestData.getServiceId().intValue(), mvelFunction.getId());
				MvelFunctionDTO effectiveOld = (baseline != null) ? baseline : oldMvelDTO;
				serviceVersionTokenService.recordMvelChangeLog(
						requestData.getServiceId().intValue(),
						oldMvelDTO == null ? "CREATE" : "UPDATE",
						requestData.getFunctionName(),
						effectiveOld,
						requestData,
						userSessionDetails
				);
			}

			responseJson.put("message", "Data saved successfully");
			responseJson.put("tabList", tablist);
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public ResponseEntity<?> getAllListOfMavelFunction(UserSessionDTO userSessionDetails, Integer serviceId){
		JSONObject responseJson = new JSONObject();
		try {
			ServiceDefinition serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
			 List<MvelFunction> mvelFunctionList = mvelFunctionRepository.findByServiceId(serviceId);
			if(Objects.isNull(serviceDefinition)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Service Not Defined.");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			List<MvelListResponseDTO> responseDTOs =  new ArrayList<MvelListResponseDTO>();
			for (MvelFunction mvelFunction : mvelFunctionList) {
				MvelListResponseDTO mvelListResponseDTO = new MvelListResponseDTO();
				mvelListResponseDTO.setId(mvelFunction.getId());
				mvelListResponseDTO.setFunctionName(mvelFunction.getName());
				mvelListResponseDTO.setFunctionDescription(mvelFunction.getMvelFunctionJson().getFunctionDescription());
				responseDTOs.add(mvelListResponseDTO);
			}
			return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	public ResponseEntity<?> deleteMvelFunction(UserSessionDTO userSessionDetails, Integer serviceId, Long id) {
		JSONObject responseJson = new JSONObject();
		try {
			ServiceDefinition serviceDefinition = serviceDefinitionRepository.findByServiceIdAndUserId(serviceId, userSessionDetails.getUserID().longValue());
			if(Objects.isNull(serviceDefinition)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Service Not Defined.");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			
			if (serviceVersionTokenService.isMinorModification(serviceId) && serviceVersionTokenService.isMvelFunctionInHistory(serviceId, id)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Deleting existing MVEL functions is not allowed in minor version modification");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			MvelFunction mvelFunction = mvelFunctionRepository.findByIdAndServiceIdAndCreatedBy(id, serviceId,
					userSessionDetails.getUserID().longValue());
			if (Objects.isNull(mvelFunction)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Invalid Data");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			MvelFunctionDTO deletedDTO = mvelFunction.getMvelFunctionJson();
			String funcName = mvelFunction.getName();
			mvelFunctionRepository.delete(mvelFunction);

			serviceVersionTokenService.recordMvelChangeLog(
					serviceId,
					"DELETE",
					funcName,
					deletedDTO,
					null,
					userSessionDetails
			);

			responseJson.put("message", "Data deleted successfully");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	public ResponseEntity<?> getMavelFunction(UserSessionDTO userSessionDetails,Integer serviceId,Long id) {
		JSONObject responseJson = new JSONObject();
		try {
			MvelFunction mvelFunction = mvelFunctionRepository.findByIdAndServiceIdAndCreatedBy(id, serviceId,
					userSessionDetails.getUserID().longValue());
			if (Objects.isNull(mvelFunction)) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Record not found");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			MvelFunctionDTO mvelFunctionDTO =  new MvelFunctionDTO();
			BeanUtils.copyProperties(mvelFunction.getMvelFunctionJson(), mvelFunctionDTO);
			mvelFunctionDTO.setId(mvelFunction.getId());
			if (serviceVersionTokenService.isMinorModification(serviceId)) {
				MvelFunctionDTO baseline = serviceVersionTokenService.getBaselineMvelFunction(serviceId, id);
				mvelFunctionDTO.setHistoryFunction(baseline);
				mvelFunctionDTO.setOldFunction(baseline);
			}
			return new ResponseEntity<>(mvelFunctionDTO, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getMvelTriggerPoints() {
			JSONObject responseJson = new JSONObject();
			try {
				ServiceMasterData masterData = masterDataRepository.findByName("MVEL Trigger Point").orElseThrow(() -> new RuntimeException("MVEL Trigger Point not found"));
				ObjectMapper mapper = new ObjectMapper();
				List<MVELTriggerPoint> mvelTriggerPoints = mapper.readValue(masterData.getData(),
						new TypeReference<List<MVELTriggerPoint>>() {
				});
				return ResponseEntity.ok().body(mvelTriggerPoints);
				
			}catch (Exception e) {
				e.printStackTrace();
				responseJson.put("errorCode", "500");
				responseJson.put("errorMessage", "Internal Server Error");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}
	
}
