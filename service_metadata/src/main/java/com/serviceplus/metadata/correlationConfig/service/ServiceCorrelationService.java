package com.serviceplus.metadata.correlationConfig.service;

import org.springframework.http.ResponseEntity;

import com.serviceplus.metadata.correlationConfig.dto.ServiceCorrelationRequestDTO;
import com.serviceplus.metadata.dto.UserSessionDTO;

public interface ServiceCorrelationService {

	ResponseEntity<?> save(ServiceCorrelationRequestDTO requestDTO, UserSessionDTO userSessionDetails);

	ResponseEntity<?> delete(Integer id, UserSessionDTO userSessionDetails);

	ResponseEntity<?> getAll(UserSessionDTO userSessionDetails,Integer serviceId, int page, int size);

	ResponseEntity<?> getById(Integer id, UserSessionDTO userSessionDetails);

	ResponseEntity<?> getAllBasedOnClcAndTenant(UserSessionDTO userSessionDetails, int page, int size);

}
