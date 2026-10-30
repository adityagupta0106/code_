package com.serviceplus.metadata.controller;


import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.ExternalClientFormAttributesMappingDTO;
import com.serviceplus.metadata.dto.ServiceActivationDTO;
import com.serviceplus.metadata.dto.ServiceChargeDTO;
import com.serviceplus.metadata.dto.ServiceChargeResDTO;
import com.serviceplus.metadata.dto.ServiceDefinitionDTO;
import com.serviceplus.metadata.dto.ServiceFormDefinitionDTO;
import com.serviceplus.metadata.dto.ServiceOutputFormatDTO;
import com.serviceplus.metadata.dto.ServiceTemplateDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.dto.XsdAttributeRequest;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.SduAdoptionService;
import com.serviceplus.metadata.service.ServiceMasterDataService;
import com.serviceplus.metadata.service.ServiceMetadataService;
import com.serviceplus.metadata.service.ServiceRequestService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import com.serviceplus.metadata.dto.ServiceTrackingConfigDTO;

@RestController
public class ServiceMetadataController {

	@Autowired
    private ServiceMetadataService serviceMetadataService;
	
	@Autowired
    private ServiceMasterDataService masterDataService;
	
	@Autowired
	private ServiceRequestService serviceRequestService;
	
	@Autowired
	private SduAdoptionService sduAdoptionSerivce;
	
	@Value("${aesServiceKey}")
	private String aesServiceKey;
	
	@Value("${service.cookie.https}")
	private String cookieHttpsOnly;
	
	@Value("${service.cookie.sameSite}")
	private String cookieSameSite;

