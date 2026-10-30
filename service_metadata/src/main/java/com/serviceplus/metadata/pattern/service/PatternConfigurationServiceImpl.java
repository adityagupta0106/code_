package com.serviceplus.metadata.pattern.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.pattern.dto.PatternMasterResponseDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDetailsResponseDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplatePageDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateSummaryDTO;
import com.serviceplus.metadata.pattern.entity.PatternConfiguration;
import com.serviceplus.metadata.pattern.entity.ServicePatternConfiguration;
import com.serviceplus.metadata.pattern.enums.Placeholder;
import com.serviceplus.metadata.pattern.enums.ResetAttribute;
import com.serviceplus.metadata.pattern.enums.Separator;
import com.serviceplus.metadata.pattern.enums.SeriesType;
import com.serviceplus.metadata.pattern.repository.PatternConfigurationRepository;
import com.serviceplus.metadata.pattern.repository.ServicePatternConfigurationRepository;

@Service
public class PatternConfigurationServiceImpl implements PatternConfigurationService {

	private static final Logger logger = LoggerFactory.getLogger(PatternConfigurationServiceImpl.class);

	private final PatternConfigurationRepository patternConfigurationRepository;
	private final ServicePatternConfigurationRepository servicePatternConfigurationRepository;

	public PatternConfigurationServiceImpl(PatternConfigurationRepository patternConfigurationRepository,
			ServicePatternConfigurationRepository servicePatternConfigurationRepository) {
		this.patternConfigurationRepository = patternConfigurationRepository;
		this.servicePatternConfigurationRepository = servicePatternConfigurationRepository;
	}

