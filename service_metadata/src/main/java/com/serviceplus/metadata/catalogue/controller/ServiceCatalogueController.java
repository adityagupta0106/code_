package com.serviceplus.metadata.catalogue.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.catalogue.dto.ServiceCatalogueConfigurationRequestDTO;
import com.serviceplus.metadata.catalogue.dto.ServiceCatalogueQuestionnaireRequestDTO;
import com.serviceplus.metadata.catalogue.service.ServiceCatalogueService;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.service.ServiceMasterDataService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/catalogue")
public class ServiceCatalogueController {

	private static final Logger serviceMetadataLogger = LogManager.getLogger(ServiceCatalogueController.class);

	private final ServiceCatalogueService serviceCatalogueService;
	private final ServiceMasterDataService serviceMasterDataService;

	public ServiceCatalogueController(ServiceCatalogueService serviceCatalogueService, ServiceMasterDataService serviceMasterDataService) {
		this.serviceCatalogueService = serviceCatalogueService;
		this.serviceMasterDataService = serviceMasterDataService;
	}

	@GetMapping("/configurations")
	public ResponseEntity<?> getConfigurations(HttpServletRequest request,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String search) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.getConfigurations(userSessionDetails, status, search);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while fetching service catalogue configurations : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/services")
	public ResponseEntity<?> getServices(HttpServletRequest request, @RequestParam(required = false) String search,
			@RequestParam(required = false) Integer status) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.getServices(userSessionDetails, search, status);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while fetching services for service catalogue : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/configuration")
	public ResponseEntity<?> createConfiguration(HttpServletRequest request,
			@RequestBody ServiceCatalogueConfigurationRequestDTO configurationRequest) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.createConfiguration(userSessionDetails, configurationRequest);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while creating service catalogue configuration : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/configuration")
	public ResponseEntity<?> getConfiguration(HttpServletRequest request, @RequestParam Long configurationId) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.getConfiguration(userSessionDetails, configurationId);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while fetching service catalogue configuration : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/configuration/modify")
	public ResponseEntity<?> modifyConfiguration(HttpServletRequest request,
			@RequestBody ServiceCatalogueConfigurationRequestDTO configurationRequest) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.modifyConfiguration(userSessionDetails, configurationRequest);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while modifying service catalogue configuration : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/questionnaire")
	public ResponseEntity<?> getQuestionnaire(HttpServletRequest request) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return ResponseEntity.ok(serviceMasterDataService.getCatalogueQuestioner());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while fetching beneficiary questionnaire : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/questionnaire/save")
	public ResponseEntity<?> saveQuestionnaire(HttpServletRequest request,
			@RequestBody ServiceCatalogueQuestionnaireRequestDTO questionnaireRequest) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.saveQuestionnaire(userSessionDetails, questionnaireRequest);

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while saving beneficiary questionnaire : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/configuration/activate")
	public ResponseEntity<?> activateConfiguration(HttpServletRequest request,
			@RequestBody ServiceCatalogueConfigurationRequestDTO configurationRequest) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.activateConfiguration(userSessionDetails,
					configurationRequest.getConfigurationId());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while activating service catalogue configuration : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/configuration/deactivate")
	public ResponseEntity<?> deactivateConfiguration(HttpServletRequest request,
			@RequestBody ServiceCatalogueConfigurationRequestDTO configurationRequest) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.deactivateConfiguration(userSessionDetails,
					configurationRequest.getConfigurationId());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while deactivating service catalogue configuration : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/configuration/delete")
	public ResponseEntity<?> deleteConfiguration(HttpServletRequest request,
			@RequestBody ServiceCatalogueConfigurationRequestDTO configurationRequest) {

		JSONObject responseJson = new JSONObject();

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);

			if (userSessionDetails == null) {
				responseJson.put("errorCode", "400");
				responseJson.put("errorMessage", "Bad Request");

				return new ResponseEntity<>(responseJson.toString(), HttpStatus.BAD_REQUEST);
			}

			return serviceCatalogueService.deleteConfiguration(userSessionDetails,
					configurationRequest.getConfigurationId());

		} catch (Exception ex) {

			ex.printStackTrace();

			if (ex instanceof SPRuntimeError) {
				throw (SPRuntimeError) ex;
			}

			serviceMetadataLogger.error("Error while deleting service catalogue configuration : ", ex);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@GetMapping("/service-data")
	public ResponseEntity<?> getServiceCatalogueData(
	        @RequestParam Integer serviceId) {

	    return ResponseEntity.ok(
	            serviceCatalogueService.buildServiceCatalogueData(serviceId)
	    );
	}
	
	@GetMapping("/service-catalogue")
	public ResponseEntity<?> getServiceCatalogue(
	        @RequestParam(name = "state", required = false) Integer state,
	        @RequestParam(name = "dept", required = false) Integer dept,
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "20") int size,
	        HttpServletRequest request) {

		String userDetails = request.getHeader("USER-DETAILS");
		UserSessionDTO user = CommonUtil.getUserSessionDetails(request);

		System.out.println("CATALOGUE TENANT: "
		        + (user == null ? null : user.getTenantId()));

		System.out.println("CATALOGUE ROLE IDs: "
		        + (user == null || user.getRoles() == null
		            ? List.of()
		            : user.getRoles().stream()
		                .map(r -> r.getRoleId())
		                .toList()));

		return ResponseEntity.ok(
		    serviceCatalogueService.getServiceCatalogue(
		        state, dept, page, size, userDetails
		    )
		);
	}
}