package com.serviceplus.tracking.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.MessageBoxResponse;
import com.serviceplus.tracking.dto.OfficeDetailsDTO;
import com.serviceplus.tracking.dto.OfficialIntimationKafkaDTO;
import com.serviceplus.tracking.dto.ServerSidePaginationRecord;
import com.serviceplus.tracking.dto.UserSessionDTO;
import com.serviceplus.tracking.entity.MessageBox;
import com.serviceplus.tracking.repository.MessageBoxRepository;
import com.serviceplus.tracking.utility.CommonUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MessageBoxService {

	private static final Logger log = LogManager.getLogger("messageBoxLogger");

	private static final int MAX_PER_PAGE_COUNT = 50;

    private final MessageBoxRepository messageBoxRepository;
    private final WorkflowInboxRedisService workflowInboxRedisService;

    public MessageBoxService(MessageBoxRepository messageBoxRepository,
                             WorkflowInboxRedisService workflowInboxRedisService) {
        this.messageBoxRepository = messageBoxRepository;
        this.workflowInboxRedisService = workflowInboxRedisService;
    }

	public ServerSidePaginationRecord<MessageBoxResponse> getMessages(HttpServletRequest request, int page, int size,
			Integer serviceId, Boolean isRead, String applRefNo) {
		try {
			UserSessionDTO session = CommonUtil.getUserSessionDetails(request);
			if (session == null) {
				throw new SPRuntimeError("Invalid Session", HttpStatus.UNAUTHORIZED);
			}
			Set<Object> holderTokens = workflowInboxRedisService.getTokensByUserAndLocation(session.getUserID(),
					String.valueOf(session.getLocationId()));
			ServerSidePaginationRecord<MessageBoxResponse> response = new ServerSidePaginationRecord<>();
			if (holderTokens == null || holderTokens.isEmpty()) {
				response.setData(Collections.emptyList());
				response.setCurrentPage(page);
				response.setPageSize(size);
				response.setTotalPages(0);
				response.setTotalRecords(0);
				return response;
			}

			List<String> holderIds = holderTokens.stream().map(Object::toString).toList();

			Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PER_PAGE_COUNT),
					Sort.by(Sort.Direction.DESC, "createdOn"));

			Page<MessageBox> messagePage = fetchMessages(holderIds, serviceId, isRead, applRefNo, pageable,session.getTenantId());

			response.setData(messagePage.getContent().stream().map(this::buildResponse).toList());

			response.setCurrentPage(messagePage.getNumber());
			response.setPageSize(messagePage.getSize());
			response.setTotalPages(messagePage.getTotalPages());
			response.setTotalRecords(messagePage.getTotalElements());

			return response;
		} catch (SPRuntimeError ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to get Messages {} ", ex);
			throw new SPRuntimeError("Exception while get messages", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Page<MessageBox> fetchMessages(List<String> holderIds, Integer serviceId, Boolean isRead, String applRefNo,
			Pageable pageable, String tenantId) {
		try {

			if (StringUtils.hasText(applRefNo)) {
				return messageBoxRepository.findByHolderIdInAndTenantIdAndApplRefNoContainingIgnoreCaseAndClearedFalse(
						holderIds, tenantId, applRefNo.trim(), pageable);
			}

			if (serviceId != null && isRead != null) {
				return messageBoxRepository.findByHolderIdInAndTenantIdAndServiceIdAndIsReadAndClearedFalse(holderIds,
						tenantId, serviceId, isRead, pageable);
			}

			if (serviceId != null) {
				return messageBoxRepository.findByHolderIdInAndTenantIdAndServiceIdAndClearedFalse(holderIds, tenantId,
						serviceId, pageable);
			}

			if (isRead != null) {
				return messageBoxRepository.findByHolderIdInAndTenantIdAndIsReadAndClearedFalse(holderIds, tenantId,
						isRead, pageable);
			}

			return messageBoxRepository.findByHolderIdInAndTenantIdAndClearedFalse(holderIds, tenantId, pageable);

		} catch (SPRuntimeError ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to fetchMessages {}", ex);
			throw new SPRuntimeError("Unable to fetchMessages", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public MessageBoxResponse getMessage(Long messageId, HttpServletRequest request) {
		try {
			UserSessionDTO session = CommonUtil.getUserSessionDetails(request);
			List<String> holderIds = workflowInboxRedisService
					.getTokensByUserAndLocation(session.getUserID(), String.valueOf(session.getLocationId())).stream()
					.map(Object::toString).toList();
			MessageBox message = messageBoxRepository.findByMessageIdAndTenantIdAndHolderIdIn(messageId,session.getTenantId(), holderIds)
					.orElseThrow(() -> new SPRuntimeError("Message not found", HttpStatus.NOT_FOUND));
			if (!Boolean.TRUE.equals(message.getIsRead())) {
				message.setIsRead(true);
				message.setReadOn(new Date());
				messageBoxRepository.save(message);
			}
			return buildResponse(message);
		} catch (SPRuntimeError ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to getMessage {} ", messageId, ex);
			throw new SPRuntimeError("Unable to mark message as read", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public void markRead(Long messageId, HttpServletRequest request) {
		try {
			UserSessionDTO session = CommonUtil.getUserSessionDetails(request);
			List<String> holderIds = workflowInboxRedisService
					.getTokensByUserAndLocation(session.getUserID(), String.valueOf(session.getLocationId())).stream()
					.map(Object::toString).toList();
			MessageBox message = messageBoxRepository.findByMessageIdAndTenantIdAndHolderIdIn(messageId,session.getTenantId(),holderIds)
					.orElseThrow(() -> new SPRuntimeError("Message not found", HttpStatus.NOT_FOUND));
			message.setIsRead(true);
			message.setReadOn(new Date());
			messageBoxRepository.save(message);
		} catch (SPRuntimeError ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Unable to mark message {} as read", messageId, ex);
			throw new SPRuntimeError("Unable to mark message as read", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void clear(Long messageId, HttpServletRequest request) {
		UserSessionDTO session = CommonUtil.getUserSessionDetails(request);
		List<String> holderIds = workflowInboxRedisService
				.getTokensByUserAndLocation(session.getUserID(), String.valueOf(session.getLocationId())).stream()
				.map(Object::toString).toList();
		MessageBox message = messageBoxRepository.findByMessageIdAndTenantIdAndHolderIdIn(messageId,session.getTenantId(), holderIds)
				.orElseThrow(() -> new SPRuntimeError("Message not found", HttpStatus.NOT_FOUND));
		message.setCleared(true);
		message.setClearedOn(new Date());
		messageBoxRepository.save(message);
	}

	public Long getUnreadCount(HttpServletRequest request) {
		UserSessionDTO session = CommonUtil.getUserSessionDetails(request);
		List<String> holderIds = workflowInboxRedisService
				.getTokensByUserAndLocation(session.getUserID(), String.valueOf(session.getLocationId())).stream()
				.map(Object::toString).toList();
		return messageBoxRepository.countByHolderIdInAndIsReadFalseAndClearedFalse(holderIds);
	}

    private MessageBoxResponse buildResponse(MessageBox entity) {

        MessageBoxResponse response = new MessageBoxResponse();
        response.setMessageId(entity.getMessageId());
        response.setApplicationId(entity.getApplicationId());
        response.setApplicationRefNo(entity.getApplicationRefNo());

        response.setServiceId(entity.getServiceId());
        response.setServiceName(entity.getServiceName());

        response.setTaskId(entity.getCurrentTaskId());
        response.setTaskName(entity.getCurrentTaskName());

        response.setAssociatedTaskId(entity.getAssociatedTaskId());
        response.setCreatedOn(entity.getCreatedOn());

        return response;
    }


	public void persistMessage(OfficialIntimationKafkaDTO dto) {
		if (dto == null || dto.getAllowedOffices() == null || dto.getAllowedOffices().isEmpty()) {
			return;
		}
		List<MessageBox> messages = new ArrayList<>();
		for (OfficeDetailsDTO.OfficeUnitData office : dto.getAllowedOffices()) {
			if (office.getHolderIds() == null || office.getHolderIds().isEmpty()) {
				continue;
			}
			for (String holderId : office.getHolderIds()) {
				MessageBox box = new MessageBox();
				box.setApplicationId(dto.getApplicationId());
				box.setApplicationRefNo(dto.getApplicationRefNo());
				box.setServiceId(dto.getServiceId());
				box.setBaseServiceId(dto.getBaseServiceId());
				box.setAssociatedTaskId(dto.getAssociatedTaskId());
				box.setProcessId(dto.getCurrentProcessId());
				box.setCurrentTaskId(dto.getCurrentTaskId());
				box.setCurrentTaskName(dto.getCurrentTaskName());
				box.setServiceName(dto.getServiceName());
				box.setHolderId(holderId);
				box.setLocationId(office.getOrgUnitCode());
				box.setAllowApplicationView(dto.getAllowApplicationView());
				box.setAllowHistoryView(dto.getAllowHistoryView());
				box.setAutoClear(dto.getAutoClear());
				box.setIsRead(Boolean.FALSE);
				box.setCleared(Boolean.FALSE);
				box.setCreatedOn(new Date());
				box.setTenantId(dto.getTenantId());
				messages.add(box);
			}
		}
		if (!messages.isEmpty()) {
			messageBoxRepository.saveAll(messages);
			log.info("Saved {} message box entries for application {}", messages.size(), dto.getApplicationId());
		}
	}
}
