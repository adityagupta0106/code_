package com.serviceplus.metadata.formSubscription.service;

import static com.serviceplus.metadata.utility.CommonUtil.fileToBase64;
import static com.serviceplus.metadata.utility.SnowflakeIdGenerator.createUniqueId;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.metadata.dto.AttachmentDTO;
import com.serviceplus.metadata.dto.ConfigurationDTO;
import com.serviceplus.metadata.dto.NotificationRequestDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.DataDTO;
import com.serviceplus.metadata.dto.ProcessFlowDTO.FormDetail;
import com.serviceplus.metadata.dto.ProcessFlowDTO.NodeDTO;
import com.serviceplus.metadata.dto.UserProfile;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceDefinition;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceMetadata;
import com.serviceplus.metadata.entity.ServiceProcessFlow;
import com.serviceplus.metadata.entity.ServiceTemplate;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.TenantConfigurationFeignClient;
import com.serviceplus.metadata.formSubscription.dto.ManualFormSaveReqDTO;
import com.serviceplus.metadata.formSubscription.dto.ManualFormServiceDTO;
import com.serviceplus.metadata.formSubscription.dto.ManualFormTaskResDTO;
import com.serviceplus.metadata.formSubscription.dto.PublishFormDTO;
import com.serviceplus.metadata.formSubscription.entity.FormPublishLog;
import com.serviceplus.metadata.formSubscription.repository.IFormPublishLogRepository;
import com.serviceplus.metadata.repository.IProcessFlowRepository;
import com.serviceplus.metadata.repository.IServiceDefinitionRepository;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceMetadataRepository;
import com.serviceplus.metadata.repository.IServiceTemplateRepository;
import com.serviceplus.metadata.service.ServiceMetadataService;
import com.serviceplus.metadata.service.UserService;
import com.serviceplus.metadata.utility.ApplicationConstants;

@Service
public class FormSubscriptionService {

	private static final Logger FORM_SUBSCRIPTION = LogManager.getLogger("formSubscription");
	@Autowired
	private UserService userService;

	@Autowired
	private IServiceDefinitionRepository serviceDefitionRepo;

	@Autowired
	private FormSubscriptionKafkaProducer kafkaProducer;
	@Autowired
	private IProcessFlowRepository flowRepository;

	@Autowired
	private IFormPublishLogRepository logRepository;

	@Autowired
	private IServiceMetadataRepository metadataRepository;
	
	@Autowired
	private ServiceMetadataService serviceMetadataService;
	
	@Autowired
	private IServiceTemplateRepository serviceTemplateRepository;
	
	@Autowired
	private IServiceLogRepository serviceLogRepo;
	
	@Autowired
	private TenantConfigurationFeignClient tenantConfigurationFeignClient;

	@Value("${mail.form.publish.subject}")
	private String subject;

	@Value("${mail.form.publish.body}")
	private String bodyTemplate;

