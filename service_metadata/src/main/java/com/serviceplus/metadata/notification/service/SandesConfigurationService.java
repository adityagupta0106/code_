package com.serviceplus.metadata.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.notification.dto.ConfigCommonDTO;
import com.serviceplus.metadata.notification.dto.SandesConfigDTO;
import com.serviceplus.metadata.notification.dto.SandesConfigDTOReqDTO;
import com.serviceplus.metadata.notification.dto.SandesConfigResListDTO;
import com.serviceplus.metadata.notification.entity.NotificationProviderConfig;
import com.serviceplus.metadata.notification.enums.NotificationStatus;
import com.serviceplus.metadata.notification.enums.NotificationType;
import com.serviceplus.metadata.notification.repository.NotificationProviderConfigRepository;

@Service
public class SandesConfigurationService {
	private static final Logger NOTIF_TEMP_DESIGN = LogManager.getLogger("notifTempDesign");
	@Autowired
	private NotificationProviderConfigRepository configRepository;

	public ResponseEntity<?> upsertSandesProvider(UserSessionDTO userSessionDetails, SandesConfigDTOReqDTO dto) {
		try {
			NotificationProviderConfig notificationConfig = getSandesConfiguration(userSessionDetails, dto);
			notificationConfig = configRepository.save(notificationConfig);
			String message = dto.getId() == null ? "Sandes configuration saved successfully."
					: "Sandes configuration updated successfully.";
			return ResponseEntity.ok(Map.of("id",notificationConfig.getId(),"message", message));
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Insert/Update sandes-configuration : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}

	private NotificationProviderConfig getSandesConfiguration(UserSessionDTO userSessionDetails,
			SandesConfigDTOReqDTO dto) {
		if (dto.getId() == null) {
			NotificationProviderConfig configuration = new NotificationProviderConfig();
			configuration.setType(NotificationType.SANDES.getValue());

			ConfigCommonDTO commonDTO = new ConfigCommonDTO();
			commonDTO.setSandesConfigDTO(dto.getData());
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
				.orElseThrow(() -> new SPRuntimeError("Sandes configuration not found.", HttpStatus.NOT_FOUND));

		if (!configuration.getUserId().equals(userSessionDetails.getUserID())) {
			throw new SPRuntimeError("Unauthorized Edit Operation.", HttpStatus.UNAUTHORIZED);
		}

		if (!NotificationStatus.DEFINE.getValue().equals(configuration.getStatus())) {
			throw new SPRuntimeError("Active/Online Sandes configuration cannot be modified.", HttpStatus.CONFLICT);
		}

		configuration.getConfigDTO().setSandesConfigDTO(dto.getData());
		configuration.setuDate(new Date());
		return configuration;
	}

	public ResponseEntity<?> getNotificationProvider(UserSessionDTO userSessionDetails, Long id) {
		try {
			NotificationProviderConfig configuration = configRepository
					.findByIdAndUserIdAndType(id, userSessionDetails.getUserID(), NotificationType.SANDES.getValue())
					.orElseThrow(() -> new SPRuntimeError("Data not found", HttpStatus.BAD_REQUEST));
			SandesConfigDTOReqDTO dto = new SandesConfigDTOReqDTO();
			dto.setId(configuration.getId());
			dto.setData(configuration.getConfigDTO().getSandesConfigDTO());

			return ResponseEntity.ok(dto);
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Get sandes-providers : ", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getAllSandesProviderOfUser(UserSessionDTO userSessionDetails) {
		try {
			List<SandesConfigResListDTO> resList = new ArrayList<SandesConfigResListDTO>();

			List<NotificationProviderConfig> list = configRepository.findByUserIdAndType(userSessionDetails.getUserID(),
					NotificationType.SANDES.getValue());
			for (NotificationProviderConfig notificationConfig : list) {
				SandesConfigResListDTO dto = new SandesConfigResListDTO();
				SandesConfigDTO sandesConfigurationDTO = notificationConfig.getConfigDTO().getSandesConfigDTO();
				dto.setId(notificationConfig.getId());
				dto.setConfigurationName(sandesConfigurationDTO.getConfigurationName());
				String status = "";
				if(notificationConfig.getStatus().equals(NotificationStatus.DEFINE.getValue())) {
					status = "DEFINED";
				}else if(notificationConfig.getStatus().equals(NotificationStatus.ACTIVE.getValue())) {
					status = "ACTIVATED";
				}else if(notificationConfig.getStatus().equals(NotificationStatus.MOVETOPROD.getValue())) {
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
				NOTIF_TEMP_DESIGN.error("Error : Get all sandes-providers of user :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> getAllSandesProviderOfDeptWithStatus(UserSessionDTO userSessionDetails) {
		try {
			List<SandesConfigResListDTO> resList = new ArrayList<SandesConfigResListDTO>();

			List<NotificationProviderConfig> list = configRepository.findByTypeAndTenantIdAndDepartmentIdAndStatusIn(
					NotificationType.SANDES.getValue(), userSessionDetails.getTenantId(),
					userSessionDetails.getEntityId(),
					List.of(NotificationStatus.ACTIVE.getValue(), NotificationStatus.MOVETOPROD.getValue()));
			for (NotificationProviderConfig notificationConfig : list) {
				SandesConfigDTO configDTO = notificationConfig.getConfigDTO().getSandesConfigDTO();
				SandesConfigResListDTO dto = new SandesConfigResListDTO();
				dto.setId(notificationConfig.getId());
				dto.setConfigurationName(configDTO.getConfigurationName());
				String status = "";
				if(notificationConfig.getStatus().equals(NotificationStatus.DEFINE.getValue())) {
					status = "DEFINED";
				}else if(notificationConfig.getStatus().equals(NotificationStatus.ACTIVE.getValue())) {
					status = "ACTIVATED";
				}else if(notificationConfig.getStatus().equals(NotificationStatus.MOVETOPROD.getValue())) {
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
				NOTIF_TEMP_DESIGN.error("Error : Get all active sandes-providers of department :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> changeStatus(UserSessionDTO userSessionDetails, Long id) {
		try {
			NotificationProviderConfig notificationConfig = configRepository.findById(id)
					.orElseThrow(() -> new SPRuntimeError("Data not found.", HttpStatus.BAD_REQUEST));
			if (notificationConfig.getStatus().equals(NotificationStatus.DEFINE.getValue())) {
				notificationConfig.setStatus(NotificationStatus.ACTIVE.getValue());
				notificationConfig.setRemark("Configuration Actived By :" + userSessionDetails.getUserID());
			} else if (notificationConfig.getStatus().equals(NotificationStatus.ACTIVE.getValue())) {
				notificationConfig.setStatus(NotificationStatus.DEFINE.getValue());
			}
			configRepository.save(notificationConfig);

			return ResponseEntity.ok(Map.of("message", "Status update"));
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Sandes-providers change status :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<?> delete(UserSessionDTO userSessionDetails, Long id) {
		try {
			NotificationProviderConfig notificationConfig = configRepository.findById(id)
					.orElseThrow(() -> new SPRuntimeError("Data not found.", HttpStatus.BAD_REQUEST));
			if (!notificationConfig.getType().equals(NotificationType.SANDES.getValue())) {
				throw new SPRuntimeError("Bad Request.", HttpStatus.BAD_REQUEST);
			}
			if (!notificationConfig.getUserId().equals(userSessionDetails.getUserID())) {
				throw new SPRuntimeError("Unauthorized Access.", HttpStatus.UNAUTHORIZED);
			}
			configRepository.delete(notificationConfig);
			return ResponseEntity.ok(Map.of("message", "Deleted"));
		} catch (Exception ex) {
			if (ex instanceof SPRuntimeError) {
				throw ex;
			} else {
				NOTIF_TEMP_DESIGN.error("Error : Sandes-providers delete :", ex);
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

}