	@Override
	@Transactional
	public ResponseEntity<?> savePatternTemplate(PatternTemplateDTO dto, UserSessionDTO userSessionDetails) {

		logger.info("Service : Save/Update pattern template started. id={}, templateName={}", dto.getId(),
				dto.getPatternName());

		String message = "";
		try {
			Long userId =  userSessionDetails.getUserID();
			String tenantId =  userSessionDetails.getTenantId();
			Integer entityId = userSessionDetails.getEntityId();

			PatternConfiguration entity;
			boolean isCreate = (dto.getId() == null || dto.getId() <= 0);

			if (isCreate) {

				logger.info("Creating new pattern template. userId={}, entityId={}, templateName={}", userId,
						entityId, dto.getPatternName());

				boolean exists = patternConfigurationRepository.existsByPatternNameAndCreatedBy(dto.getPatternName(),
						userId);

				if (exists) {
					logger.warn("Pattern template already exists. userId={}, templateName={}", userId,
							dto.getPatternName());
					throw new SPRuntimeError("Pattern template already exists with name: " + dto.getPatternName(),
							HttpStatus.BAD_REQUEST);
				}

				entity = new PatternConfiguration();
				entity.setPatternName(dto.getPatternName());
				entity.setTenantId(tenantId);
				entity.setEntityId(entityId);
				entity.setCreatedBy(userId);
				message = "Pattern template saved successfully.";

			} else {

				logger.info("Updating existing pattern template. id={}, userId={}, entityId={}, templateName={}",
						dto.getId(), userId, entityId, dto.getPatternName());

				entity = patternConfigurationRepository.findById(dto.getId())
						.orElseThrow(() -> new SPRuntimeError("Pattern configuration not found for id: " + dto.getId(),
								HttpStatus.BAD_REQUEST));

				boolean exists = patternConfigurationRepository
						.existsByPatternNameAndCreatedByAndIdNot(dto.getPatternName(), userId, dto.getId());

				if (exists) {
					logger.warn("Pattern template name already exists. id={}, userId={}, templateName={}", dto.getId(),
							userId, dto.getPatternName());
					throw new SPRuntimeError("Pattern template already exists with name: " + dto.getPatternName(),
							HttpStatus.BAD_REQUEST);
				}

				entity.setPatternName(dto.getPatternName());
				entity.setUpdatedBy(userId);
				message = "Pattern template updated successfully.";
			}

			if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
				if (dto.getSequenceReset() != null && dto.getSequenceReset().getAttributes() == null) {
					dto.getSequenceReset().setAttributes(dto.getAttributes());
				}
			} else if (dto.getSequenceReset() != null && dto.getSequenceReset().getAttributes() != null) {
				dto.setAttributes(dto.getSequenceReset().getAttributes());
			}

			if (dto.getPrefix() != null && "DYNAMIC".equalsIgnoreCase(dto.getPrefix().getType())) {
				dto.getPrefix().setStaticValue(null);
			}
			if (dto.getSuffix() != null && "DYNAMIC".equalsIgnoreCase(dto.getSuffix().getType())) {
				dto.getSuffix().setStaticValue(null);
			}

			entity.setConfigJson(dto);

			PatternConfiguration savedEntity = patternConfigurationRepository.save(entity);

			dto.setId(savedEntity.getId());

			logger.info("Pattern template saved/updated successfully. id={}, entityId={}, patternName={}",
					savedEntity.getId(), savedEntity.getEntityId(), savedEntity.getPatternName());

			Map<String, Object> response = new HashMap<>();
			response.put("status", "SUCCESS");
			response.put("id", savedEntity.getId());
			response.put("patternName", savedEntity.getPatternName());
			response.put("message", message);

			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				logger.error("Error saving/updating pattern template : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PatternTemplateDTO getPatternTemplateById(Long id) {
		logger.info("Fetching pattern template by id: {}", id);

		PatternConfiguration entity = patternConfigurationRepository.findById(id).orElseThrow(
				() -> new SPRuntimeError("Pattern template not found for id: " + id, HttpStatus.BAD_REQUEST));

		PatternTemplateDTO dto = null;
		if (entity.getConfigJson() instanceof PatternTemplateDTO) {
			dto = (PatternTemplateDTO) entity.getConfigJson();
		} else if (entity.getConfigJson() != null) {
			ObjectMapper mapper = new ObjectMapper();
			dto = mapper.convertValue(entity.getConfigJson(), PatternTemplateDTO.class);
		}

		if (dto == null) {
			dto = new PatternTemplateDTO();
		}
		dto.setId(entity.getId());
		dto.setPatternName(entity.getPatternName());
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public PatternTemplateDetailsResponseDTO getPatternTemplateAttributesById(Long id) {
		logger.info("Fetching pattern template attributes by id: {}", id);

		PatternConfiguration entity = patternConfigurationRepository.findById(id).orElseThrow(
				() -> new SPRuntimeError("Pattern template not found for id: " + id, HttpStatus.BAD_REQUEST));

		PatternTemplateDTO dto = null;
		ObjectMapper mapper = new ObjectMapper();
		if (entity.getConfigJson() instanceof PatternTemplateDTO) {
			dto = (PatternTemplateDTO) entity.getConfigJson();
		} else if (entity.getConfigJson() != null) {
			try {
				dto = mapper.convertValue(entity.getConfigJson(), PatternTemplateDTO.class);
			} catch (Exception ex) {
				logger.error("Error converting configJson to PatternTemplateDTO for id: {}", id, ex);
			}
		}

		String patternName = entity.getPatternName();
		String rawPattern = "";
		String configuredTemplateFormat = "";

		List<PatternTemplateDTO.PatternItemDTO> pattern = null;
		List<PatternTemplateDTO.AttributeItemDTO> attributes = null;
		PatternTemplateDTO.TokenSettingDTO prefix = null;
		PatternTemplateDTO.TokenSettingDTO suffix = null;

		if (dto != null) {
			if (dto.getPatternName() != null && !dto.getPatternName().isEmpty()) {
				patternName = dto.getPatternName();
			}
			if (dto.getRawPattern() != null) {
				rawPattern = dto.getRawPattern();
			}
			if (dto.getConfiguredTemplateFormat() != null) {
				configuredTemplateFormat = dto.getConfiguredTemplateFormat();
			}
			pattern = dto.getPattern();
			if (dto.getAttributes() != null && !dto.getAttributes().isEmpty()) {
				attributes = dto.getAttributes();
			} else if (dto.getSequenceReset() != null && dto.getSequenceReset().getAttributes() != null) {
				attributes = dto.getSequenceReset().getAttributes();
			}
			prefix = dto.getPrefix();
			suffix = dto.getSuffix();
		}

		if (entity.getConfigJson() instanceof Map<?, ?> map) {
			if ((rawPattern == null || rawPattern.isEmpty()) && map.get("rawPattern") != null) {
				rawPattern = map.get("rawPattern").toString();
			}
			if ((configuredTemplateFormat == null || configuredTemplateFormat.isEmpty())
					&& map.get("configuredTemplateFormat") != null) {
				configuredTemplateFormat = map.get("configuredTemplateFormat").toString();
			}
			if (attributes == null) {
				if (map.containsKey("attributes")) {
					try {
						attributes = mapper.convertValue(map.get("attributes"),
								mapper.getTypeFactory().constructCollectionType(List.class, PatternTemplateDTO.AttributeItemDTO.class));
					} catch (Exception ignored) {
					}
				} else if (map.get("sequenceReset") instanceof Map<?, ?> resetMap && resetMap.containsKey("attributes")) {
					try {
						attributes = mapper.convertValue(resetMap.get("attributes"),
								mapper.getTypeFactory().constructCollectionType(List.class, PatternTemplateDTO.AttributeItemDTO.class));
					} catch (Exception ignored) {
					}
				}
			}
		}

		if (suffix != null && "DYNAMIC".equalsIgnoreCase(suffix.getType())
				&& (suffix.getStaticValue() == null || suffix.getStaticValue().trim().isEmpty())) {
			suffix.setStaticValue(null);
		}
		if (prefix != null && "DYNAMIC".equalsIgnoreCase(prefix.getType())
				&& (prefix.getStaticValue() == null || prefix.getStaticValue().trim().isEmpty())) {
			prefix.setStaticValue(null);
		}

		if (pattern != null) {
		    pattern.forEach(item -> {
		        if (item.getKeyType() == null || item.getKeyType().isBlank()) {
		            item.setKeyType("components");
		        }
		    });
		}

		if (prefix != null && (prefix.getKeyType() == null || prefix.getKeyType().isBlank())) {
		    prefix.setKeyType("prefixConfig");
		}

		if (suffix != null && (suffix.getKeyType() == null || suffix.getKeyType().isBlank())) {
		    suffix.setKeyType("suffixConfig");
		}

		if (attributes != null) {
		    attributes.forEach(item -> {
		        if (item.getKeyType() == null || item.getKeyType().isBlank()) {
		            item.setKeyType("resetConfig");
		        }
		    });
		}
		
		return new PatternTemplateDetailsResponseDTO(patternName, rawPattern, configuredTemplateFormat, pattern,
				attributes, prefix, suffix);
	}

	@Override
	@Transactional(readOnly = true)
	public PatternTemplatePageDTO getAllPatternTemplates(Integer page, Integer size, UserSessionDTO userSessionDetails) {
		logger.info("Fetching all pattern templates with pagination. page={}, size={}", page, size);

		if (page == null || page < 0) {
			page = 0;
		}
		if (size == null || size <= 0) {
			size = 10;
		}
		if (size > 100) {
			size = 100;
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

		Long userId = (userSessionDetails != null && userSessionDetails.getUserID() != null)
				? userSessionDetails.getUserID()
				: null;

		Page<PatternConfiguration> entityPage = (userId != null)
				? patternConfigurationRepository.findByCreatedBy(userId, pageable)
				: patternConfigurationRepository.findAll(pageable);

		ObjectMapper mapper = new ObjectMapper();

		List<PatternTemplateSummaryDTO> records = entityPage.getContent()
		        .stream()
		        .map(entity -> mapToSummaryDTO(entity, mapper))
		        .collect(Collectors.toList());

		PatternTemplatePageDTO response = new PatternTemplatePageDTO();
		response.setRecords(records);
		response.setPage(entityPage.getNumber());
		response.setSize(entityPage.getSize());
		response.setTotalRecords(entityPage.getTotalElements());
		response.setTotalPages(entityPage.getTotalPages());
		response.setFirst(entityPage.isFirst());
		response.setLast(entityPage.isLast());

		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PatternTemplateSummaryDTO> getAllPatternTemplatesByDept(UserSessionDTO userSessionDetails) {
		Integer entityId = (userSessionDetails != null && userSessionDetails.getEntityId() != null)
				? userSessionDetails.getEntityId()
				: null;
		Long userId = (userSessionDetails != null && userSessionDetails.getUserID() != null)
				? userSessionDetails.getUserID()
				: null;

		logger.info("Fetching all pattern templates for entityId: {}, userId: {}", entityId, userId);

		List<PatternConfiguration> entityList;
		if (entityId != null) {
			entityList = patternConfigurationRepository.findByEntityId(entityId);
			if (entityList.isEmpty() && userId != null) {
				entityList = patternConfigurationRepository.findByCreatedBy(userId);
			}
		} else if (userId != null) {
			entityList = patternConfigurationRepository.findByCreatedBy(userId);
		} else {
			entityList = patternConfigurationRepository.findAll();
		}

		ObjectMapper mapper = new ObjectMapper();

		return entityList.stream().map(entity -> mapToSummaryDTO(entity, mapper)).collect(Collectors.toList());
	}

	private PatternTemplateSummaryDTO mapToSummaryDTO(PatternConfiguration entity, ObjectMapper mapper) {
		String rawPattern = "";
		if (entity.getConfigJson() instanceof PatternTemplateDTO) {
			rawPattern = ((PatternTemplateDTO) entity.getConfigJson()).getRawPattern();
		} else if (entity.getConfigJson() instanceof Map<?, ?> map) {
			Object rp = map.get("rawPattern");
			rawPattern = rp != null ? rp.toString() : "";
		} else if (entity.getConfigJson() != null) {
			try {
				PatternTemplateDTO dto = mapper.convertValue(entity.getConfigJson(), PatternTemplateDTO.class);
				if (dto != null && dto.getRawPattern() != null) {
					rawPattern = dto.getRawPattern();
				}
			} catch (Exception ignored) {
			}
		}

		return new PatternTemplateSummaryDTO(entity.getId(), entity.getPatternName(),
				rawPattern != null ? rawPattern : "");
	}

	@Override
	@Transactional
	public ResponseEntity<?> delete(Long id) {

		logger.info("Delete pattern template started. id={}", id);

		PatternConfiguration entity = patternConfigurationRepository.findById(id).orElseThrow(
				() -> new SPRuntimeError("Pattern template not found with id: " + id, HttpStatus.BAD_REQUEST));

		List<ServicePatternConfiguration> usages = servicePatternConfigurationRepository.findAllByPatternId(id);
		if (!usages.isEmpty()) {
			List<Long> serviceIds = usages.stream()
					.map(ServicePatternConfiguration::getServiceId)
					.filter(Objects::nonNull)
					.distinct()
					.collect(Collectors.toList());

			String serviceIdsStr = serviceIds.stream().map(String::valueOf).collect(Collectors.joining(", "));
			logger.warn("Cannot delete pattern template id={}. It is used in service(s): {}", id, serviceIdsStr);
			throw new SPRuntimeError("This configuration is used in service_id(s): " + serviceIdsStr,
					HttpStatus.BAD_REQUEST);
		}

		patternConfigurationRepository.delete(entity);

		logger.info("Pattern template deleted successfully. id={}", id);
		return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Pattern template deleted successfully"));
	}

	@Override
	public PatternMasterResponseDTO getMasters() {
		logger.info("Fetching pattern template masters");

		List<PatternMasterResponseDTO.PlaceholderDTO> placeholders = Arrays.stream(Placeholder.values())
				.map(p -> new PatternMasterResponseDTO.PlaceholderDTO(p.getLabel(), p.getValue(), p.getDescription(),
						p.getSample()))
				.collect(Collectors.toList());

		List<PatternMasterResponseDTO.SeparatorDTO> separators = Arrays.stream(Separator.values())
				.map(s -> new PatternMasterResponseDTO.SeparatorDTO(s.getLabel(), s.getValue(), s.getSymbol()))
				.collect(Collectors.toList());

		List<PatternMasterResponseDTO.SeriesTypeDTO> seriesTypes = Arrays.stream(SeriesType.values())
				.map(st -> new PatternMasterResponseDTO.SeriesTypeDTO(st.getLabel(), st.getValue()))
				.collect(Collectors.toList());

		List<PatternMasterResponseDTO.ResetAttributeDTO> resetAttributes = Arrays.stream(ResetAttribute.values()).map(
				ra -> new PatternMasterResponseDTO.ResetAttributeDTO(ra.getLabel(), ra.getValue(), ra.getDescription()))
				.collect(Collectors.toList());

		return new PatternMasterResponseDTO(placeholders, separators, seriesTypes, resetAttributes);
	}

}
