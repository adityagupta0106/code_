package com.serviceplus.metadata.notification.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.metadata.dto.LabelValue;
import com.serviceplus.metadata.dto.NotificationListResponseDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceMasterData;
import com.serviceplus.metadata.entity.ServiceMetadata;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.notification.dto.ChangeStatusDTO;
import com.serviceplus.metadata.notification.dto.NotificationDTO;
import com.serviceplus.metadata.notification.dto.NotificationReqDTO;
import com.serviceplus.metadata.notification.entity.NotificationConfiguration;
import com.serviceplus.metadata.notification.entity.NotificationTriggerMaster;
import com.serviceplus.metadata.notification.repository.INotificationRepository;
import com.serviceplus.metadata.repository.IServiceMasterDataRepository;
import com.serviceplus.metadata.repository.IServiceMetadataRepository;
import com.serviceplus.metadata.utility.ApplicationConstants;

@Service
@Transactional
public class NotificationManagementService {

	private static final Logger NOTIF_MAPPING = LogManager.getLogger("notifMapping");
	
	private final INotificationRepository notificationRepository;
	private final IServiceMasterDataRepository masterDataRepository;
	private final IServiceMetadataRepository serviceMetadataRepo;

	public NotificationManagementService(INotificationRepository notificationRepository, IServiceMasterDataRepository masterDataRepository, 
			IServiceMetadataRepository serviceMetadataRepo) {
		super();
		this.notificationRepository = notificationRepository;
		this.masterDataRepository = masterDataRepository;
		this.serviceMetadataRepo = serviceMetadataRepo;
	}

