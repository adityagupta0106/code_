package com.serviceplus.metadata.pattern.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.pattern.dto.ServicePatternConfigurationDTO;
import com.serviceplus.metadata.pattern.dto.ServicePatternSummaryDTO;

public interface ServicePatternConfigurationService {

	ResponseEntity<?> saveOrUpdateServicePattern(ServicePatternConfigurationDTO dto, UserSessionDTO userSessionDetails);

	ServicePatternConfigurationDTO getServicePattern(Long id);

	List<ServicePatternSummaryDTO> getAllServicePatterns(Long serviceId, UserSessionDTO userSessionDetails);

	ResponseEntity<?> deleteServicePattern(Long id, UserSessionDTO userSessionDetails);
}
