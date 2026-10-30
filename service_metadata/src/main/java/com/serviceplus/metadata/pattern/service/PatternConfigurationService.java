package com.serviceplus.metadata.pattern.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.pattern.dto.PatternMasterResponseDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateDetailsResponseDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplatePageDTO;
import com.serviceplus.metadata.pattern.dto.PatternTemplateSummaryDTO;

public interface PatternConfigurationService {

	ResponseEntity<?> savePatternTemplate(PatternTemplateDTO dto, UserSessionDTO userSessionDetails);

	PatternTemplateDTO getPatternTemplateById(Long id);

	PatternTemplateDetailsResponseDTO getPatternTemplateAttributesById(Long id);

	PatternTemplatePageDTO getAllPatternTemplates(Integer page, Integer size, UserSessionDTO userSessionDetails);

	List<PatternTemplateSummaryDTO> getAllPatternTemplatesByDept(UserSessionDTO userSessionDetails);

	ResponseEntity<?> delete(Long id);

	PatternMasterResponseDTO getMasters();
}
