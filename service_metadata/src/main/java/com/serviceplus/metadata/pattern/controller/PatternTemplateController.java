package com.serviceplus.metadata.pattern.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.pattern.dto.PatternMasterResponseDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDetailsResponseDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplatePageDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateSummaryDTO;
import com.serviceplus.metadata.pattern.service.PatternConfigurationService;
import com.serviceplus.metadata.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class PatternTemplateController {

	private static final Logger logger = LoggerFactory.getLogger(PatternTemplateController.class);

	private final PatternConfigurationService patternConfigurationService;

	public PatternTemplateController(PatternConfigurationService patternConfigurationService) {
		this.patternConfigurationService = patternConfigurationService;
	}

	@GetMapping("/pattern/master")
	public ResponseEntity<PatternMasterResponseDTO> getMasters(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			logger.info("PatternTemplateController: Fetching pattern template masters");
			return ResponseEntity.ok(patternConfigurationService.getMasters());
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("PatternTemplateController : Fetching master of pattern :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/pattern/all")
	public ResponseEntity<PatternTemplatePageDTO> getAllPatternTemplates(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "50") Integer size,
			HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			logger.info("PatternTemplateController: Fetching all pattern templates with pagination. page={}, size={}",
					page, size);
			return ResponseEntity
					.ok(patternConfigurationService.getAllPatternTemplates(page, size, userSessionDetails));

		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("PatternTemplateController : Fetching all pattern templates :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/pattern/all/dept")
	public ResponseEntity<List<PatternTemplateSummaryDTO>> getAllPatternTemplatesByDept(HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			logger.info("PatternTemplateController: Fetching all pattern templates for entityId: {}",
					userSessionDetails.getEntityId());
			return ResponseEntity.ok(patternConfigurationService.getAllPatternTemplatesByDept(userSessionDetails));

		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("PatternTemplateController : Fetching pattern templates by department :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/pattern")
	public ResponseEntity<PatternTemplateDTO> getPatternTemplateByIdQuery(@RequestParam("id") Long id,
			HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			logger.info("PatternTemplateController: Fetching pattern template by id: {}", id);
			PatternTemplateDTO dto = patternConfigurationService.getPatternTemplateById(id);
			return ResponseEntity.ok(dto);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("PatternTemplateController : Fetching pattern template by id :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@GetMapping("/pattern/attr")
	public ResponseEntity<PatternTemplateDetailsResponseDTO> getPatternTemplateAttributesById(
			@RequestParam("id") Long id, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}
			logger.info("PatternTemplateController: Fetching pattern template attributes by id: {}", id);
			PatternTemplateDetailsResponseDTO response = patternConfigurationService
					.getPatternTemplateAttributesById(id);
			return ResponseEntity.ok(response);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("PatternTemplateController : Fetching pattern template attributes by id :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/pattern")
	public ResponseEntity<?> saveOrUpdatePatternTemplate(@Valid @RequestBody PatternTemplateDTO dto,
			HttpServletRequest request) {
		logger.info("PatternTemplateController: Save/Update pattern template API called. id={}, patternName={}",
				dto.getId(), dto.getPatternName());

		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			return patternConfigurationService.savePatternTemplate(dto, userSessionDetails);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				logger.error("Error while saving/updating pattern template. id={}, patternName={}", dto.getId(),
						dto.getPatternName(), ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@PostMapping("/pattern/delete")
	public ResponseEntity<?> deletePatternTemplate(@RequestParam("id") Long id, HttpServletRequest request) {
		try {
			UserSessionDTO userSessionDetails = CommonUtil.getUserSessionDetails(request);
			if (userSessionDetails == null) {
				throw new SPRuntimeError("Bad Request", HttpStatus.BAD_REQUEST);
			}

			logger.info("PatternTemplateController: Delete pattern template called for id: {}", id);
			return patternConfigurationService.delete(id);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("PatternTemplateController : Error deleting pattern template for id: {}", id, ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

}
