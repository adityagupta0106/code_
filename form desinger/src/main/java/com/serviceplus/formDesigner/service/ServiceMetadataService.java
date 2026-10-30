package com.serviceplus.formDesigner.service;

import com.serviceplus.formDesigner.feignClient.ServiceMataDataFeignClient;
import com.serviceplus.formDesigner.infrastructure.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.serviceplus.formDesigner.util.CommonUtil.getCacheTimeoutWithJitter;

@Service
public class ServiceMetadataService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceMetadataService.class);

    private final RedisCacheService redis;
    private final ServiceMataDataFeignClient serviceMataDataFeignClient;

    public ServiceMetadataService(RedisCacheService redis, ServiceMataDataFeignClient serviceMataDataFeignClient) {
        this.redis = redis;
        this.serviceMataDataFeignClient = serviceMataDataFeignClient;
    }

    public ServiceMetadataDTO fetchServiceMetadata(Integer serviceId) {

        String redisKey = "ServiceMetaData_" + serviceId;

        ServiceMetadataDTO metadata = redis.fetch(redisKey, ServiceMetadataDTO.class);

        if (metadata != null) {
            return metadata;
        }

        return fetchAndCacheServiceMetadata(serviceId);
    }

    private ServiceMetadataDTO fetchAndCacheServiceMetadata(Integer serviceId) {

        ResponseEntity<ServiceMetadataDTO> response = serviceMataDataFeignClient.getServiceMetadata(serviceId);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {

            logger.error("Unable to fetch service metadata. serviceId={}", serviceId);
            throw new RuntimeException("Unable to fetch service metadata");
        }

        ServiceMetadataDTO metadata = response.getBody();

        logger.info("Service metadata cache miss. Fetching metadata from Metadata Service. serviceId={}", serviceId);

        int timeout = getCacheTimeoutWithJitter(5 * 60 * 60, 30);

        redis.add(metadata, "ServiceMetaData_" + serviceId, true, timeout);

        return metadata;
    }
}