	public ResponseEntity<?> getNotificationToMaster() {
		try {
			List<Map<String, Object>> master = new ArrayList<>();

			ServiceMasterData masterData = masterDataRepository.findByName(ApplicationConstants.NOTIFICATION_TO_MASTER)
					.orElseThrow(() -> new RuntimeException("Notification To Master not found"));

			ObjectMapper mapper = new ObjectMapper();

			List<LabelValue> masters;
			try {
				masters = mapper.readValue(masterData.getData(), new TypeReference<List<LabelValue>>() {
				});
			} catch (JsonProcessingException e) {
				throw new SPRuntimeError("Invalid Notification To Master JSON", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			for (LabelValue labelValue : masters) {

				Map<String, Object> masterMap = new HashMap<>();
				masterMap.put("label", labelValue.getLabel());
				masterMap.put("value", labelValue.getValue());
				
				if (labelValue.getValue().equals(2)) {
					masterMap.put("sysValue", ApplicationConstants.APPLICANT_EMAIL_SYSTEM_VARIABLE);
				} else if (labelValue.getValue().equals(3)) {
					masterMap.put("sysValue", ApplicationConstants.APPLICANT_EMAIL_SYSTEM_VARIABLE);
				}
				master.add(masterMap);
			}
			return ResponseEntity.ok(master);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_MAPPING.error("Error : Get Notification To master :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	public ResponseEntity<?> getTriggerPointMst() {
		try {
			ServiceMasterData masterData = masterDataRepository.findByName("Notification Trigger Point").orElseThrow(() -> new RuntimeException("Notification Trigger Point not found"));
			ObjectMapper mapper = new ObjectMapper();
			List<NotificationTriggerMaster> masters = mapper.readValue(masterData.getData(),
					new TypeReference<List<NotificationTriggerMaster>>() {
			});
			return ResponseEntity.ok(Map.of("master", masters));
		} catch (Exception ex) {
			ex.printStackTrace();
			NOTIF_MAPPING.error("Error : Get all notification trigger-point master :", ex);
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	public ResponseEntity<?> saveNotification(NotificationReqDTO notificationReqDTO, UserSessionDTO userSession) {
		try {
			NotificationConfiguration entity;
			String message = "";

			if (notificationReqDTO.getNotificationId() != null) {

				entity = notificationRepository.findById(notificationReqDTO.getNotificationId())
						.orElseThrow(() -> new SPRuntimeError("Notification not found", HttpStatus.BAD_REQUEST));

				if (entity.getActive()) {
					throw new SPRuntimeError("Activate notification can't be edit.", HttpStatus.BAD_REQUEST);
				}

				entity.setModifiedBy(userSession.getUserID().longValue());
				entity.setModifiedOn(new Date());
				message = "Notification Configuration Edit Successfully";

			} else {

				entity = new NotificationConfiguration();
				entity.setCreatedBy(userSession.getUserID().longValue());
				entity.setCreatedOn(new Date());
				entity.setActive(false);
				message = "Notification Configuration Save Successfully";
			}

			NotificationDTO notificationDTO = notificationReqDTO.getNotificationDTO();
			entity.setServiceId(notificationDTO.getServiceId());
			entity.setBaseServiceId(notificationDTO.getServiceId()/10000);
			entity.setNotificationName(notificationDTO.getNotificationName());

			entity.setChannel(notificationDTO.getChannel().getValue());
			entity.setTaskId(notificationDTO.getTaskId());
			entity.setTriggerPointId(notificationDTO.getTriggerPointId());
			if(notificationDTO.getTriggerPointId()!=null && Integer.parseInt(notificationDTO.getTriggerPointId())==3) {
				entity.setTaskId(ApplicationConstants.SUBMISSION_NODE_ID);
			}
			entity.setConfigJson(notificationDTO);
			entity.setTenantId(userSession.getTenantId());

			entity = notificationRepository.save(entity);

			JSONObject response = new JSONObject();

			response.put("notificationId", entity.getNotificationId());

			response.put("status", true);
			response.put("message", message);

			return ResponseEntity.ok(response.toString());
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				NOTIF_MAPPING.error("Error : Save notification configuration data:", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> changeStatus(ChangeStatusDTO statusDTO, UserSessionDTO userSession) {
		try {
			JSONObject response = new JSONObject();
			NotificationConfiguration entity = notificationRepository.findById(statusDTO.getNotificationId())
						.orElseThrow(() -> new SPRuntimeError("Notification not found", HttpStatus.BAD_REQUEST));
			
			//Active
			if (statusDTO.getIsActive()) {
			    if (entity.getActive()) {
			        throw new SPRuntimeError("Notification is already active.", HttpStatus.BAD_REQUEST);
			    }
			    response.put("message", "Notification marked as active successfully.");
			} else {
			// Inactive
			    if (!entity.getActive()) {
			        throw new SPRuntimeError("Notification is already inactive.", HttpStatus.BAD_REQUEST);
			    }
			    List<ServiceMetadata> serviceMetadataList =
			            serviceMetadataRepo.findServiceByNotificationId(statusDTO.getNotificationId());

				if (!serviceMetadataList.isEmpty()) {

					String serviceNames = serviceMetadataList.stream()
							.map(service -> service.getMetadataJson().getServiceName()).filter(Objects::nonNull)
							.distinct().collect(Collectors.joining(", "));

					throw new SPRuntimeError(
							"Notification is already used in the following frozen service(s): " + serviceNames,
							HttpStatus.BAD_REQUEST);
				}
			    response.put("message", "Notification marked as inactive successfully.");
			    
			}

			entity.setActive(statusDTO.getIsActive());
			
			entity = notificationRepository.save(entity);
			response.put("notificationId", entity.getNotificationId());
			response.put("status", true);

			return ResponseEntity.ok(response.toString());
			
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				NOTIF_MAPPING.error("Error : Change Status notification configuration data:", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getNotification(Long notificationId, UserSessionDTO userSession) {

		return ResponseEntity.ok(notificationRepository.findByNotificationId(notificationId).orElse(null));
	}

	public ResponseEntity<?> deleteNotification(Long notificationId, UserSessionDTO userSession) {
		try {
			NotificationConfiguration entity = notificationRepository.findByNotificationId(notificationId)
					.orElseThrow(() -> new RuntimeException("Notification not found"));

			List<ServiceMetadata> serviceMetadataList = serviceMetadataRepo.findServiceByNotificationId(notificationId);
			if (!serviceMetadataList.isEmpty()) {

				String serviceNames = serviceMetadataList.stream()
						.map(service -> service.getMetadataJson().getServiceName()).filter(Objects::nonNull).distinct()
						.collect(Collectors.joining(", "));

				throw new SPRuntimeError("Notification is already used in the following frozen service(s): " + serviceNames, HttpStatus.BAD_REQUEST);
			}

			notificationRepository.delete(entity);
			JSONObject response = new JSONObject();
			response.put("message", "Notification Configuration delete.");
			response.put("status", true);

			return ResponseEntity.ok(response.toString());
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				ex.printStackTrace();
				NOTIF_MAPPING.error("Error : Delete Notification Configuration Details :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}

	public ResponseEntity<?> notificationList(Integer serviceId, String channel, UserSessionDTO userSession) {

		List<NotificationConfiguration> notifications;

		String tenantId = userSession.getTenantId();

		if (serviceId != null && channel != null && !channel.trim().isEmpty() && !"ALL".equalsIgnoreCase(channel)) {

			notifications = notificationRepository.findByTenantIdAndServiceIdAndChannel(tenantId,
					serviceId, channel);

		} else if (serviceId != null) {

			notifications = notificationRepository.findByTenantIdAndServiceId(tenantId, serviceId);

		} else if (channel != null && !channel.trim().isEmpty() && !"ALL".equalsIgnoreCase(channel)) {

			notifications = notificationRepository.findByTenantIdAndChannel(tenantId, channel);

		} else {

			notifications = notificationRepository.findByTenantId(tenantId);
		}

		List<NotificationListResponseDTO> response = new ArrayList<NotificationListResponseDTO>();

		for (NotificationConfiguration notification : notifications) {

			NotificationDTO config = notification.getConfigJson();
			NotificationListResponseDTO dto = new NotificationListResponseDTO();
			dto.setNotificationId(notification.getNotificationId());
			dto.setNotificationName(notification.getNotificationName());
			dto.setChannel(notification.getChannel());
			String status = notification.getActive() ? "ACTIVE" : "INACTIVE";
			dto.setStatus(status);

			if (config != null) {
				dto.setServiceId(config.getServiceId());
				dto.setServiceName(config.getServiceName());
				dto.setTriggerPointId(config.getTriggerPointId());
				dto.setTriggerPointName(config.getTriggerPointName());
				dto.setServiceVersions(config.getServiceVersions());
			}
			response.add(dto);
		}
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<?> notificationDetail(Long notificationId, UserSessionDTO userSession) {

		NotificationConfiguration notification = notificationRepository
				.findByNotificationIdAndTenantId(notificationId, userSession.getTenantId())
				.orElseThrow(() -> new RuntimeException("Notification not found"));
		NotificationReqDTO resDto = new NotificationReqDTO();
		resDto.setNotificationId(notification.getNotificationId());
		resDto.setNotificationDTO(notification.getConfigJson());
		return ResponseEntity.ok(resDto);
	}

}