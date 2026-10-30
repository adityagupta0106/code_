package com.serviceplus.metadata.formrecommendation.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.formrecommendation.dto.FormSearchDocument;
import com.serviceplus.metadata.formrecommendation.dto.GlobalSearchRequestDTO;
import com.serviceplus.metadata.formrecommendation.dto.ServiceSearchDocument;
import com.serviceplus.metadata.formrecommendation.dto.ServiceSearchDocument.AttachedFormDTO;
import com.serviceplus.metadata.formrecommendation.dto.UniqueFormResponseDTO;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchResponse;

@Service
public class GlobalSearchService {

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	@Value("${elasticsearch.index.service-search}")
	private String serviceSearchIndex;

	@Value("${elasticsearch.index.form-search}")
	private String formSearchIndex;

	public List<UniqueFormResponseDTO> globalSearch(GlobalSearchRequestDTO requestDTO) throws IOException {
		List<ServiceSearchDocument> services = getServiceDTOs(requestDTO);
		List<FormSearchDocument> forms = getFormDTOs(requestDTO);
		List<UniqueFormResponseDTO> uniqueForms = prepareUniqueForms(services, forms);
		return uniqueForms;
	}

	public List<ServiceSearchDocument> getServiceDTOs(GlobalSearchRequestDTO requestDTO) throws IOException {

		SearchResponse<ServiceSearchDocument> response = elasticsearchClient.search(s -> s
				.index(serviceSearchIndex)
				.query(q -> q.bool(b -> {
					b.must(m -> m.bool(bb -> bb
							.should(sh -> sh.multiMatch(mm -> mm.query(requestDTO.getKeyword())
							.fields("serviceName", "serviceDescription", "attachedForm.formName")
							.fuzziness("AUTO").operator(Operator.Or)))
							.should(sh -> sh.wildcard(w -> w.field("serviceName.keyword")
							.value("*" + requestDTO.getKeyword().toLowerCase() + "*")))));
					
					if (hasText(requestDTO.getTenantId())) {
						b.filter(f -> f.term(t -> t.field("tenantId").value(requestDTO.getTenantId())));
					}
					
					if (hasText(requestDTO.getDepartmentId())) {
						b.filter(f -> f.term(t -> t.field("departmentId").value(requestDTO.getDepartmentId())));
					}
					return b;
				})), ServiceSearchDocument.class);
		return response.hits().hits().stream().map(hit -> hit.source()).toList();
	}

	public List<FormSearchDocument> getFormDTOs(GlobalSearchRequestDTO requestDTO) throws IOException {
		SearchResponse<FormSearchDocument> response = elasticsearchClient.search(s -> s
				.index(formSearchIndex) .query(q -> q.bool(b -> {
					b.must(m -> m.bool(bb -> bb
					.should(sh -> sh.multiMatch(mm -> mm.query(requestDTO.getKeyword())
					.fields("formName", "formDescription").fuzziness("AUTO").operator(Operator.Or)))
					.should(sh -> sh.wildcard(w -> w.field("formName.keyword")
					.value("*" + requestDTO.getKeyword().toLowerCase() + "*")))));

					if (hasText(requestDTO.getTenantId())) {
						b.filter(f -> f.term(t -> t.field("tenantId").value(requestDTO.getTenantId())));
					}
					if (hasText(requestDTO.getDepartmentId())) {
						b.filter(f -> f.term(t -> t.field("departmentId").value(requestDTO.getDepartmentId())));
					}
					return b;
				})), FormSearchDocument.class);

		return response.hits().hits().stream().map(hit -> hit.source()).toList();
	}

	public List<UniqueFormResponseDTO> prepareUniqueForms(List<ServiceSearchDocument> services,
			List<FormSearchDocument> forms) {
		Map<String, UniqueFormResponseDTO> uniqueMap = new LinkedHashMap<String, UniqueFormResponseDTO>();
		
		for (ServiceSearchDocument service : services) {
			if (service.getAttachedForm() != null) {
				for (AttachedFormDTO attachedForm : service.getAttachedForm()) {
					uniqueMap.putIfAbsent(attachedForm.getFormId(),
							new UniqueFormResponseDTO(attachedForm.getFormId(), attachedForm.getFormName()));
				}
			}
		}

		for (FormSearchDocument form : forms) {
			uniqueMap.putIfAbsent(form.getFormId(), new UniqueFormResponseDTO(form.getFormId(), form.getFormName()));
		}

		return new ArrayList<>(uniqueMap.values());
	}
	
	private boolean hasText(String value) {
	    return value != null && !value.trim().isEmpty();
	}
}