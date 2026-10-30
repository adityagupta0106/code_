package com.serviceplus.metadata.esign.service;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.esign.dto.EsignAadhaarFieldOptionDTO;
import com.serviceplus.metadata.esign.dto.EsignTemplateResponseDTO;
import com.serviceplus.metadata.esign.dto.ServiceEsignConfigurationDTO;
import com.serviceplus.metadata.esign.dto.ServiceEsignToggleDTO;

import java.util.List;

public interface ServiceEsignConfigurationService {

    List<EsignTemplateResponseDTO> getEligibleTemplates(Integer serviceId, UserSessionDTO user);

    ServiceEsignConfigurationDTO getConfiguration(Integer serviceId, UserSessionDTO user);

    ServiceEsignConfigurationDTO saveConfiguration(ServiceEsignConfigurationDTO request, UserSessionDTO user);

    ServiceEsignConfigurationDTO updateConfiguration(ServiceEsignConfigurationDTO request, UserSessionDTO user);

    ServiceEsignConfigurationDTO toggleEsign(ServiceEsignToggleDTO request, UserSessionDTO user);

    List<EsignAadhaarFieldOptionDTO> getEligibleAadhaarFields(Integer serviceId, UserSessionDTO user);
}
