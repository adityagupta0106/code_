package com.serviceplus.formDesigner.service;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.dto.LinkAttributeNodeDTO;
import com.serviceplus.formDesigner.dto.PublishFormDTO;
import com.serviceplus.formDesigner.dto.RecommendedFormEventDTO;
import com.serviceplus.formDesigner.dto.UserSessionDTO;
import com.serviceplus.formDesigner.enums.FormStandaloneStatus;
import com.serviceplus.formDesigner.enums.FormStatus;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.formrecommendation.service.FormSearchDocument;
import com.serviceplus.formDesigner.formrecommendation.service.FormSearchElasticService;
import com.serviceplus.formDesigner.kafka.FormPublishKafkaProducer;
import com.serviceplus.formDesigner.kafka.RecommendedFormKafkaProducer;
import com.serviceplus.formDesigner.repository.ApplicationTemplateRepository;
import com.serviceplus.formDesigner.repository.ApplicationTemplateStandaloneRepository;
import com.serviceplus.formDesigner.repository.FormEncryptionKeyRepository;
import com.serviceplus.formDesigner.repository.FormPublishLogRepository;
import com.serviceplus.formDesigner.request.model.ApplicationTemplate;
import com.serviceplus.formDesigner.request.model.ApplicationTemplateStandalone;
import com.serviceplus.formDesigner.request.model.FormEncryptionKey;
import com.serviceplus.formDesigner.request.model.FormPublishLog;
import com.serviceplus.formDesigner.request.model.FormPublishLog.Log;
import com.serviceplus.formDesigner.request.model.IApplicationTemplate;
import com.serviceplus.formDesigner.util.ApplicationTemplateCommonUtil;
import com.serviceplus.formDesigner.util.MessageConstants;
import com.serviceplus.formDesigner.util.SnowflakeIdGenerator;

@Service
public class FormSubscriptionService {

	@Autowired
	private FormPublishLogRepository logRepository;
	@Autowired
	private ApplicationTemplateService applicationTemplateService;
	@Autowired
	private FormSearchElasticService formSearchElasticService;
	@Autowired
	private RecommendedFormKafkaProducer copyFormDataKafkaProducer;
	@Autowired
	private FormRedisService formRedisService;

	@Autowired
	private FormPublishKafkaProducer kafkaProducer;
	private static final Logger FORM_SUBSCRIPTION = LogManager.getLogger(FormSubscriptionService.class);

	@Autowired
	private ApplicationTemplateRepository repository;
	@Autowired
	private ApplicationTemplateStandaloneRepository standaloneRepository;
	@Autowired
	private FormEncryptionKeyRepository formEncryptionKeyRepository;

	@Autowired
	private ObjectMapper objectMapper;

