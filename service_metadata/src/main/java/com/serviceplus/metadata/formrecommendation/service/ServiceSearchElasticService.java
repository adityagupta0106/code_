package com.serviceplus.metadata.formrecommendation.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.formrecommendation.dto.ServiceSearchDocument;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;

@Service
public class ServiceSearchElasticService {
	@Value("${elasticsearch.index.service-search}")
	private String serviceSearchIndex;

	@Autowired
	private ElasticsearchClient elasticsearchClient;
	public void upsert(ServiceSearchDocument document) throws IOException {

		elasticsearchClient
				.index(i -> i.index(serviceSearchIndex).id(document.getServiceId().toString()).document(document));
	}
	public void delete(Long serviceId) throws IOException {

		elasticsearchClient.delete(d -> d.index(serviceSearchIndex).id(serviceId.toString()));
	}
	public ServiceSearchDocument get(Long serviceId) throws IOException {

		GetResponse<ServiceSearchDocument> response = elasticsearchClient
				.get(g -> g.index(serviceSearchIndex).id(serviceId.toString()), ServiceSearchDocument.class);
		return response.source();
	}
}
