package com.serviceplus.tracking.service;

import com.serviceplus.tracking.dto.WorkflowAssignmentKafkaEvent;
import com.serviceplus.tracking.feignClient.ServiceMetadataFeignClient;
import com.serviceplus.tracking.service.interfaces.ICacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class WorkflowInboxRedisService {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowInboxRedisService.class);

    private final ICacheService redisService;

    private final ServiceMetadataFeignClient serviceMetadataFeignClient;

    public WorkflowInboxRedisService(ICacheService redisService, ServiceMetadataFeignClient serviceMetadataFeignClient) {
        this.redisService = redisService;
        this.serviceMetadataFeignClient = serviceMetadataFeignClient;
    }

    public void addTokenForUsers(WorkflowAssignmentKafkaEvent event) {

        for (Long userId : event.getUserIds()) {

            String inboxKey = buildInboxKey(userId, event.getLocationId());
            redisService.addToSet(inboxKey, event.getTokenKey());
            String lookupKey = buildLookupKey(event.getServiceId(), event.getTaskId(), userId,event.getLocationId());
            redisService.addToSet(lookupKey, event.getTokenKey());

            logger.info("Redis token mapping added for userId : {} tokenKey : {}", userId, event.getTokenKey());
        }

        redisService.addToSet(buildServiceHolderKey(event.getServiceId()), event.getTokenKey());
    }

    public void removeTokenForUsers(WorkflowAssignmentKafkaEvent event) {

        for (Long userId : event.getUserIds()) {

            String inboxKey = buildInboxKey(userId, event.getLocationId());
            redisService.removeFromSet(inboxKey, event.getTokenKey());
            Long size = redisService.getSetSize(inboxKey);

            if (size != null && size == 0) {
                redisService.deleteKey(inboxKey);
            }

            String lookupKey = buildLookupKey(event.getServiceId(), event.getTaskId(), userId,event.getLocationId());
            redisService.removeFromSet(lookupKey, event.getTokenKey());
            Long size_1 = redisService.getSetSize(lookupKey);

            if (size_1 != null && size_1 == 0) {
                redisService.deleteKey(lookupKey);
            }

            logger.info("Redis token mapping removed for userId : {} tokenKey : {}", userId, event.getTokenKey());
        }

        String serviceHolderKey = buildServiceHolderKey(event.getServiceId());

        redisService.removeFromSet(serviceHolderKey, event.getTokenKey());

        Long holderSize = redisService.getSetSize(serviceHolderKey);

        if (holderSize != null && holderSize == 0) {
            redisService.deleteKey(serviceHolderKey);
        }
    }

    public Set<Object> getTokensByUserAndLocation(Long userId, String locationId) {

        String key = buildInboxKey(userId, locationId);

        Set<Object> tokens = redisService.getSetMembers(key);

        if (tokens != null && !tokens.isEmpty()) {
            logger.info("Redis inbox cache hit key={}", key);
            return tokens;
        }

        logger.warn("Redis inbox cache miss key={}. Fetching from ServiceMetadata", key);

        List<WorkflowAssignmentKafkaEvent> assignments = serviceMetadataFeignClient.getAssignmentsForUser(userId, locationId);

        if (assignments == null || assignments.isEmpty()) {

            logger.info("[0] No workflow assignments found for userId={} locationId={}", userId, locationId);
            return Set.of();
        }

        assignments.forEach(this::addTokenForUsers);

        tokens = redisService.getSetMembers(key);
        return tokens != null ? tokens : Set.of();
    }

    public Set<Object> getTokenByServiceTaskAndUser(Integer serviceId, String taskId, Long userId, String locationId) {

        String key = buildLookupKey(serviceId, taskId, userId, locationId);

        Set<Object> tokens = redisService.getSetMembers(key);

        if (tokens != null && !tokens.isEmpty()) {
            logger.info("Redis lookup cache hit key={}", key);
            return tokens;
        }

        logger.warn("Redis lookup cache miss key={}. Fetching user assignments from ServiceMetadata", key);

        List<WorkflowAssignmentKafkaEvent> assignments = serviceMetadataFeignClient.getAssignmentsForUser(userId, locationId);

        if (assignments == null || assignments.isEmpty()) {
            logger.info("[1] No workflow assignments found for userId={} locationId={}", userId, locationId);
            return Set.of();
        }
        assignments.forEach(this::addTokenForUsers);
        tokens = redisService.getSetMembers(key);

        return tokens != null ? tokens : Set.of();
    }


    private String buildInboxKey(Long userId, String locationId) {
        return ("wf:user:" + userId + ":loc:" + locationId).trim();
    }

    private String buildLookupKey(Integer serviceId, String taskId, Long userId,String locationId) {
        return ("wf:srv:" + serviceId + ":task:" + taskId + ":user:" + userId + ":locId:" + locationId).trim();
    }

    public Set<Object> getHolderIdsByService(Integer serviceId) {
        String key = buildServiceHolderKey(serviceId);

        Set<Object> holderIds = redisService.getSetMembers(key);

        if (holderIds != null && !holderIds.isEmpty()) {
            logger.info("Redis service-holder cache hit key={}", key);
            return holderIds;
        }

        logger.warn("Redis service-holder cache miss key={}", key);

        List<WorkflowAssignmentKafkaEvent> assignments = serviceMetadataFeignClient.getAssignmentsForService(serviceId);

        if (assignments == null || assignments.isEmpty()) {
            return Set.of();
        }

        assignments.forEach(this::addTokenForUsers);
        return redisService.getSetMembers(key);
    }

    private String buildServiceHolderKey(Integer serviceId) {
        return ("wf:srv:" + serviceId + ":holders").trim();
    }
}
