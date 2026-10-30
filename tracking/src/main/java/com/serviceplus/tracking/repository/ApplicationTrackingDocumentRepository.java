package com.serviceplus.tracking.repository;

import com.serviceplus.tracking.entity.ApplicationTrackingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ApplicationTrackingDocumentRepository extends JpaRepository<ApplicationTrackingDocument, Long> {

    List<ApplicationTrackingDocument> findByTracking_ProcessId(String processId);

    List<ApplicationTrackingDocument> findByTrackingProcessId(String processId);
}
