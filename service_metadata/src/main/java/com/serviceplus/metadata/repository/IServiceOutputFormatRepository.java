package com.serviceplus.metadata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.serviceplus.metadata.entity.ServiceOutputFormat;

public interface IServiceOutputFormatRepository extends JpaRepository<ServiceOutputFormat, Long> {

	ServiceOutputFormat findByIdAndUserId(Long outputFormatId, Long userId);

	ServiceOutputFormat findByIdAndServiceIdAndUserId(Long outputFormatId, Integer serviceId, Long userId);

	List<ServiceOutputFormat> findByServiceId(Integer serviceId);

	List<ServiceOutputFormat> findByServiceIdAndUserId(Integer serviceId, Long userId);
	
	@Query(value = """
			SELECT *
			FROM schm_sp.service_output_format sof
			WHERE sof.service_id = :serviceId
			  AND (
			        sof.output_format_json ->> 'notificationType' IS NULL
			        OR sof.output_format_json ->> 'notificationType' = ''
			      )
			""", nativeQuery = true)
	List<ServiceOutputFormat> findAllDocumentByServiceId(@Param("serviceId") Integer serviceId);

	@Query(value = """
			SELECT *
			FROM schm_sp.service_output_format sof
			WHERE sof.service_id = :serviceId
			  AND sof.output_format_json ->> 'notificationType' IS NOT NULL
			  AND sof.output_format_json ->> 'notificationType' <> ''
			""", nativeQuery = true)
	List<ServiceOutputFormat> findAllNotificationsByServiceId(@Param("serviceId") Integer serviceId);

	@Query(value = """
			SELECT *
			FROM schm_sp.service_output_format sof
			WHERE sof.service_id = :serviceId
			  AND sof.output_format_json ->> 'notificationType' = :notificationType
			""", nativeQuery = true)
	List<ServiceOutputFormat> findNotificationsByType(@Param("serviceId") Integer serviceId,
			@Param("notificationType") String notificationType);

	void deleteByServiceId(Integer serviceId);

}
