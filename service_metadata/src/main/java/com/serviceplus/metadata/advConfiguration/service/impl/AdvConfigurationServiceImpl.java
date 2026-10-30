package com.serviceplus.metadata.advConfiguration.service.impl;

import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationDeleteRequestDTO;
import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationResponseDTO;
import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationSaveRequestDTO;
import com.serviceplus.metadata.advConfiguration.entity.AdvConfiguration;
import com.serviceplus.metadata.advConfiguration.repository.AdvConfigurationRepository;
import com.serviceplus.metadata.advConfiguration.service.AdvConfigurationService;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdvConfigurationServiceImpl implements AdvConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(AdvConfigurationServiceImpl.class);

    private final AdvConfigurationRepository repository;

    public AdvConfigurationServiceImpl(AdvConfigurationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public AdvConfigurationResponseDTO saveConfiguration(AdvConfigurationSaveRequestDTO requestDTO, UserSessionDTO userSession) {
        if (requestDTO == null) {
            throw new SPRuntimeError("AdvConfigurationSaveRequestDTO request body cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(requestDTO.getEnrollApplId()) || isEmpty(requestDTO.getAuthKey()) || isEmpty(requestDTO.getUsername())) {
            throw new SPRuntimeError("enrollApplId, authKey, and username are required fields", HttpStatus.BAD_REQUEST);
        }

        AdvConfiguration target;

        if (requestDTO.getAdvConfigId() != null) {
            Optional<AdvConfiguration> existingOpt = repository.findById(requestDTO.getAdvConfigId());
            if (existingOpt.isPresent()) {
                target = existingOpt.get();
                copyDtoToEntity(requestDTO, target);
            } else {
                target = convertDtoToEntity(requestDTO);
                target.setCDate(new Date());
                if (userSession != null) {
                    target.setCByUserId(userSession.getUserID());
                }
            }
        } else if (requestDTO.getServiceId() != null) {
            Optional<AdvConfiguration> existingOpt = repository.findFirstByServiceId(requestDTO.getServiceId());
            if (existingOpt.isPresent()) {
                target = existingOpt.get();
                copyDtoToEntity(requestDTO, target);
            } else {
                target = convertDtoToEntity(requestDTO);
                target.setCDate(new Date());
                if (userSession != null) {
                    target.setCByUserId(userSession.getUserID());
                }
            }
        } else {
            target = convertDtoToEntity(requestDTO);
            target.setCDate(new Date());
            if (userSession != null) {
                target.setCByUserId(userSession.getUserID());
            }
        }

        target.setUDate(new Date());
        if (userSession != null && userSession.getUserID() != null) {
            target.setUByUserId(userSession.getUserID());
        }

        log.info("Saving AdvConfiguration for serviceId: {}, baseServiceId: {}", target.getServiceId(), target.getBaseServiceId());
        AdvConfiguration saved = repository.save(target);
        return AdvConfigurationResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public AdvConfiguration saveConfiguration(AdvConfiguration config, UserSessionDTO userSession) {
        if (config == null) {
            throw new SPRuntimeError("AdvConfiguration request body cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (isEmpty(config.getEnrollApplId()) || isEmpty(config.getAuthKey()) || isEmpty(config.getUsername())) {
            throw new SPRuntimeError("enrollApplId, authKey, and username are required fields", HttpStatus.BAD_REQUEST);
        }

        AdvConfiguration target;

        if (config.getAdvConfigId() != null) {
            Optional<AdvConfiguration> existingOpt = repository.findById(config.getAdvConfigId());
            if (existingOpt.isPresent()) {
                target = existingOpt.get();
                copyProperties(config, target);
            } else {
                target = config;
                if (target.getCDate() == null) {
                    target.setCDate(new Date());
                }
                if (target.getCByUserId() == null && userSession != null) {
                    target.setCByUserId(userSession.getUserID());
                }
            }
        } else if (config.getServiceId() != null) {
            Optional<AdvConfiguration> existingOpt = repository.findFirstByServiceId(config.getServiceId());
            if (existingOpt.isPresent()) {
                target = existingOpt.get();
                copyProperties(config, target);
            } else {
                target = config;
                if (target.getCDate() == null) {
                    target.setCDate(new Date());
                }
                if (userSession != null) {
                    target.setCByUserId(userSession.getUserID());
                }
            }
        } else {
            target = config;
            if (target.getCDate() == null) {
                target.setCDate(new Date());
            }
            if (target.getCByUserId() == null && userSession != null) {
                target.setCByUserId(userSession.getUserID());
            }
        }

        target.setUDate(new Date());
        if (userSession != null && userSession.getUserID() != null) {
            target.setUByUserId(userSession.getUserID());
        } else if (config.getUByUserId() != null) {
            target.setUByUserId(config.getUByUserId());
        }

        log.info("Saving AdvConfiguration entity for serviceId: {}, baseServiceId: {}", target.getServiceId(), target.getBaseServiceId());
        return repository.save(target);
    }

    @Override
    public List<AdvConfigurationResponseDTO> getConfigurationsByServiceId(Integer serviceId) {
        if (serviceId == null) {
            throw new SPRuntimeError("serviceId query parameter cannot be null", HttpStatus.BAD_REQUEST);
        }
        log.info("Fetching AdvConfiguration by serviceId: {}", serviceId);
        List<AdvConfiguration> list = repository.findByServiceId(serviceId);
        return list.stream().map(AdvConfigurationResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<AdvConfigurationResponseDTO> getConfigurationsByBaseServiceId(Integer baseServiceId) {
        if (baseServiceId == null) {
            throw new SPRuntimeError("baseServiceId query parameter cannot be null", HttpStatus.BAD_REQUEST);
        }
        log.info("Fetching AdvConfiguration by baseServiceId: {}", baseServiceId);
        List<AdvConfiguration> list = repository.findByBaseServiceId(baseServiceId);
        return list.stream().map(AdvConfigurationResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    public AdvConfigurationResponseDTO getConfigurationById(Long advConfigId) {
        if (advConfigId == null) {
            throw new SPRuntimeError("advConfigId query parameter cannot be null", HttpStatus.BAD_REQUEST);
        }
        log.info("Fetching AdvConfiguration by advConfigId: {}", advConfigId);
        AdvConfiguration entity = repository.findById(advConfigId)
                .orElseThrow(() -> new SPRuntimeError("AdvConfiguration not found for id: " + advConfigId, HttpStatus.NOT_FOUND));
        return AdvConfigurationResponseDTO.fromEntity(entity);
    }

    @Override
    public List<AdvConfigurationResponseDTO> getAllConfigurations(int page, int size) {
        int pageNumber = Math.max(page, 0);
        int pageSize = size > 0 ? size : 20;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "advConfigId"));
        Page<AdvConfiguration> resultPage = repository.findAll(pageRequest);
        return resultPage.getContent().stream().map(AdvConfigurationResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int deleteConfiguration(AdvConfigurationDeleteRequestDTO deleteDTO, UserSessionDTO userSession) {
        if (deleteDTO == null) {
            throw new SPRuntimeError("Delete request body cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (deleteDTO.getAdvConfigId() != null) {
            deleteConfigurationById(deleteDTO.getAdvConfigId());
            return 1;
        }

        if (deleteDTO.getServiceId() != null) {
            Long userId = deleteDTO.getUserId();
            if (userId == null && userSession != null) {
                userId = userSession.getUserID();
            }
            if (userId != null) {
                return deleteConfigurationByServiceIdAndUserId(deleteDTO.getServiceId(), userId);
            } else {
                return repository.deleteByServiceId(deleteDTO.getServiceId());
            }
        }

        throw new SPRuntimeError("Either advConfigId or serviceId must be provided for deletion", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Transactional
    public int deleteConfigurationByServiceIdAndUserId(Integer serviceId, Long userId) {
        if (serviceId == null || userId == null) {
            throw new SPRuntimeError("serviceId and userId cannot be null", HttpStatus.BAD_REQUEST);
        }
        log.info("Deleting AdvConfiguration for serviceId: {} and userId: {}", serviceId, userId);
        return repository.deleteByServiceIdAndUserId(serviceId, userId);
    }

    @Override
    @Transactional
    public void deleteConfigurationById(Long advConfigId) {
        if (advConfigId == null) {
            throw new SPRuntimeError("advConfigId cannot be null", HttpStatus.BAD_REQUEST);
        }
        log.info("Deleting AdvConfiguration by advConfigId: {}", advConfigId);
        if (!repository.existsById(advConfigId)) {
            throw new SPRuntimeError("AdvConfiguration not found for id: " + advConfigId, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(advConfigId);
    }

    private AdvConfiguration convertDtoToEntity(AdvConfigurationSaveRequestDTO dto) {
        AdvConfiguration entity = new AdvConfiguration();
        entity.setAdvConfigId(dto.getAdvConfigId());
        copyDtoToEntity(dto, entity);
        return entity;
    }

    private void copyDtoToEntity(AdvConfigurationSaveRequestDTO source, AdvConfiguration target) {
        target.setEnrollApplId(source.getEnrollApplId());
        target.setAuthKey(source.getAuthKey());
        target.setUsername(source.getUsername());
        if (source.getPassword() != null) {
            target.setPassword(source.getPassword());
        }
        if (source.getxApiKey() != null) {
            target.setxApiKey(source.getxApiKey());
        }
        if (source.getAuthKeyStaging() != null) {
            target.setAuthKeyStaging(source.getAuthKeyStaging());
        }
        if (source.getUsernameStaging() != null) {
            target.setUsernameStaging(source.getUsernameStaging());
        }
        if (source.getPasswordStaging() != null) {
            target.setPasswordStaging(source.getPasswordStaging());
        }
        if (source.getxApiKeyStaging() != null) {
            target.setxApiKeyStaging(source.getxApiKeyStaging());
        }
        if (source.getAuthenticateUrlStaging() != null) {
            target.setAuthenticateUrlStaging(source.getAuthenticateUrlStaging());
        }
        if (source.getTokenizeUrlStaging() != null) {
            target.setTokenizeUrlStaging(source.getTokenizeUrlStaging());
        }
        if (source.getDetokenizeUrlStaging() != null) {
            target.setDetokenizeUrlStaging(source.getDetokenizeUrlStaging());
        }
        if (source.getAuthenticateUrlProd() != null) {
            target.setAuthenticateUrlProd(source.getAuthenticateUrlProd());
        }
        if (source.getTokenizeUrlProd() != null) {
            target.setTokenizeUrlProd(source.getTokenizeUrlProd());
        }
        if (source.getDetokenizeUrlProd() != null) {
            target.setDetokenizeUrlProd(source.getDetokenizeUrlProd());
        }
        if (source.getServiceId() != null) {
            target.setServiceId(source.getServiceId());
        }
        if (source.getBaseServiceId() != null) {
            target.setBaseServiceId(source.getBaseServiceId());
        }
    }

    private void copyProperties(AdvConfiguration source, AdvConfiguration target) {
        target.setEnrollApplId(source.getEnrollApplId());
        target.setAuthKey(source.getAuthKey());
        target.setUsername(source.getUsername());
        if (source.getPassword() != null) {
            target.setPassword(source.getPassword());
        }
        if (source.getxApiKey() != null) {
            target.setxApiKey(source.getxApiKey());
        }
        if (source.getAuthKeyStaging() != null) {
            target.setAuthKeyStaging(source.getAuthKeyStaging());
        }
        if (source.getUsernameStaging() != null) {
            target.setUsernameStaging(source.getUsernameStaging());
        }
        if (source.getPasswordStaging() != null) {
            target.setPasswordStaging(source.getPasswordStaging());
        }
        if (source.getxApiKeyStaging() != null) {
            target.setxApiKeyStaging(source.getxApiKeyStaging());
        }
        if (source.getAuthenticateUrlStaging() != null) {
            target.setAuthenticateUrlStaging(source.getAuthenticateUrlStaging());
        }
        if (source.getTokenizeUrlStaging() != null) {
            target.setTokenizeUrlStaging(source.getTokenizeUrlStaging());
        }
        if (source.getDetokenizeUrlStaging() != null) {
            target.setDetokenizeUrlStaging(source.getDetokenizeUrlStaging());
        }
        if (source.getAuthenticateUrlProd() != null) {
            target.setAuthenticateUrlProd(source.getAuthenticateUrlProd());
        }
        if (source.getTokenizeUrlProd() != null) {
            target.setTokenizeUrlProd(source.getTokenizeUrlProd());
        }
        if (source.getDetokenizeUrlProd() != null) {
            target.setDetokenizeUrlProd(source.getDetokenizeUrlProd());
        }
        if (source.getServiceId() != null) {
            target.setServiceId(source.getServiceId());
        }
        if (source.getBaseServiceId() != null) {
            target.setBaseServiceId(source.getBaseServiceId());
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
