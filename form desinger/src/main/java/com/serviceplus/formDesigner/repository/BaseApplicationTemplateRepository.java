package com.serviceplus.formDesigner.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.serviceplus.formDesigner.request.model.IApplicationTemplate;

@NoRepositoryBean
public interface BaseApplicationTemplateRepository<T extends IApplicationTemplate> extends MongoRepository<T, String> {
    List<T> findByFormIdAndStatus(String formId, String status);
    T findByFormIdAndCreatedByAndStatus(String formId, Long createdBy, String status);
}