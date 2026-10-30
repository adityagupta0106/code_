package com.serviceplus.metadata.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceplus.metadata.notification.entity.NotificationConfiguration;

public interface INotificationRepository extends JpaRepository<NotificationConfiguration, Long> {

	List<NotificationConfiguration> findByTenantIdAndServiceIdAndChannel(String tenantId,
			Integer serviceId, String channel);

	List<NotificationConfiguration> findByTenantIdAndServiceId(String tenantId, Integer serviceId);

	List<NotificationConfiguration> findByTenantIdAndChannel(String tenantId, String channel);

	List<NotificationConfiguration> findByTenantId(String tenantId);

	Optional<NotificationConfiguration> findByNotificationId(Long notificationId);

	Optional<NotificationConfiguration> findByNotificationIdAndTenantId(Long notificationId,String tenantId);

	List<NotificationConfiguration> findByServiceId(Integer serviceId);

	void deleteByServiceId(Integer serviceId);

}
