package com.serviceplus.metadata.esign.service;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateRequestDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateResponseDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateStatusRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EsignTemplateService {

    EsignTemplateResponseDTO createTemplate(EsignTemplateRequestDTO request, MultipartFile pfxFile, UserSessionDTO user);

    List<EsignTemplateResponseDTO> getTemplates(String status, UserSessionDTO user);

    EsignTemplateResponseDTO getTemplateDetails(Long templateId, UserSessionDTO user);

    EsignTemplateResponseDTO updateTemplate(EsignTemplateRequestDTO request, MultipartFile pfxFile, UserSessionDTO user);

    EsignTemplateResponseDTO updateTemplateStatus(EsignTemplateStatusRequestDTO request, UserSessionDTO user);
}
