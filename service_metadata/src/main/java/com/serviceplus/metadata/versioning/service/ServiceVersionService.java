package com.serviceplus.metadata.versioning.service;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.versioning.dto.StartModificationResponse;
import com.serviceplus.metadata.versioning.enums.ServiceComponent;
import com.serviceplus.metadata.versioning.enums.VersionType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ServiceVersionService {

    StartModificationResponse startModification(Integer serviceId, VersionType versionType, UserSessionDTO userSessionDetails);

    void validateModification(String modificationToken, ServiceComponent component, UserSessionDTO userSessionDetails);

    Optional<com.serviceplus.metadata.versioning.entity.ServiceVersionChange> findActiveModification(Integer serviceId);

    boolean isMinorModification(Integer serviceId);

    void validateComponentAllowed(Integer serviceId, ServiceComponent component);

    void cancelModification(String modificationToken, Integer serviceId, UserSessionDTO userSessionDetails);

    void completeModificationFreeze(Integer serviceId);

    Map<String, Object> copyService(Integer sourceServiceId, String newServiceName, String newServiceAbbreviation, UserSessionDTO userSessionDetails);

    Map<String, Object> copyService(Integer sourceServiceId, String newServiceName, String newServiceAbbreviation, Set<ServiceComponent> excludedComponents, UserSessionDTO userSessionDetails);
}