	public ResponseEntity<?> formPublish(String formId, UserSessionDTO userSessionDetails) {
		try {
			Long userId = userSessionDetails.getUserID();

			ApplicationTemplate applTemplate = repository.findByFormIdAndCreatedByAndStatus(formId, userId,
					FormStatus.ACTIVE.getValue());
			if (applTemplate == null) {
				throw new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}

			FormPublishLog formPublishLog = logRepository.findById(applTemplate.getHolderId())
					.orElse(new FormPublishLog());
			formPublishLog.setHolderId(applTemplate.getHolderId());
			List<Log> logs = formPublishLog.getLogs() != null ? formPublishLog.getLogs() : new ArrayList<>();

			Log logEntry = new Log();
			logEntry.setFormId(applTemplate.getFormId());
			logEntry.setVersion(applTemplate.getVersion());
			logEntry.setCreatedOn(new Date());
			logEntry.setUserId(userId);
			logEntry.setTenantId(userSessionDetails.getTenantId());
			logEntry.setStateId(userSessionDetails.getClcId());
			logEntry.setDepartmentId(userSessionDetails.getEntityId());
			logEntry.setLocationId(userSessionDetails.getLocationId());
			logs.add(logEntry);
			formPublishLog.setLogs(logs);
			logRepository.save(formPublishLog);

			applTemplate.setStatus(FormStatus.PUBLISHED.getValue());
			ApplicationTemplate saveData = repository.save(applTemplate);

			int departmentId = userSessionDetails.getEntityId() != null ? userSessionDetails.getEntityId() : 0;
			int locationId = userSessionDetails.getLocationId() != null ? userSessionDetails.getLocationId() : 0;
			String formDesc = applicationTemplateService.getformDescription(saveData);

			indexInSearch(saveData, userSessionDetails.getTenantId(), departmentId, locationId, formDesc);
			publishRecommendedFormEvent(saveData, formDesc);

			Map<String, Object> finalRes = new HashMap<String, Object>();

			finalRes.put("message", "Published");
			PublishFormDTO event = new PublishFormDTO();
			event.setHolderId(saveData.getHolderId());
			event.setPublishFormId(saveData.getFormId());
			event.setPublishVersion(saveData.getVersion());
			kafkaProducer.publishFormPublishedEvent(event);

			return ResponseEntity.ok(finalRes);

		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			}
			FORM_SUBSCRIPTION.error("Service-Error : publish form : {}", e);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public IApplicationTemplate formUnpublish(String formId, UserSessionDTO userSessionDetails) {
		try {
			Long userId = userSessionDetails.getUserID();

			List<ApplicationTemplate> list = repository.findByFormIdAndStatusAndArchive(formId,
					FormStatus.PUBLISHED.getValue(), false);
			if (list.isEmpty()) {
				throw new SPRuntimeError(MessageConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}

			ApplicationTemplate applicationTemplateDB = list.get(0);
			if (!applicationTemplateDB.getCreatedBy().equals(userId)) {
				throw new SPRuntimeError("Unauthorized user", HttpStatus.UNAUTHORIZED);
			}

			applicationTemplateDB.setArchive(true);
			repository.save(applicationTemplateDB);

			formRedisService.deleteRecord(applicationTemplateDB.getFormId());

			ApplicationTemplate newTemplate = new ApplicationTemplate();
			BeanUtils.copyProperties(applicationTemplateDB, newTemplate);

			String baseFormId = extractBaseFormId(applicationTemplateDB.getFormId());
			int newVersion = applicationTemplateDB.getVersion() + 1;

			newTemplate.setArchive(false);
			newTemplate.setStatus(FormStatus.ACTIVE.getValue());
			newTemplate.setCreatedOn(new Date());
			newTemplate.setUpdateOn(new Date());
			newTemplate.setVersion(newVersion);
			newTemplate.setFormId(baseFormId + "_" + newVersion);

			ApplicationTemplate saveData = repository.save(newTemplate); // saveTemplateFormUnpublish(newTemplate,
																			// standalone);
			formEncryptionKeyRepository.findByFormIdAndServiceIdAndTaskId(applicationTemplateDB.getFormId(), 0, null)
					.ifPresent(oldKey -> {
						FormEncryptionKey newKey = new FormEncryptionKey();
						newKey.setFormId(saveData.getFormId());
						newKey.setServiceId(0);
						newKey.setTransitEncFlag(oldKey.getTransitEncFlag());
						newKey.setRestEncKey(oldKey.getRestEncKey());
						newKey.setUdate(new Date());
						formEncryptionKeyRepository.save(newKey);
					});

			String formDesc = applicationTemplateService.getformDescription(saveData);
			int departmentId = userSessionDetails.getEntityId() != null ? userSessionDetails.getEntityId() : 0;
			int locationId = userSessionDetails.getLocationId() != null ? userSessionDetails.getLocationId() : 0;
			indexInSearch(saveData, userSessionDetails.getTenantId(), departmentId, locationId, formDesc);
			publishRecommendedFormEvent(saveData, formDesc);

			return saveData;

		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			}
			FORM_SUBSCRIPTION.error("Service-Error : creating new form version : {}", e);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> formSubscribe(String formId) {
		try {
			List<ApplicationTemplate> applTemplateList = repository.findByFormIdAndStatus(formId,
					FormStatus.PUBLISHED.getValue());
			if (applTemplateList.isEmpty()) {
				throw new SPRuntimeError("Form not found for formId: " + formId, HttpStatus.BAD_REQUEST);
			}
			ApplicationTemplate applTemplate = applTemplateList.getFirst();
			Integer subscriptionCount = applTemplate.getSubscriptionCount() != null
					? applTemplate.getSubscriptionCount()
					: 0;
			applTemplate.setSubscriptionCount(subscriptionCount + 1);

			repository.save(applTemplate);
			return ResponseEntity.ok(Map.of("message", "Subscribed", "formId", applTemplate.getFormId(), "holderId",
					applTemplate.getHolderId()));

		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Service-Error : subscribe form : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> formUnsubscribe(String formId, Boolean makeCopy) {
		Map<String, String> respObject = new HashMap<String, String>();
		try {
			List<ApplicationTemplate> applTemplateList = repository.findByFormIdAndStatus(formId,
					FormStatus.PUBLISHED.getValue());
			if (applTemplateList.isEmpty()) {
				throw new SPRuntimeError("Form not found for formId: " + formId, HttpStatus.BAD_REQUEST);
			}
			ApplicationTemplate applTemplate = applTemplateList.getFirst();

			Integer subscriptionCount = applTemplate.getSubscriptionCount() != null
					? applTemplate.getSubscriptionCount()
					: 0;

			if (!isNull(subscriptionCount) && subscriptionCount > 1) {
				if (makeCopy == null || makeCopy == true) {
					ApplicationTemplate newApplicationTemplate = new ApplicationTemplate();
					BeanUtils.copyProperties(applTemplate, newApplicationTemplate);

					newApplicationTemplate.setFormId(null);
					newApplicationTemplate.setHolderId(SnowflakeIdGenerator.createUniqueId());
					
					String newFormName = "Copy of " + applTemplate.getFormName();
					newApplicationTemplate.setFormName(newFormName);
					String stringFormTemplateObject = objectMapper.writeValueAsString(newApplicationTemplate.getFormJsonData());
					JSONObject jsonFormTemplateObject = new JSONObject(stringFormTemplateObject);
					JSONObject globalSetting = jsonFormTemplateObject.optJSONObject("globalSetting");
					if (globalSetting != null) {
						globalSetting.put("formName", newFormName);
					}
					
					Map<String, Object> formJsonData = objectMapper.readValue(jsonFormTemplateObject.toString(),
							new TypeReference<Map<String, Object>>() {
							});

					newApplicationTemplate.setFormJsonData(formJsonData);
				

					newApplicationTemplate.setArchive(false);
					newApplicationTemplate.setStatus(FormStatus.PUBLISHED.getValue());
					newApplicationTemplate.setCreatedOn(new Date());
					newApplicationTemplate.setUpdateOn(new Date());
					newApplicationTemplate.setVersion(1);
					newApplicationTemplate.setSubscriptionCount(0);
					ApplicationTemplate saveData = repository.save(newApplicationTemplate);
					respObject.put("formId", saveData.getFormId());
					respObject.put("holderId", saveData.getHolderId());
				}
				subscriptionCount = subscriptionCount - 1;
			} else {
				return ResponseEntity.badRequest().body(Map.of("message",
						"This form is already dedicated exclusively to the current service. You can continue modifying"
								+ " it whenever required without affecting other service. Since there are no other active subscriptions, unsubscription"
								+ " is not applicable."));
			}

			applTemplate.setSubscriptionCount(subscriptionCount);
			repository.save(applTemplate);

			respObject.put("message", "Unsubscribed");
			return ResponseEntity.ok(respObject);

		} catch (Exception e) {

			if (e instanceof SPRuntimeError) {
				throw (SPRuntimeError) e;
			}
			FORM_SUBSCRIPTION.error("Service-Error : subscribe form : {}", e);

			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<?> getDeptAllPublishForms(UserSessionDTO userSessionDetails) {
		try {
			List<Map<String, Object>> respObject = new ArrayList<Map<String, Object>>();
			Integer departmentId = userSessionDetails.getEntityId();
			List<ApplicationTemplate> applTemplList = repository
					.findByStatusAndDepartmentIdAndArchive(FormStatus.PUBLISHED.getValue(), departmentId, false);
			for (ApplicationTemplate applTempl : applTemplList) {
				Map<String, Object> resp = new HashMap<String, Object>();
				resp.put("formId", applTempl.getFormId());
				resp.put("formName", applTempl.getFormName());
				resp.put("holderId", applTempl.getHolderId());
				resp.put("global", ApplicationTemplateCommonUtil.isGlobalForm(applTempl));

				respObject.add(resp);
			}
			return ResponseEntity.ok(respObject);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Service-Error : get all published form of department : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getAllPublishForms(String mode, UserSessionDTO userSessionDetails) {
		try {
			List<Map<String, String>> respObject = new ArrayList<Map<String, String>>();
			if (mode.equalsIgnoreCase("service")) {
				List<ApplicationTemplate> applTemplList = repository
						.findByStatusAndArchive(FormStatus.PUBLISHED.getValue(), false);
				for (ApplicationTemplate applTempl : applTemplList) {
					Map<String, String> resp = new HashMap<String, String>();
					resp.put("formId", applTempl.getFormId());
					resp.put("formName", applTempl.getFormName());
					resp.put("holderId", applTempl.getHolderId());

					respObject.add(resp);
				}
			} else if (mode.equalsIgnoreCase("standalone")) {
				List<ApplicationTemplateStandalone> applTemplList = standaloneRepository.findByCreatedByAndTenantId(userSessionDetails.getUserID(), userSessionDetails.getTenantId());
				for (ApplicationTemplateStandalone applTempl : applTemplList) {
					if (applTempl.getStatus().equals(FormStandaloneStatus.YET_TO_PUBLISH.getValue())
							|| applTempl.getStatus().equals(FormStandaloneStatus.PUBLISHED.getValue())
							|| applTempl.getStatus().equals(FormStandaloneStatus.ARCHIVED.getValue())) {

						Map<String, String> resp = new HashMap<String, String>();
						resp.put("formId", applTempl.getFormId());
						resp.put("formName", applTempl.getFormName());
						resp.put("holderId", applTempl.getHolderId());
						respObject.add(resp);
						
					}

				}
			}
			return ResponseEntity.ok(respObject);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Service-Error : get all published form of department : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

//	private ApplicationTemplate saveTemplateFormPublish(IApplicationTemplate template) {
//	    if (template instanceof ApplicationTemplate) {
//	        return repository.save((ApplicationTemplate) template);
//	    }
//
//	    ApplicationTemplateStandalone standalone = (ApplicationTemplateStandalone) template;
//
//	    if (standalone.getTinyUrlToken() == null || standalone.getTinyUrlToken().isEmpty()) {
//	        String tinyUrlToken = generateUniqueTinyUrlToken();
//	        standalone.setTinyUrlToken(tinyUrlToken);
//	        standalone.setConfigureTinyUrl(shortUrlBaseConfigure + tinyUrlToken);
//	        standalone.setProdTinyUrl(shortUrlBaseProd + tinyUrlToken);
//	    }
//
//	    return standaloneRepository.save(standalone);
//	}

//	private String generateUniqueTinyUrlToken() {
//	    for (int attempt = 0; attempt < MAX_TOKEN_GENERATION_ATTEMPTS; attempt++) {
//	        String candidate = ApplicationTemplateCommonUtil.randomCode();
//	        if (!standaloneRepository.existsByTinyUrlToken(candidate)) {
//	            return candidate;
//	        }
//	    }
//	    FORM_SUBSCRIPTION.error("Failed to generate a unique tinyUrlToken after {} attempts", MAX_TOKEN_GENERATION_ATTEMPTS);
//	    throw new SPRuntimeError("Could not generate short URL, please retry", HttpStatus.INTERNAL_SERVER_ERROR);
//	}

	private void indexInSearch(ApplicationTemplate saveData, String tenantId, int departmentId, int locationId,
			String formDesc) {
		try {
			FormSearchDocument formSearchDocument = new FormSearchDocument();
			formSearchDocument.setHolderId(saveData.getHolderId());
			formSearchDocument.setFormId(saveData.getFormId());
			formSearchDocument.setFormName(saveData.getFormName());
			formSearchDocument.setFormDescription(formDesc);
			formSearchDocument.setTenantId(tenantId);
			formSearchDocument.setDepartmentId(String.valueOf(departmentId));
			formSearchDocument.setLocationId(String.valueOf(locationId));
			formSearchElasticService.upsert(formSearchDocument);
		} catch (Exception e) {
			FORM_SUBSCRIPTION.error("Elastic indexing failed on publish for holderId={}, formId={}", saveData.getHolderId(), saveData.getFormId(), e);
		}
	}

	private void publishRecommendedFormEvent(ApplicationTemplate saveData, String formDesc) {
		try {
			List<LinkAttributeNodeDTO> allAttributeForLink = ApplicationTemplateCommonUtil
					.extractAttributesForLink(saveData.getFormJsonData());
			List<String> labels = allAttributeForLink.stream().map(LinkAttributeNodeDTO::getLabel)
					.filter(Objects::nonNull).collect(Collectors.toList());

			RecommendedFormEventDTO copyFormEventDTO = new RecommendedFormEventDTO();
			copyFormEventDTO.setFormId(saveData.getFormId());
			copyFormEventDTO.setFormName(saveData.getFormName() + "_V" + saveData.getVersion());
			copyFormEventDTO.setFormDesc(formDesc);
			copyFormEventDTO.setLabels(labels);

			copyFormDataKafkaProducer.publishRecommendedForm(copyFormEventDTO);
		} catch (Exception e) {
			FORM_SUBSCRIPTION.error("Kafka publish failed on publish for formId={}", saveData.getFormId(), e);
		}
	}

	private String extractBaseFormId(String formId) {
		int idx = formId.lastIndexOf('_');
		return idx > -1 ? formId.substring(0, idx) : formId;
	}
}
