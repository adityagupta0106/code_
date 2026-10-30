package com.serviceplus.metadata.departmentConfig.service;

import com.serviceplus.metadata.departmentConfig.dto.*;
import com.serviceplus.metadata.departmentConfig.entity.BsaConfiguration;
import com.serviceplus.metadata.dto.UserSessionDTO;

import java.util.List;

public interface BsaServiceMappingService {

    BsaServiceMappingDTO getMapping(Integer serviceId, UserSessionDTO userSession);

    BsaServiceMappingDTO saveMapping(BsaServiceMappingRequestDTO request, UserSessionDTO userSession);

    List<BsaConfigurationResponseDTO> getAvailableConfigurations(UserSessionDTO userSession);

    BsaServiceTaskAttributeDTO getServiceTasksAndAttributes(Integer serviceId, UserSessionDTO userSession);

    BsaServiceMappingDTO buildBsaServiceMappingDTO(Integer serviceId, String tenantId);

    BsaConfiguration findBsaConfigurationById(Long bsaConfigId);
}
