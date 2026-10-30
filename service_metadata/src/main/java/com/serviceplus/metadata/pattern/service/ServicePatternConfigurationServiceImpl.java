package com.serviceplus.metadata.pattern.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO;
import com.serviceplus.metadata.pattern.dto.ServicePatternConfigurationDTO;
import com.serviceplus.metadata.pattern.dto.ServicePatternSummaryDTO;
import com.serviceplus.metadata.pattern.entity.PatternConfiguration;
import com.serviceplus.metadata.pattern.entity.ServicePatternConfiguration;
import com.serviceplus.metadata.pattern.enums.MappedAttrType;
import com.serviceplus.metadata.pattern.repository.PatternConfigurationRepository;
import com.serviceplus.metadata.pattern.repository.ServicePatternConfigurationRepository;

@Service
public class ServicePatternConfigurationServiceImpl implements ServicePatternConfigurationService {

	private static final Logger logger = LoggerFactory.getLogger(ServicePatternConfigurationServiceImpl.class);

	private final ServicePatternConfigurationRepository repository;
	private final PatternConfigurationRepository patternConfigurationRepository;
	private final ObjectMapper objectMapper;

	public ServicePatternConfigurationServiceImpl(ServicePatternConfigurationRepository repository,
			PatternConfigurationRepository patternConfigurationRepository, ObjectMapper objectMapper) {
		this.repository = repository;
		this.patternConfigurationRepository = patternConfigurationRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	@Transactional
	public ResponseEntity<?> saveOrUpdateServicePattern(ServicePatternConfigurationDTO dto,
			UserSessionDTO userSessionDetails) {

		logger.info("Save/Update service pattern configuration started. patternTemplateId={}", dto.getPatternId());

		try {
			Long serviceId = dto.getServiceId();
			if (serviceId == null || serviceId <= 0) {
				throw new SPRuntimeError("serviceId is required", HttpStatus.BAD_REQUEST);
			}

			Long baseServiceId = serviceId / 10000L;

			Long patternId = dto.getPatternId();
			if (patternId == null || patternId <= 0) {
				throw new SPRuntimeError("patternTemplateId is required", HttpStatus.BAD_REQUEST);
			}

			// Validate attribute mappings
			if (dto.getAttributeMappings() != null) {
				for (ServicePatternConfigurationDTO.AttributeMappingDTO mapping : dto.getAttributeMappings()) {
					if (mapping.getMappedAttrType() != null) {
						MappedAttrType type = MappedAttrType.fromCode(mapping.getMappedAttrType().trim());
						if (type == null) {
							throw new SPRuntimeError(
									"Invalid mappedAttrType: " + mapping.getMappedAttrType()
											+ ". Allowed values are 'F' (Form Attribute) or 'S' (System Variable)",
									HttpStatus.BAD_REQUEST);
						}
						mapping.setMappedAttrType(type.getCode());
					}
				}
			}

			Long userId = userSessionDetails.getUserID();
			String tenantId = userSessionDetails.getTenantId();
			Long entityId = userSessionDetails.getEntityId().longValue();

			ServicePatternConfiguration entity;
			String message;
			if (dto.getId() != null) {
				Optional<ServicePatternConfiguration> existingOpt = repository.findById(dto.getId());
				entity = existingOpt.get();
				logger.info("Updating existing service pattern configuration. id={}, serviceId={}, patternId={}",
						entity.getId(), serviceId, patternId);

				entity.setPatternId(patternId);
				entity.setTenantId(tenantId);
				if (entityId != null) {
					entity.setEntityId(entityId);
				}
				entity.setUpdatedBy(userId);
				entity.setUpdatedDate(LocalDateTime.now());
				message = "Service pattern configuration updated successfully.";
			} else {
				// Verify if another record already has this serviceId and patternId
				if (repository.existsByServiceIdAndPatternId(serviceId, patternId)) {
					throw new SPRuntimeError("Service pattern configuration already exists for serviceId: " + serviceId
							+ " and patternId: " + patternId, HttpStatus.BAD_REQUEST);
				}

				logger.info("Creating new service pattern configuration. serviceId={}, patternId={}, userId={}",
						serviceId, patternId, userId);

				entity = new ServicePatternConfiguration();
				entity.setServiceId(serviceId);
				entity.setBaseServiceId(baseServiceId);
				entity.setPatternId(patternId);
				entity.setTenantId(tenantId);
				entity.setEntityId(entityId);
				entity.setCreatedBy(userId);
				entity.setCreatedDate(LocalDateTime.now());
				message = "Service pattern configuration saved successfully.";
			}

			dto.setServiceId(serviceId);
			entity.setConfigJson(dto);

			ServicePatternConfiguration savedEntity = repository.save(entity);
			dto.setId(savedEntity.getId());

			Map<String, Object> response = new HashMap<>();
			response.put("status", "SUCCESS");
			response.put("id", savedEntity.getId());
			response.put("message", message);

			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				logger.error("Error saving/updating service pattern configuration : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ServicePatternConfigurationDTO getServicePattern(Long id) {
		logger.info("Fetching service pattern configuration. Id={}", id);

		Optional<ServicePatternConfiguration> entityOpt = repository.findById(id);

		ServicePatternConfiguration entity = entityOpt
				.orElseThrow(() -> new SPRuntimeError("Service pattern configuration not found for Id: " + id,
						HttpStatus.BAD_REQUEST));

		ServicePatternConfigurationDTO dto = null;
		if (entity.getConfigJson() instanceof ServicePatternConfigurationDTO) {
			dto = (ServicePatternConfigurationDTO) entity.getConfigJson();
		} else if (entity.getConfigJson() != null) {
			try {
				dto = objectMapper.convertValue(entity.getConfigJson(), ServicePatternConfigurationDTO.class);
			} catch (Exception ex) {
				logger.error("Error converting configJson to ServicePatternConfigurationDTO", ex);
			}
		}

		if (dto == null) {
			dto = new ServicePatternConfigurationDTO();
		}
		dto.setId(entity.getId());
		dto.setServiceId(entity.getServiceId());
		dto.setPatternId(entity.getPatternId());

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ServicePatternSummaryDTO> getAllServicePatterns(Long serviceId, UserSessionDTO userSessionDetails) {
		logger.info("Fetching all service patterns. serviceId={}", serviceId);

		List<ServicePatternConfiguration> configurations;
		String tenantId = (userSessionDetails != null && userSessionDetails.getTenantId() != null)
				? userSessionDetails.getTenantId()
				: null;

		if (serviceId != null && serviceId > 0) {
			if (tenantId != null && !tenantId.isEmpty()) {
				configurations = repository.findAllByServiceIdAndTenantId(serviceId, tenantId);
				if (configurations.isEmpty()) {
					configurations = repository.findAllByServiceId(serviceId);
				}
			} else {
				configurations = repository.findAllByServiceId(serviceId);
			}
		} else if (tenantId != null && !tenantId.isEmpty()) {
			configurations = repository.findAllByTenantId(tenantId);
			if (configurations.isEmpty()) {
				configurations = repository.findAll();
			}
		} else {
			configurations = repository.findAll();
		}

		if (configurations.isEmpty()) {
			return Collections.emptyList();
		}

		// Collect patternIds to batch fetch pattern configurations
		Set<Long> patternIds = configurations.stream().map(ServicePatternConfiguration::getPatternId)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		Map<Long, PatternConfiguration> patternMap = Collections.emptyMap();
		if (!patternIds.isEmpty()) {
			List<PatternConfiguration> patterns = patternConfigurationRepository.findAllById(patternIds);
			patternMap = patterns.stream()
					.collect(Collectors.toMap(PatternConfiguration::getId, Function.identity(), (a, b) -> a));
		}

		List<ServicePatternSummaryDTO> result = new ArrayList<>();
		for (ServicePatternConfiguration spc : configurations) {
			PatternConfiguration pc = patternMap.get(spc.getPatternId());

			String patternName = "";
			String rawPattern = "";
			String configuredTemplateFormat = "";

			if (pc != null) {
				patternName = pc.getPatternName() != null ? pc.getPatternName() : "";
				if (pc.getConfigJson() instanceof PatternTemplateDTO ptDto) {
					rawPattern = ptDto.getRawPattern() != null ? ptDto.getRawPattern() : "";
					configuredTemplateFormat = ptDto.getConfiguredTemplateFormat() != null
							? ptDto.getConfiguredTemplateFormat()
							: "";
				} else if (pc.getConfigJson() instanceof Map<?, ?> map) {
					rawPattern = map.get("rawPattern") != null ? map.get("rawPattern").toString() : "";
					configuredTemplateFormat = map.get("configuredTemplateFormat") != null
							? map.get("configuredTemplateFormat").toString()
							: "";
				} else if (pc.getConfigJson() != null) {
					try {
						PatternTemplateDTO ptDto = objectMapper.convertValue(pc.getConfigJson(),
								PatternTemplateDTO.class);
						if (ptDto != null) {
							if (ptDto.getRawPattern() != null) {
								rawPattern = ptDto.getRawPattern();
							}
							if (ptDto.getConfiguredTemplateFormat() != null) {
								configuredTemplateFormat = ptDto.getConfiguredTemplateFormat();
							}
						}
					} catch (Exception ignored) {
					}
				}
			}

			Boolean isApplicationRefNo = null;
			if (spc.getConfigJson() instanceof ServicePatternConfigurationDTO spcDto) {
				isApplicationRefNo = spcDto.getIsApplicationRefNo();
			} else if (spc.getConfigJson() instanceof Map<?, ?> map) {
				Object val = map.get("isApplicationRefNo");
				if (val instanceof Boolean b) {
					isApplicationRefNo = b;
				} else if (val != null) {
					isApplicationRefNo = Boolean.valueOf(val.toString());
				}
			} else if (spc.getConfigJson() != null) {
				try {
					ServicePatternConfigurationDTO spcDto = objectMapper.convertValue(spc.getConfigJson(),
							ServicePatternConfigurationDTO.class);
					if (spcDto != null) {
						isApplicationRefNo = spcDto.getIsApplicationRefNo();
					}
				} catch (Exception ignored) {
				}
			}

			result.add(new ServicePatternSummaryDTO(spc.getId(), patternName, rawPattern, isApplicationRefNo,
					configuredTemplateFormat));
		}

		return result;
	}

	@Override
	@Transactional
	public ResponseEntity<?> deleteServicePattern(Long id, UserSessionDTO userSessionDetails) {
		logger.info("Delete service pattern configuration. id={}", id);

		ServicePatternConfiguration entity = repository.findById(id)
				.orElseThrow(() -> new SPRuntimeError("Service pattern configuration not found for id: " + id,
						HttpStatus.BAD_REQUEST));

		repository.delete(entity);
		logger.info("Service pattern configuration deleted successfully. id={}", id);

		return ResponseEntity
				.ok(Map.of("status", "SUCCESS", "message", "Service pattern configuration deleted successfully"));
	}
}
