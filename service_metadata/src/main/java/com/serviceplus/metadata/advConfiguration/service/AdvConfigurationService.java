package com.serviceplus.metadata.advConfiguration.service;

import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationDeleteRequestDTO;
import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationResponseDTO;
import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationSaveRequestDTO;
import com.serviceplus.metadata.advConfiguration.entity.AdvConfiguration;
import com.serviceplus.metadata.dto.UserSessionDTO;

import java.util.List;

public interface AdvConfigurationService {

    AdvConfigurationResponseDTO saveConfiguration(AdvConfigurationSaveRequestDTO requestDTO, UserSessionDTO userSession);

    AdvConfiguration saveConfiguration(AdvConfiguration config, UserSessionDTO userSession);

    List<AdvConfigurationResponseDTO> getConfigurationsByServiceId(Integer serviceId);

    List<AdvConfigurationResponseDTO> getConfigurationsByBaseServiceId(Integer baseServiceId);

    AdvConfigurationResponseDTO getConfigurationById(Long advConfigId);

    List<AdvConfigurationResponseDTO> getAllConfigurations(int page, int size);

    int deleteConfiguration(AdvConfigurationDeleteRequestDTO deleteDTO, UserSessionDTO userSession);

    int deleteConfigurationByServiceIdAndUserId(Integer serviceId, Long userId);

    void deleteConfigurationById(Long advConfigId);
}
