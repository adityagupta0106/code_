package com.serviceplus.tracking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.tracking.entity.MessageBox;

@Repository
public interface MessageBoxRepository extends JpaRepository<MessageBox, Long> {

	Page<MessageBox> findByHolderIdInAndTenantIdAndApplRefNoContainingIgnoreCaseAndClearedFalse(List<String> holderIds,
			String tenantId, String trim, Pageable pageable);

	Page<MessageBox> findByHolderIdInAndTenantIdAndServiceIdAndIsReadAndClearedFalse(List<String> holderIds,
			String tenantId, Integer serviceId, Boolean isRead, Pageable pageable);

	Page<MessageBox> findByHolderIdInAndTenantIdAndServiceIdAndClearedFalse(List<String> holderIds, String tenantId,
			Integer serviceId, Pageable pageable);

	Page<MessageBox> findByHolderIdInAndTenantIdAndIsReadAndClearedFalse(List<String> holderIds, String tenantId,
			Boolean isRead, Pageable pageable);

	Page<MessageBox> findByHolderIdInAndTenantIdAndClearedFalse(List<String> holderIds, String tenantId,
			Pageable pageable);

	Long countByHolderIdInAndIsReadFalseAndClearedFalse(List<String> holderIds);

	Optional<MessageBox> findByMessageIdAndTenantIdAndHolderIdIn(Long messageId, String tenantId,
			List<String> holderIds);

	Page<MessageBox> findByTenantIdAndHolderIdInAndIsReadAndClearedFalse(String tenantId,List<String> holderIds, Boolean isRead,
			Pageable pageable);

	

	

}
