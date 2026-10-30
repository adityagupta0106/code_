package com.serviceplus.metadata.notification.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.notification.dto.ConfigCommonDTO;
import com.serviceplus.metadata.notification.dto.SMSConfigResListDTO;
import com.serviceplus.metadata.notification.dto.SMSConfigurationDTO;
import com.serviceplus.metadata.notification.dto.SMSConfigurationReqDTO;
import com.serviceplus.metadata.notification.entity.NotificationProviderConfig;
import com.serviceplus.metadata.notification.enums.NotificationStatus;
import com.serviceplus.metadata.notification.enums.NotificationType;
import com.serviceplus.metadata.notification.enums.SmsServiceProvider;
import com.serviceplus.metadata.notification.repository.NotificationProviderConfigRepository;

@Service
public class SMSConfigurationService {

	private static final Logger NOTIF_TEMP_DESIGN = LogManager.getLogger("notifTempDesign");
	@Autowired
	private NotificationProviderConfigRepository configRepository;

	public ResponseEntity<?> getAllSMSProviderMaster() {
		try {
			List<String> masterList = Arrays.stream(SmsServiceProvider.values()).map(SmsServiceProvider::getValue)
					.collect(Collectors.toList());
			return ResponseEntity.ok(Map.of("master", masterList));
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error-Service : Get all sms-providers : {}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getNotificationProvider(UserSessionDTO userSessionDetails, Long id) {
		try {
			NotificationProviderConfig configuration = configRepository
					.findByIdAndUserIdAndType(id, userSessionDetails.getUserID(), NotificationType.SMS.getValue())
					.orElseThrow(() -> new SPRuntimeError("Data not found", HttpStatus.BAD_REQUEST));
			SMSConfigurationReqDTO dto = new SMSConfigurationReqDTO();
			dto.setId(configuration.getId());
			dto.setData(configuration.getConfigDTO().getSmsConfigurationDTO());

			return ResponseEntity.ok(dto);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Get sms-providers of user :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getAllSMSProviderOfUser(UserSessionDTO userSessionDetails) {
		try {
			List<SMSConfigResListDTO> resList = new ArrayList<SMSConfigResListDTO>();

			List<NotificationProviderConfig> list = configRepository.findByUserIdAndType(userSessionDetails.getUserID(), NotificationType.SMS.getValue());
			for (NotificationProviderConfig smsConfiguration : list) {
				SMSConfigResListDTO dto = new SMSConfigResListDTO();
				SMSConfigurationDTO smsConfigurationDTO = smsConfiguration.getConfigDTO().getSmsConfigurationDTO();
				dto.setId(smsConfiguration.getId());
				dto.setUrl(smsConfigurationDTO.getUrl());
				dto.setSmsProvider(smsConfigurationDTO.getSmsServiceProvider().getValue());
				dto.setSmsId(smsConfigurationDTO.getSmsId());
				String status = "";
				if(smsConfiguration.getStatus().equals(NotificationStatus.DEFINE.getValue())) {
					status = "DEFINED";
				}else if(smsConfiguration.getStatus().equals(NotificationStatus.ACTIVE.getValue())) {
					status = "ACTIVED";
				}else if(smsConfiguration.getStatus().equals(NotificationStatus.MOVETOPROD.getValue())) {
					status = "ONLINE";
				}
				dto.setStatus(status);
				resList.add(dto);
			}
			return ResponseEntity.ok(resList);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Get all sms-providers of user :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getAllSMSProviderOfDeptWithStatus(UserSessionDTO userSessionDetails) {
		try {
			List<SMSConfigResListDTO> resList = new ArrayList<SMSConfigResListDTO>();

			List<NotificationProviderConfig> list = configRepository.findByTypeAndTenantIdAndDepartmentIdAndStatusIn(
					NotificationType.SMS.getValue(), userSessionDetails.getTenantId(), userSessionDetails.getEntityId(),
					List.of(NotificationStatus.ACTIVE.getValue(), NotificationStatus.MOVETOPROD.getValue()));
			for (NotificationProviderConfig smsConfiguration : list) {
				SMSConfigurationDTO configDTO = smsConfiguration.getConfigDTO().getSmsConfigurationDTO();
				SMSConfigResListDTO dto = new SMSConfigResListDTO();
				dto.setId(smsConfiguration.getId());
				dto.setUrl(configDTO.getUrl());
				dto.setSmsProvider(configDTO.getSmsServiceProvider().getValue());
				String status = "";
				if(smsConfiguration.getStatus().equals(NotificationStatus.DEFINE.getValue())) {
					status = "DEFINED";
				}else if(smsConfiguration.getStatus().equals(NotificationStatus.ACTIVE.getValue())) {
					status = "ACTIVED";
				}else if(smsConfiguration.getStatus().equals(NotificationStatus.MOVETOPROD.getValue())) {
					status = "ONLINE";
				}
				dto.setStatus(status);
				resList.add(dto);
			}
			return ResponseEntity.ok(resList);

		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Get all active sms-providers of department :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> changeStatus(UserSessionDTO userSessionDetails, Long id) {
		try {
			NotificationProviderConfig smsConfiguration = configRepository.findById(id)
					.orElseThrow(() -> new SPRuntimeError("Data not found.", HttpStatus.BAD_REQUEST));
			if (smsConfiguration.getStatus().equals(NotificationStatus.DEFINE.getValue())) {
				smsConfiguration.setStatus(NotificationStatus.ACTIVE.getValue());
				smsConfiguration.setRemark("Configuration Actived By :"+userSessionDetails.getUserID());
			} else if (smsConfiguration.getStatus().equals(NotificationStatus.ACTIVE.getValue())) {
				smsConfiguration.setStatus(NotificationStatus.DEFINE.getValue());
			}
			configRepository.save(smsConfiguration);

			return ResponseEntity.ok(Map.of("message", "Status update"));
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : SMS-providers change status :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> delete(UserSessionDTO userSessionDetails, Long id) {
		try {
			NotificationProviderConfig smsConfiguration = configRepository
					.findById(id)
					.orElseThrow(() -> new SPRuntimeError("Data not found.", HttpStatus.BAD_REQUEST));
			if (!smsConfiguration.getUserId().equals(userSessionDetails.getUserID())) {
				throw new SPRuntimeError("Unauthorized Access.", HttpStatus.UNAUTHORIZED);
			}
			if (!smsConfiguration.getType().equals(NotificationType.SMS.getValue())) {
				throw new SPRuntimeError("Bad Request.", HttpStatus.BAD_REQUEST);
			}
			configRepository.delete(smsConfiguration);
			return ResponseEntity.ok(Map.of("message", "Deleted"));
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : SMS-providers delete :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> upsertSmsProvider(UserSessionDTO userSessionDetails, SMSConfigurationReqDTO dto) {
		try {
			NotificationProviderConfig smsConfiguration = getSmsConfiguration(userSessionDetails, dto);
			smsConfiguration = configRepository.save(smsConfiguration);
			String message = dto.getId() == null ? "SMS configuration saved successfully."
					: "SMS configuration updated successfully.";
			return ResponseEntity.ok(Map.of("id", smsConfiguration.getId(), "message", message));
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error-Service : Insert/Update sms-configuration :}", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	private NotificationProviderConfig getSmsConfiguration(UserSessionDTO userSessionDetails, SMSConfigurationReqDTO dto) {
		validate(dto.getData());
		if (dto.getId() == null) {
			NotificationProviderConfig configuration = new NotificationProviderConfig();
			configuration.setType(NotificationType.SMS.getValue());

			ConfigCommonDTO commonDTO = new ConfigCommonDTO();
			commonDTO.setSmsConfigurationDTO(dto.getData());
			configuration.setConfigDTO(commonDTO);
			
			configuration.setUserId(userSessionDetails.getUserID());
			configuration.setcDate(new Date());
			
			configuration.setTenantId(userSessionDetails.getTenantId());
			configuration.setStateId(userSessionDetails.getClcId());
			configuration.setDepartmentId(userSessionDetails.getEntityId());		
			configuration.setLocationId(userSessionDetails.getLocationId());
			configuration.setStatus(NotificationStatus.DEFINE.getValue());
			
			return configuration;
		}
		
		NotificationProviderConfig configuration = configRepository.findById(dto.getId())
				.orElseThrow(() -> new SPRuntimeError("SMS configuration not found.", HttpStatus.NOT_FOUND));
		
		if(!configuration.getUserId().equals(userSessionDetails.getUserID())) {
			throw new SPRuntimeError("Unauthorized Edit Operation.", HttpStatus.UNAUTHORIZED);
		}

		if (!NotificationStatus.DEFINE.getValue().equals(configuration.getStatus())) {
			throw new SPRuntimeError("Active/Online SMS configuration cannot be modified.", HttpStatus.CONFLICT);
		}

		configuration.getConfigDTO().setSmsConfigurationDTO(dto.getData());
		configuration.setuDate(new Date());
		return configuration;
	}

	private void validate(SMSConfigurationDTO dto) {
		switch (dto.getSmsServiceProvider()) {
		case NIC:
			requireFields(Map.of("SMS ID", dto.getSmsId(), "Signature ID", dto.getSignatureId(), "DLT Entity ID",
					dto.getDltEntityId()));
			validatePassword(dto);
			break;
		case CDAC:
			requireFields(Map.of("SMS ID", dto.getSmsId(), "Signature ID", dto.getSignatureId(), "Secure Key",
					dto.getSecureKey()));
			validatePassword(dto);
			break;
		case MSDG:
			requireFields(Map.of("User Name", dto.getUserName(), "Sender ID", dto.getSenderId(), "API Service Key",
					dto.getApiServiceKey()));
			validatePassword(dto);
			break;
		case AMTRON:
			requireFields(Map.of("SMS ID", dto.getSmsId(), "Signature ID", dto.getSignatureId()));
			validatePassword(dto);
			break;
		case BSNL:
			requireFields(Map.of("User Name", dto.getUserName(), "Sender ID", dto.getSenderId(), "Secure Key",
					dto.getSecureKey(), "DLT Entity ID", dto.getDltEntityId()));
			break;
		default:
			throw new SPRuntimeError("Invalid SMS Service Provider.", HttpStatus.BAD_REQUEST);
		}
	}

	private void validatePassword(SMSConfigurationDTO dto) {
		require(dto.getAuthenticationPassword(), "Authentication Password");
		require(dto.getConfirmAuthenticationPassword(), "Confirm Authentication Password");

		if (!Objects.equals(dto.getAuthenticationPassword(), dto.getConfirmAuthenticationPassword())) {
			throw new SPRuntimeError("Authentication Password and Confirm Authentication Password do not match.",
					HttpStatus.BAD_REQUEST);
		}
	}

	private void requireFields(Map<String, String> fields) {
		fields.forEach(this::require);
	}

	private void require(String fieldName, String value) {
		if (!StringUtils.hasText(value)) {
			throw new SPRuntimeError(fieldName + " is required.", HttpStatus.BAD_REQUEST);
		}
	}
}
