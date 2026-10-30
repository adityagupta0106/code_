package com.serviceplus.metadata.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.serviceplus.metadata.entity.ServiceMetadata;

public interface IServiceMetadataRepository extends MongoRepository<ServiceMetadata, Integer> {

	List<ServiceMetadata> findByTenantId(String tenantId);

	ServiceMetadata findByBaseServiceIdAndTenantId(Integer baseServiceId, String tenantId);

	ServiceMetadata findByServiceIdAndTenantId(Integer serviceId, String tenantId);

	Optional<ServiceMetadata> findTopByBaseServiceIdOrderByServiceIdDesc(Integer baseServiceId);

	List<ServiceMetadata> findByTenantIdAndServiceIdIn(String tenantId, List<Integer> serviceIds);
	
	@Query("{ 'service_json.notificationDetails.notificationId' : ?0 }")
	List<ServiceMetadata> findServiceByNotificationId(Long notificationId);

	List<ServiceMetadata> findByServiceIdIn(List<Integer> serviceIds);

	
}