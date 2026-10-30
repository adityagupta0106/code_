package com.serviceplus.formDesigner.formrecommendation.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Service
public class FormSearchElasticService {

	 @Value("${elasticsearch.index.form-search}")
	 private String formSearchIndex;
	 
	@Autowired
	private ElasticsearchClient elasticsearchClient;

    public void upsert(FormSearchDocument document) throws IOException {
        elasticsearchClient.index(i -> i .index(formSearchIndex).id(document.getHolderId()).document(document));
    }

    public void delete(String formId) throws IOException {
        elasticsearchClient.delete(d -> d.index(formSearchIndex).id(formId));
    }
}
