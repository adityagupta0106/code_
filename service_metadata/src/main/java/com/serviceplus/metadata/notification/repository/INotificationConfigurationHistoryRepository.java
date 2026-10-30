package com.serviceplus.metadata.notification.repository;

import com.serviceplus.metadata.notification.entity.NotificationConfigurationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INotificationConfigurationHistoryRepository extends JpaRepository<NotificationConfigurationHistory, Long> {

    List<NotificationConfigurationHistory> findByModificationId(Long modificationId);

    List<NotificationConfigurationHistory> findByServiceId(Integer serviceId);
}
