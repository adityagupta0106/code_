package com.serviceplus.metadata.kafka.repository;

import com.serviceplus.metadata.kafka.entity.KafkaOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KafkaOutboxRepository extends JpaRepository<KafkaOutboxEvent, String> {

    List<KafkaOutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(String status);
}