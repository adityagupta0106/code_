package com.serviceplus.metadata.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.metadata.notification.entity.NotificationProviderConfig;

@Repository
public interface NotificationProviderConfigRepository extends JpaRepository<NotificationProviderConfig, Long> {
	public Optional<NotificationProviderConfig> findByIdAndUserIdAndType(Long id,Long userId, String type);
	public List<NotificationProviderConfig> findByUserIdAndType(Long userId, String type);
	public List<NotificationProviderConfig> findByUserIdAndStatusNot(Long userId, String status);
//	public List<NotificationProviderConfig> findByDepartmentIdAndStatus(Long departmentId, String status);
	public List<NotificationProviderConfig> findByTypeAndTenantIdAndDepartmentIdAndStatusIn(String type,String tenantId,Integer departmentId, List<String> status);

}