	@Transactional
	public ResponseEntity<?> formPublish(PublishFormDTO dto) {
		try {

			// service_template
			List<ServiceProcessFlow> serviceList = flowRepository.findActiveFlowsSubscribedToForm(dto.getHolderId());
			Map<Long, UserProfile> usersMap = new HashMap<Long, UserProfile>();


			for (ServiceProcessFlow flow : serviceList) {
				List<FormPublishLog> logs = new ArrayList<>();
				Integer serviceId = flow.getServiceId();
				Optional<ServiceMetadata> serviceMetadataDB = metadataRepository.findById(serviceId);
				ServiceMetadata serviceMetadata = null;
				if (serviceMetadataDB.isPresent()) {
					serviceMetadata = serviceMetadataDB.get();
				}

				ProcessFlowDTO pf = flow.getProcessFlowJson();
				boolean changed = false;
				ServiceLog serviceLog = serviceLogRepo.findByServiceId(serviceId);
				ServiceDefinition serviceDetail = getServiceDetails(serviceId);
				if (serviceDetail == null || serviceLog==null) {
					FORM_SUBSCRIPTION.info("Service detail not found, serviceId :{}", serviceId);
					continue;
				}

				Long userId = serviceDetail.getUserId();

				for (NodeDTO node : pf.getNodes()) {
					DataDTO data = node.getData();
					if (data == null || data.getFormDetail() == null)
						continue;

					FormDetail fd = data.getFormDetail();
					if (Boolean.FALSE.equals(fd.getIsSubscribed()))
						continue;
					if (!dto.getHolderId().equals(fd.getHolderId()))
						continue;

					FormPublishLog log = new FormPublishLog();
					log.setUserId(userId);
					log.setServiceId(serviceId);
					log.setServiceName(serviceDetail.getDefinitionJson().getName());
					log.setTaskId(node.getId());
					log.setTaskName(data.getName());
					log.setHolderId(dto.getHolderId());
					String formId = fd.getFormId();
					log.setFormId(formId);
					log.setPublishFormId(dto.getPublishFormId());
					log.setPublishVersion(dto.getPublishVersion());
					log.setMode(fd.getSubscriptionMode());
					log.setcDate(new Date());

					if ("AUTO".equals(fd.getSubscriptionMode())) {
						ServiceTemplate serviceTemplate = serviceTemplateRepository.findByServiceIdAndTaskIdAndFormId(serviceId, node.getId() ,fd.getFormId());
						if(serviceTemplate != null) {
							serviceTemplate.setFormId(dto.getPublishFormId());
							serviceTemplateRepository.save(serviceTemplate);
						}
						fd.setFormId(dto.getPublishFormId());
						changed = true;
						log.setApply(true);
						log.setuDate(new Date());
					}
					logs.add(log);
				}
				
				if (changed) {
					flow.setProcessFlowJson(pf);
					flowRepository.save(flow);
					
					
					if (serviceMetadata != null && serviceMetadata.getMetadataJson() != null) {
						FORM_SUBSCRIPTION.info("Info : Apply manual form subscription delete Service Metadata JSON from RedisCache. ");
						serviceMetadataService.evictRedisCache(ApplicationConstants.REDIS_KEY_OF_SERVICEMETADATA+serviceMetadata.getServiceId());

						serviceMetadataService.rebuildServiceMetaData(serviceMetadata, flow);
						metadataRepository.save(serviceMetadata);
					}

				}

				UserProfile userProfile;
				if (usersMap.containsKey(userId)) {
					userProfile = usersMap.get(userId);
				} else {
					userProfile = userService.fetchUserProfile(userId);
					if (userProfile == null) {
						FORM_SUBSCRIPTION.info("User detail not found, userId :{}", userId);
						continue;
					}
					usersMap.put(userId, userProfile);
				}
				String emailId = userProfile.getData().getProfile().getU_emailId();

				FORM_SUBSCRIPTION.info("Get User detail , userId :{}", userId);

				publishMail(serviceLog.getTenantId(),emailId, serviceId, serviceDetail.getDefinitionJson().getName());
				FORM_SUBSCRIPTION.info("Email Data push on Kafka , serviceId: {},userId :{}", serviceId, userId);

				logRepository.saveAll(logs);
			}

		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("FormSubscriptionService : Error : publish form : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return ResponseEntity.ok().build();
	}

	public ServiceDefinition getServiceDetails(Integer serviceId) {
		return serviceDefitionRepo.findById(serviceId).orElse(null);
	}

	public void publishMail(String tenantId, String emailId, Integer serviceId, String serviceName) {

		Map<String, Object> configuration = new HashMap<>();

		String provider = "";
		String keys = "MAIL,SMS";

		try {

			ResponseEntity<ConfigurationDTO> response = tenantConfigurationFeignClient.getConfig(tenantId, keys);

			if (response.getBody() != null) {

				ConfigurationDTO configurationDTO = response.getBody();

				Map<String, Object> data = configurationDTO.getData();

				if (data != null) {

					Map<String, Object> mailConfiguration = (Map<String, Object>) data.get("MAIL");

					if (mailConfiguration != null) {
						configuration = mailConfiguration;
						provider = (String) configuration.getOrDefault("provider", "");
					}
				}
			}

			String mailBody = bodyTemplate;
			mailBody = mailBody.replace("{SERVICE_ID}", String.valueOf(serviceId));
			mailBody = mailBody.replace("{SERVICE_NAME}", serviceName);
			NotificationRequestDTO notificationDto = getNotificationRequestEntity(tenantId, "EMAIL", provider, emailId,
					subject, null, null, null, mailBody, null, null, configuration);

			kafkaProducer.publishFormNotificationEvent(notificationDto);

		} catch (Exception ex) {
			FORM_SUBSCRIPTION.error("Failed to publish form notification event for beneficiary {}", emailId, ex);

			throw ex;
		}
	}

	private NotificationRequestDTO getNotificationRequestEntity(String tenantId, String channel,
			String provider, String beneficiary, String subject, String copyTo, String filePath,
			String attachmentType, String content, Integer toMobileNo, Integer countryCode,
			Map<String, Object> configuration) {

		NotificationRequestDTO request = new NotificationRequestDTO();

		request.setTenantId(tenantId);
		request.setChannel(channel);
		request.setProvider(provider);
		request.setConfiguration(configuration);
		request.setCorrelationId(createUniqueId());
		Map<String, Object> recipient = new HashMap<>();
		Map<String, Object> payload = new HashMap<>();

		if ("EMAIL".equalsIgnoreCase(channel)) {

			recipient.put("to", List.of(beneficiary));

			if (copyTo != null && !copyTo.isBlank()) {
				recipient.put("cc", List.of(copyTo.split(";")));
			}

			payload.put("subject", subject);
			payload.put("body", content);

			if (filePath != null && !filePath.isBlank()) {
				AttachmentDTO attachment = new AttachmentDTO();
				attachment.setFileName(new File(filePath).getName());
				attachment.setContentType(attachmentType);
				attachment.setBase64Content(fileToBase64(filePath));
				request.setAttachments(List.of(attachment));
			}

		} else if ("SMS".equalsIgnoreCase(channel)) {
			recipient.put("mobile", toMobileNo);
			if (countryCode != null) {
				recipient.put("countryCode", countryCode);
			}
			payload.put("message", content);
		}

		request.setRecipient(recipient);
		request.setPayload(payload);

		return request;
	}

	public ResponseEntity<?> saveManual(ManualFormSaveReqDTO dto, UserSessionDTO userSessionDetails) {
		try {

			ServiceProcessFlow serviceProcessFlow = flowRepository.findByServiceId(dto.getServiceId());
			if (serviceProcessFlow == null) {
				return ResponseEntity.badRequest().body("Invalid Service Id");
			}

			ServiceMetadata serviceMetadata = metadataRepository.findById(dto.getServiceId())
					.orElseThrow(() -> new SPRuntimeError("Invalid Service Id", HttpStatus.BAD_REQUEST));
			Map<String, String> taskFormMapping = serviceMetadata.getMetadataJson().getTaskFormMapping();

			Boolean update = false;
			ProcessFlowDTO pf = serviceProcessFlow.getProcessFlowJson();

			for (NodeDTO node : pf.getNodes()) {

				DataDTO data = node.getData();
				FormDetail fd = data.getFormDetail();

				if (!node.getId().equals(dto.getTaskId()))
					continue;
				if (!fd.getFormId().equals(dto.getFormId()))
					continue;
				if (!fd.getSubscriptionMode().equals("MANUAL"))
					continue;

				FormPublishLog formPublishLog = logRepository
						.findByUserIdAndServiceIdAndTaskIdAndFormIdAndPublishFormIdAndMode(
								userSessionDetails.getUserID(), serviceProcessFlow.getServiceId(), node.getId(),
								dto.getFormId(), dto.getApplyFormId(), "MANUAL")
						.orElse(null);
				if (formPublishLog == null)
					continue;
				if(formPublishLog.getApply() != null)
					continue;
				
				if (Boolean.TRUE.equals(dto.getAccept())) {
					ServiceTemplate serviceTemplate = serviceTemplateRepository.findByServiceIdAndTaskIdAndFormId(serviceProcessFlow.getServiceId(), node.getId() ,fd.getFormId());
					if(serviceTemplate != null) {
						serviceTemplate.setFormId(dto.getApplyFormId());
						serviceTemplateRepository.save(serviceTemplate);
					}
					
					fd.setFormId(dto.getApplyFormId());
					if (taskFormMapping.containsKey(node.getId())) {
						taskFormMapping.put(node.getId(), dto.getApplyFormId());
					}
					update = true;
					formPublishLog.setApply(true);
					formPublishLog.setuDate(new Date());
					logRepository.save(formPublishLog);

					break;
				}
			}
			if (update) {
				flowRepository.save(serviceProcessFlow);

				FORM_SUBSCRIPTION.info("Info : Apply auto form subscription delete Service Metadata JSON from RedisCache. ");
				serviceMetadataService.evictRedisCache(ApplicationConstants.REDIS_KEY_OF_SERVICEMETADATA+serviceMetadata.getServiceId());
				
				serviceMetadataService.rebuildServiceMetaData(serviceMetadata, serviceProcessFlow);
				metadataRepository.save(serviceMetadata);
				
				return ResponseEntity.ok().body(Map.of("message", "Changes applied successfully to the service."));
			}
			return ResponseEntity.badRequest().body(Map.of("message", "Nothing to update."));

		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : save manual form subscription action : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getAllService(UserSessionDTO userSessionDetails) {
		try {
			List<ManualFormServiceDTO> resObj = new ArrayList<ManualFormServiceDTO>();

			List<FormPublishLog> formPublishLogList = logRepository.findByUserIdAndModeAndApply(userSessionDetails.getUserID(), "MANUAL", null);
			
			for (FormPublishLog log : formPublishLogList) {
				ManualFormServiceDTO dto = new ManualFormServiceDTO();
				dto.setServiceId(log.getServiceId());
				dto.setServiceName(log.getServiceName());
				resObj.add(dto);
			}
			return ResponseEntity.ok(resObj);
		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : Get all service : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getAllTasks(Integer serviceId, UserSessionDTO userSessionDetails) {
		try {

			List<ManualFormTaskResDTO> resObj = new ArrayList<ManualFormTaskResDTO>();

			List<FormPublishLog> formPublishLogList = logRepository.findLogs(userSessionDetails.getUserID(), serviceId,
					"MANUAL");

			for (FormPublishLog formPublishLog : formPublishLogList) {
				ManualFormTaskResDTO dto = new ManualFormTaskResDTO();

				dto.setServiceId(formPublishLog.getServiceId());
				dto.setServiceName(formPublishLog.getServiceName());
				dto.setTaskId(formPublishLog.getTaskId());
				dto.setTaskName(formPublishLog.getTaskName());
				dto.setFormId(formPublishLog.getFormId());
				dto.setPublishFormId(formPublishLog.getPublishFormId());
				dto.setVersion(formPublishLog.getPublishVersion());

				resObj.add(dto);
			}
			return ResponseEntity.ok(resObj);

		} catch (Exception e) {
			if (e instanceof SPRuntimeError) {
				throw e;
			} else {
				FORM_SUBSCRIPTION.error("Error : Get all task : {}", e);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
}