	private static final Logger serviceMetadataLogger = LogManager.getLogger("serviceMetadataLogger");

	
	@GetMapping("/task-allowed-action")
	public ResponseEntity<?> getAllAllowActionOfTask(@RequestParam Integer serviceId, @RequestParam String taskId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getAllAllowActionOfTask(serviceId,taskId, userSessionDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				serviceMetadataLogger.error("Error : Get all allow action of task :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	@GetMapping("/version")
	public ResponseEntity<?> getAllVersionOfService(@RequestParam Integer serviceId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getAllVersionOfService(serviceId, userSessionDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				serviceMetadataLogger.error("Error : Get all version of service :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("/tasks")
	public ResponseEntity<?> getAllTaskOfAttachedForms(@RequestParam Integer serviceId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getAllTaskOfAttachedForms(serviceId, userSessionDetails);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				serviceMetadataLogger.error("Error : Get all get process flow tasks of attached forms :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("task/form-attributes")
	public ResponseEntity<?> getProcessFlowFormsWithMailAttr(@RequestParam Integer serviceId, @RequestParam String taskId, @RequestParam String attrType, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			
			return serviceMetadataService.getProcessFlowFormsAttr(serviceId, taskId, attrType, userSessionDetails);
			
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				serviceMetadataLogger.error("Error : Get all get process flow forms with attributes :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@GetMapping("tasks/all-document")
	public ResponseEntity<?> getAllDouments(@RequestParam Integer serviceId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getAllDouments(serviceId, userSessionDetails);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				serviceMetadataLogger.error("Error : Get all attached documents :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	
    @PostMapping("/saveServiceDefinition")
    public ResponseEntity<?> saveServiceDefinition(@Valid @RequestBody ServiceDefinitionDTO serviceDefinitionDTO,HttpServletRequest request) {
    	
    	JSONObject responseJson= new JSONObject();
    	try {
    	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    	userSessionDetails=getLocalSessionDetails(userSessionDetails);
    	
		if (userSessionDetails == null) {
        	responseJson.put("errorCode", "400");
        	responseJson.put("errorMessage", "Bad Request");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}
		responseJson = serviceMetadataService.saveServiceDefinition(serviceDefinitionDTO,userSessionDetails);
		if(!responseJson.getBoolean("status")) {
			responseJson.remove("status");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}else {
			responseJson.remove("status");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.OK);
		}
    	}catch(Exception ex) {
    		responseJson.put("errorCode", "500");
        	responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @GetMapping("/serviceDefinition")
    public ResponseEntity<?> getServiceDefinitionDetails(@RequestParam Integer serviceId,HttpServletRequest request,HttpServletResponse response) {
    	
    	JSONObject responseJson= new JSONObject();
    	
    	try {
    	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    	userSessionDetails=getLocalSessionDetails(userSessionDetails);
		if (userSessionDetails == null) {
        	responseJson.put("errorCode", "400");
        	responseJson.put("errorMessage", "Bad Request");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
		}
		String serviceToken = updateServiceRequestToken(serviceId);
		

		response.addCookie(CommonUtil.createCookie("serviceToken", serviceToken, true, 
				cookieHttpsOnly.isBlank() ? true : Boolean.parseBoolean(cookieHttpsOnly),
			    cookieSameSite.isBlank() ? "strict" : cookieSameSite
					,"/"));
		return serviceMetadataService.getServiceDefinitionDetails(serviceId,userSessionDetails);
		
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		responseJson.put("errorCode", "500");
        	responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    private String updateServiceRequestToken(Integer serviceId) {
	   	String checksum = serviceRequestService.generateCheckSum(serviceId);	
	   	checksum=CommonUtil.AESEncrypt(checksum, aesServiceKey);
	    return checksum;
	}
    
    @GetMapping("/taskList")
	public ResponseEntity<?> getTaskList(@RequestParam Integer serviceId, @RequestParam(required=false) String taskId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getTaskList(serviceId,taskId,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/serviceList")
	public ResponseEntity<?> getServiceList(@RequestParam(required=false) Integer status, HttpServletRequest request,HttpServletResponse response) {
		JSONObject responseJson = new JSONObject();

		try {
			Cookie serviceCookie=new Cookie("serviceToken",""); 
			serviceCookie.setHttpOnly(true);		
			serviceCookie.setPath("/");
			serviceCookie.setMaxAge(0);
			response.addCookie(serviceCookie);
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getServiceList(status,userSessionDetails);

		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

    @GetMapping("/categories")
    public ResponseEntity<?> getServiceCategory() {
        return ResponseEntity.ok(masterDataService.getServiceCategory());
    }

    @GetMapping("/types")
    public ResponseEntity<?> getServiceType() {
        return ResponseEntity.ok(masterDataService.getServiceType());
    }

    @GetMapping("/goals")
    public ResponseEntity<?> getGoals() {
        return ResponseEntity.ok(masterDataService.getGoals());
    }
    
    @GetMapping("/serviceTemplateList")
	public ResponseEntity<?> getServiceTemplateList(@RequestParam Integer serviceId,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getServiceTemplateList(serviceId,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
    @PostMapping("/saveServiceTemplate")
	public ResponseEntity<?> saveServiceTemplate(@Valid @RequestBody ServiceTemplateDTO serviceTemplate,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceTemplate.getServiceId(), responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.saveServiceTemplate(serviceTemplate,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
    @GetMapping("/serviceTemplate")
	public ResponseEntity<?> getServiceTemplate(@RequestParam Integer serviceId,@RequestParam Integer templateId,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getServiceTemplate(serviceId,templateId,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
//    @PostMapping("/deleteTemplate")
//   	public ResponseEntity<?> deleteTemplate(@RequestParam Integer serviceId,@RequestParam Integer templateId,HttpServletRequest request) {
//   		JSONObject responseJson = new JSONObject();
//
//   		try {
//   			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
//   			userSessionDetails=getLocalSessionDetails(userSessionDetails);
//   	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
//   	        if (tokenValidationResult != null) {
//   	            return tokenValidationResult;
//   	        }
//   			if (userSessionDetails == null) {
//   				responseJson.put("errorCode", "400");
//   				responseJson.put("errorMessage", "Bad Request");
//   				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
//   			}
//   			return serviceMetadataService.deleteTemplate(serviceId,templateId,userSessionDetails);
//
//   		} catch (Exception ex) {
//   			responseJson.put("errorCode", "500");
//   			responseJson.put("errorMessage", "Internal Server Error");
//   			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
//   		}
//
//   	}
    @GetMapping("/enclosure")
    public ResponseEntity<?> getEnclosures() {
        return ResponseEntity.ok(masterDataService.getEnclosures());
    }

    @GetMapping("/documentRecommended")
    public ResponseEntity<?> getDocumentRecommended() {
        return ResponseEntity.ok(masterDataService.getDocumentRecommended());
    }
    
    @GetMapping("/submissionMode")
    public ResponseEntity<?> getSubmissionMode() {
        return ResponseEntity.ok(masterDataService.getSubmissionMode());
    }
    @GetMapping("/chargeType")
    public ResponseEntity<?> getChargeType(HttpServletRequest request) {
    	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    	userSessionDetails=getLocalSessionDetails(userSessionDetails);
    	return ResponseEntity.ok(masterDataService.getChargeType(userSessionDetails));
    }
    
    @GetMapping("/paymentMode")
    public ResponseEntity<?> getPaymentMode(HttpServletRequest request) {
    	UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    	userSessionDetails=getLocalSessionDetails(userSessionDetails);
    	return ResponseEntity.ok(masterDataService.getPaymentMode(userSessionDetails));
    }
    
    @GetMapping("/serviceFormAttributeList")
    public ResponseEntity<?> getServiceFormAttributeList(@RequestParam Integer serviceId,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getServiceFormAttributeList(serviceId,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    @GetMapping("/service-parameter-list")
    public ResponseEntity<?> serviceParameterList(@RequestParam Integer serviceId,@RequestParam Long moduleId,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.serviceParameterList(serviceId,moduleId,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    @GetMapping("/serviceOutputFormatList")
   	public ResponseEntity<?> getServiceOutputFormatList(@RequestParam Integer serviceId,HttpServletRequest request) {
   		JSONObject responseJson = new JSONObject();

   		try {
   			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
   			userSessionDetails=getLocalSessionDetails(userSessionDetails);
   	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
   	        if (tokenValidationResult != null) {
   	            return tokenValidationResult;
   	        }
   			if (userSessionDetails == null) {
   				responseJson.put("errorCode", "400");
   				responseJson.put("errorMessage", "Bad Request");
   				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
   			}
   			return serviceMetadataService.getServiceOutputFormatList(serviceId,userSessionDetails);

   		} catch (Exception ex) {
   			responseJson.put("errorCode", "500");
   			responseJson.put("errorMessage", "Internal Server Error");
   			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
   		}

   	}
    @PostMapping("/saveServiceOutputFormat")
	public ResponseEntity<?> saveServiceOutputFormat(@Valid @RequestBody ServiceOutputFormatDTO serviceOutputFormatDTO,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceOutputFormatDTO.getServiceId(), responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.saveServiceOutputFormat(serviceOutputFormatDTO,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
    @GetMapping("/serviceOutputFormat")
	public ResponseEntity<?> getServiceOutputFormat(@RequestParam Integer serviceId,@RequestParam Long outputFormatId,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getServiceOutputFormat(serviceId,outputFormatId,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
    @PostMapping("/deleteOutputFormat")
   	public ResponseEntity<?> deleteOutputFormat(@RequestParam Integer serviceId,@RequestParam Long outputFormatId,HttpServletRequest request) {
   		JSONObject responseJson = new JSONObject();

   		try {
   			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
   			userSessionDetails=getLocalSessionDetails(userSessionDetails);
   	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
   	        if (tokenValidationResult != null) {
   	            return tokenValidationResult;
   	        }
   			if (userSessionDetails == null) {
   				responseJson.put("errorCode", "400");
   				responseJson.put("errorMessage", "Bad Request");
   				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
   			}
   			return serviceMetadataService.deleteOutputFormat(serviceId,outputFormatId,userSessionDetails);

   		} catch (Exception ex) {
   			responseJson.put("errorCode", "500");
   			responseJson.put("errorMessage", "Internal Server Error");
   			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
   		}

   	}
    
    @GetMapping("/apply/serviceList")
   	public ResponseEntity<?> getEligibleServiceList(HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
//			userSessionDetails=getLocalSessionDetails(userSessionDetails);
//			if (userSessionDetails == null) {
//				responseJson.put("errorCode", "400");
//				responseJson.put("errorMessage", "Bad Request");
//				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
//			}
			return serviceMetadataService.getEligibleServiceList(userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    
   	

   	}
    
    @GetMapping("/serviceAbbreviation")
   	public ResponseEntity<?> getServiceAbbreviation(@RequestParam Integer serviceId,HttpServletRequest request) {
    	JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getServiceAbbreviation(serviceId, userSessionDetails);
		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    
   	}
    @PostMapping("/getXSDAttributeList")
    public ResponseEntity<?> getXSDAttributeList(@RequestBody XsdAttributeRequest request,
                                                 HttpServletRequest httpRequest) {

        JSONObject responseJson = new JSONObject();

        try {
            return serviceMetadataService.getXSDAttributeList(request.getXsd());
        } catch (Exception ex) {
            responseJson.put("errorCode", "500");
            responseJson.put("errorMessage", "Internal Server Error");
            return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/charge-task")
	public ResponseEntity<?> getChargeTask(@RequestParam Integer serviceId,
			@RequestParam(required = false) Long chargeId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			serviceMetadataLogger.info("Get charge task request received. serviceId={}, chargeId={}", serviceId,
					chargeId);
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			userSessionDetails = getLocalSessionDetails(userSessionDetails);
			if (userSessionDetails == null) {
				serviceMetadataLogger.warn("User session not found. serviceId={}, chargeId={}", serviceId, chargeId);
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
			if (tokenValidationResult != null) {
				serviceMetadataLogger.warn("Service token validation failed. serviceId={}, chargeId={}", serviceId,
						chargeId);
				return tokenValidationResult;
			}

			return serviceMetadataService.getChargeTask(serviceId, chargeId, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				serviceMetadataLogger.error("Error while getting charge tasks. serviceId={}, chargeId={}", serviceId,
						chargeId, ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
    
    @PostMapping("/saveServiceCharge")
	public ResponseEntity<?> saveServiceCharge(@Valid @RequestBody ServiceChargeResDTO serviceChargeDTO,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceChargeDTO.getServiceId(), responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.saveServiceCharge(serviceChargeDTO,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
    @GetMapping("/serviceChargeDetail")
	public ResponseEntity<?> getServiceChargeDetail(@RequestParam Integer serviceId,@RequestParam Long chargeId,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails=getLocalSessionDetails(userSessionDetails);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getServiceChargeDetail(serviceId,chargeId,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
    
    @GetMapping("/serviceChargeDetailList")
   	public ResponseEntity<?> getServiceChargeDetailList(@RequestParam Integer serviceId,HttpServletRequest request) {
   		JSONObject responseJson = new JSONObject();

   		try {
   			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
   			userSessionDetails=getLocalSessionDetails(userSessionDetails);
   	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
   	        if (tokenValidationResult != null) {
   	            return tokenValidationResult;
   	        }
   			if (userSessionDetails == null) {
   				responseJson.put("errorCode", "400");
   				responseJson.put("errorMessage", "Bad Request");
   				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
   			}
   			return serviceMetadataService.getServiceChargeDetailList(serviceId,userSessionDetails);

   		} catch (Exception ex) {
   			responseJson.put("errorCode", "500");
   			responseJson.put("errorMessage", "Internal Server Error");
   			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
   		}

   	}
    
    @PostMapping("/deleteChargeDetail")
   	public ResponseEntity<?> deleteChargeDetail(@RequestParam Integer serviceId,@RequestParam Long chargeId,HttpServletRequest request) {
   		JSONObject responseJson = new JSONObject();

   		try {
   			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
   			userSessionDetails=getLocalSessionDetails(userSessionDetails);
   	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
   	        if (tokenValidationResult != null) {
   	            return tokenValidationResult;
   	        }
   			if (userSessionDetails == null) {
   				responseJson.put("errorCode", "400");
   				responseJson.put("errorMessage", "Bad Request");
   				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
   			}
   			return serviceMetadataService.deleteChargeDetail(serviceId,chargeId,userSessionDetails);

   		} catch (Exception ex) {
   			responseJson.put("errorCode", "500");
   			responseJson.put("errorMessage", "Internal Server Error");
   			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
   		}

   	}
    @PostMapping("/freezeService")
    public ResponseEntity<?> freezeService(@RequestParam Integer serviceId,HttpServletRequest request) {
    	JSONObject responseJson = new JSONObject();
    	try {
    		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
   			userSessionDetails=getLocalSessionDetails(userSessionDetails);
    		if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.freezeService(serviceId,userSessionDetails);
        }catch (Exception ex) {
        	ex.printStackTrace();
   			responseJson.put("errorCode", "500");
   			responseJson.put("errorMessage", "Internal Server Error");
   			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
   		}
    }
    
    @GetMapping("/allowed-actions")
    public ResponseEntity<?> getActionsMaster() {
        return ResponseEntity.ok(masterDataService.getServiceActions());
    }
    
    @PostMapping("/unfreeze")
    public ResponseEntity<?> unfreeze(@RequestParam Integer serviceId,HttpServletRequest request) {
    	try {
    		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    		if (userSessionDetails == null) {
    			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return serviceMetadataService.unfreeze(serviceId,userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
    }
    
    
    @GetMapping("/service-form-definition")
    public ServiceFormDefinitionDTO getServiceFormDefinition(@RequestParam Integer baseServiceId) {
        return serviceMetadataService.getServiceFormDefinition(baseServiceId);
    }
    
	@GetMapping("/external-service-mapping")
	public Boolean isExternalServiceMapped(@RequestParam Integer serviceId, @RequestParam String clientId) {
		return serviceMetadataService.isExternalServiceMapped(serviceId, clientId);
	}
	
	@GetMapping("/external-form-attributes")
	public List<ExternalClientFormAttributesMappingDTO> getExternalFormAttributes(
	        @RequestParam Integer serviceId) {

	    return serviceMetadataService.getExternalFormAttributes(serviceId);
	}
	
	@GetMapping("/task-level")
	public ResponseEntity<?> getLevlsOfTask(@RequestParam Integer serviceId, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			
			return serviceMetadataService.getLevlsOfTask(serviceId, userSessionDetails);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
    
	
    @GetMapping("/user-assignment-service-list")
    public ResponseEntity<?> getUserAssignmentServiceList(HttpServletRequest request) {
    	try {
    		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    		if (userSessionDetails == null) {
    			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
    		return serviceMetadataService.getUserAssignmentServiceList(userSessionDetails);
    	}catch (Exception ex) {
    		if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
    	}
    }
    @GetMapping("/user-assignment-task-list")
    public ResponseEntity<?> getUserAssignmentTaskList(@RequestParam Integer serviceId ,@RequestParam(required=false) Integer officeLevelId, HttpServletRequest request) {
    	try {
    		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    		if (userSessionDetails == null) {
    			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
    		officeLevelId=officeLevelId!=null?officeLevelId:userSessionDetails.getEntityLevelId();
    		return serviceMetadataService.getUserAssignmentTaskList(serviceId,officeLevelId,userSessionDetails);
    	}catch (Exception ex) {
    		if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
    	}
    }

	@GetMapping("/service-delivery-unit-list")
	public ResponseEntity<?> getServiceDeliveryUnitList(@RequestParam Integer serviceId,
			@RequestParam(required = false) Integer officeLevelId, HttpServletRequest request) {
		try {
    		UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
    		if (userSessionDetails == null) {
    			throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
    		officeLevelId = officeLevelId != null ? officeLevelId : userSessionDetails.getEntityLevelId();
			return serviceMetadataService.getServiceDeliveryUnitList(serviceId, officeLevelId, userSessionDetails);
    	}catch (Exception ex) {
    		if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
    	}
	}

	@PostMapping("/service-activate")
	public ResponseEntity<?> activateService(@RequestBody @Valid ServiceActivationDTO request,
			HttpServletRequest servletRequest) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(servletRequest);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Invalid session", HttpStatus.UNAUTHORIZED);
			}
			return serviceMetadataService.activateService(request, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	

	@GetMapping("/output-format-details")
	public ResponseEntity<?> getOutputFormatDetails(@RequestParam Integer serviceId, @RequestParam Long outputFormatId) {

		return serviceMetadataService.getOutputFormatDetails(serviceId, outputFormatId);
	}
	@GetMapping("/routing-parameter-list")
    public ResponseEntity<?> serviceRoutingParameterList(@RequestParam Integer serviceId,@RequestParam String typeIds,HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
	    	ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
	        if (tokenValidationResult != null) {
	            return tokenValidationResult;
	        }
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.serviceRoutingParameterList(serviceId,typeIds,userSessionDetails);

		} catch (Exception ex) {
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("/department-service-list")
	public ResponseEntity<?> getDepartmentServiceList(@RequestParam(required=false) Integer status, HttpServletRequest request,HttpServletResponse response) {
		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return sduAdoptionSerivce.getDepartmentServiceList(status,userSessionDetails);

		} catch (Exception ex) {
			ex.printStackTrace();
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("/trackingConfig/eligibleAttributes")
	public ResponseEntity<?> getEligibleTrackingAttributes(@RequestParam("serviceId") Integer serviceId, HttpServletRequest request) {
		JSONObject responseJson = new JSONObject();
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			userSessionDetails = getLocalSessionDetails(userSessionDetails);
			ResponseEntity<?> tokenValidationResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
			if (tokenValidationResult != null) {
				return tokenValidationResult;
			}
			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");
				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}
			return serviceMetadataService.getEligibleTrackingAttributes(serviceId, userSessionDetails);
		} catch (SPRuntimeError ex) {
			throw ex;
		} catch (Exception ex) {
			serviceMetadataLogger.error("Error fetching eligible tracking attributes for serviceId: {}", serviceId, ex);
			responseJson.put("errorCode", "500");
			responseJson.put("errorMessage", "Internal Server Error");
			return new ResponseEntity<>(responseJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


    @PostMapping("/saveTrackingConfig")
    public ResponseEntity<?> saveTrackingConfig(@RequestParam("serviceId") Integer serviceId, @RequestBody(required = false) ServiceTrackingConfigDTO config, HttpServletRequest request) {
        JSONObject responseJson = new JSONObject();
        try {
            if (serviceId == null || serviceId <= 0) {
                throw new SPRuntimeError("Invalid Service ID", HttpStatus.BAD_REQUEST);
            }
            UserSessionDTO userSessionDetails = getLocalSessionDetails(CommonUtil.getUserSessionDetails(request));
            if (userSessionDetails == null) {
                throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
            }
            ResponseEntity<?> tokenResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
            if (tokenResult != null) {
                return tokenResult;
            }
            return serviceMetadataService.saveTrackingConfig(serviceId, config, userSessionDetails);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            serviceMetadataLogger.error("Error in saveTrackingConfig for serviceId: {}", serviceId, ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/trackingConfig")
    public ResponseEntity<?> getTrackingConfig(@RequestParam("serviceId") Integer serviceId, HttpServletRequest request) {
        JSONObject responseJson = new JSONObject();
        try {
            if (serviceId == null || serviceId <= 0) {
                throw new SPRuntimeError("Invalid Service ID", HttpStatus.BAD_REQUEST);
            }
            UserSessionDTO userSessionDetails = getLocalSessionDetails(CommonUtil.getUserSessionDetails(request));
            if (userSessionDetails == null) {
                throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
            }
            ResponseEntity<?> tokenResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
            if (tokenResult != null) {
                return tokenResult;
            }
            return serviceMetadataService.getTrackingConfig(serviceId, userSessionDetails);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            serviceMetadataLogger.error("Error in getTrackingConfig for serviceId: {}", serviceId, ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deleteTrackingConfig")
    public ResponseEntity<?> deleteTrackingConfig(@RequestParam("serviceId") Integer serviceId, HttpServletRequest request) {
        JSONObject responseJson = new JSONObject();
        try {
            if (serviceId == null || serviceId <= 0) {
                throw new SPRuntimeError("Invalid Service ID", HttpStatus.BAD_REQUEST);
            }
            UserSessionDTO userSessionDetails = getLocalSessionDetails(CommonUtil.getUserSessionDetails(request));
            if (userSessionDetails == null) {
                throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
            }
            ResponseEntity<?> tokenResult = CommonUtil.validateServiceToken(request, serviceId, responseJson);
            if (tokenResult != null) {
                return tokenResult;
            }
            return serviceMetadataService.deleteTrackingConfig(serviceId, userSessionDetails);
        } catch (SPRuntimeError ex) {
            throw ex;
        } catch (Exception ex) {
            serviceMetadataLogger.error("Error in deleteTrackingConfig for serviceId: {}", serviceId, ex);
            throw new SPRuntimeError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private UserSessionDTO getLocalSessionDetails(UserSessionDTO userSessionDetails) {
//    	userSessionDetails=new UserSessionDTO();
//    	userSessionDetails.setUserID(-2);
//    	userSessionDetails.setStateId(32);
//    	userSessionDetails.setDepartmentId(544);
//    	userSessionDetails.setDepartmentLevelId(852);
//    	userSessionDetails.setTenantId("68d29060b44b6f4239cd5abc");
		return userSessionDetails;
	}    
    
    
    @GetMapping("/internal/tracking/configured-services")
    public ResponseEntity <List<Map<String, Object>>> getConfigureServices(@RequestParam("tenantId") String tenantId)
    {
    	List<Map<String,Object>> Services= serviceMetadataService.getConfiguredServices(tenantId);
    	return ResponseEntity.ok(Services);
    }
    
    
    @GetMapping("/internal/tracking/configuration")
    public ResponseEntity<Map<String, Object>>getPublicTrackingConfiguration(@RequestParam("serviceId") Long serviceId, @RequestParam("tenantId") String tenantId )
    {
    	Map<String , Object> configuration = serviceMetadataService.getPublicTrackingConfig(serviceId, tenantId);
    	
    	return ResponseEntity.ok(configuration);
    }
	
}
