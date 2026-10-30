package com.serviceplus.tracking.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.InboxApplReqDTO;
import com.serviceplus.tracking.dto.InboxFilterResolution;
import com.serviceplus.tracking.dto.MatchedOutputAttr;
import com.serviceplus.tracking.entity.InboxSentBoxFilterConfig;
import com.serviceplus.tracking.entity.ServiceFilterInputData;
import com.serviceplus.tracking.entity.ServiceFilterOutputData;
import com.serviceplus.tracking.enums.FilterOperator;
import com.serviceplus.tracking.repository.InboxSentBoxFilterConfigRepository;
import com.serviceplus.tracking.repository.ServiceFilterInputDataRepository;
import com.serviceplus.tracking.repository.ServiceFilterOutputDataRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class DynamicInboxFilterService {

	@Autowired
	private InboxSentBoxFilterConfigRepository filterConfigRepository;

	@Autowired
	private ServiceFilterInputDataRepository filterInputDataRepository;

	@Autowired
	private ServiceFilterOutputDataRepository filterOutputDataRepository;

	private static final Logger applicationFlowLogs = LogManager.getLogger("applicationFlowLogger");
	private static final int FILTER_BATCH_SIZE = 500;

	public InboxFilterResolution resolveInboxFilterResult(Long serviceId, String taskId,
			List<InboxApplReqDTO.FilterDTO> filters, List<Character> filterTypes) {

		if (filters == null || filters.isEmpty()) {
			return new InboxFilterResolution(null, Collections.emptyMap());
		}

		List<InboxSentBoxFilterConfig> inputConfigs = filterConfigRepository
				.findByServiceIdAndTaskIdAndAttrTypeAndFilterTypeInOrderByAttrOrder(serviceId, taskId, 'I',filterTypes);
		
		List<InboxSentBoxFilterConfig> outputConfigs = filterConfigRepository
				.findByServiceIdAndTaskIdAndAttrTypeAndFilterTypeInOrderByAttrOrder(serviceId, taskId, 'O',filterTypes);
		
		boolean hasInputConfig = !inputConfigs.isEmpty();
		boolean hasOutputConfig = !outputConfigs.isEmpty();

		List<String> inputMatchedIds = hasInputConfig
		        ? resolveInputApplicationIds(serviceId, inputConfigs, filters)
		        : null;

		List<String> finalIds = (inputMatchedIds != null) ? inputMatchedIds : null;

		Map<String, List<MatchedOutputAttr>> outputAttrsByApplicationId = hasOutputConfig
		        ? resolveOutputDisplayAttrs(serviceId, outputConfigs, finalIds)
		        : Collections.emptyMap();

		return new InboxFilterResolution(finalIds, outputAttrsByApplicationId);

	}

	private Map<String, List<MatchedOutputAttr>> resolveOutputDisplayAttrs(
	        Long serviceId, List<InboxSentBoxFilterConfig> outputConfigs, List<String> applicationIds) {

	    if (outputConfigs.isEmpty()) {
	        return Collections.emptyMap();
	    }

	    Long filterId = outputConfigs.get(0).getFilterId();

	    Specification<ServiceFilterOutputData> spec = (root, query, cb) -> {
	        Predicate base = cb.and(
	                cb.equal(root.get("serviceId"), serviceId),
	                cb.equal(root.get("filterId"), filterId));

	        if (applicationIds == null) {
	            return base;
	        }
	        return cb.and(base, root.get("applicationId").in(applicationIds));
	    };

	    List<ServiceFilterOutputData> allRows = filterOutputDataRepository.findAll(spec);

	    Map<String, ServiceFilterOutputData> latestByApplicationId = allRows.stream()
	            .collect(Collectors.toMap(
	                    ServiceFilterOutputData::getApplicationId,
	                    row -> row,
	                    (existing, incoming) -> incoming.getUpDate().isAfter(existing.getUpDate()) ? incoming : existing));

	    List<InboxSentBoxFilterConfig> orderedOutputConfigs = outputConfigs.stream()
	            .sorted(Comparator.comparing(InboxSentBoxFilterConfig::getAttrOrder))
	            .toList();

	    Map<String, List<MatchedOutputAttr>> result = new HashMap<>();

	    latestByApplicationId.forEach((appId, row) -> {
	        List<MatchedOutputAttr> attrs = orderedOutputConfigs.stream()
	                .map(config -> new MatchedOutputAttr(
	                        config.getAttrLabel(),
	                        resolveValueForAttrOrder(row, config.getAttrOrder())))
	                .toList();
	        result.put(appId, attrs);
	    });

	    return result;
	}

	private List<String> resolveInputApplicationIds(Long serviceId, List<InboxSentBoxFilterConfig> configs,
			List<InboxApplReqDTO.FilterDTO> filters) {

		Map<String, InboxSentBoxFilterConfig> configByAttrId = configs.stream()
				.collect(Collectors.toMap(InboxSentBoxFilterConfig::getAttrId, c -> c));

		Specification<ServiceFilterInputData> combinedSpec = null;

		for (InboxApplReqDTO.FilterDTO filter : filters) {
			InboxSentBoxFilterConfig config = configByAttrId.get(filter.getAttrId());
			if (config == null)
				continue;

			Specification<ServiceFilterInputData> current = (root, query, cb) -> cb.and(
					cb.equal(root.get("serviceId"), serviceId), cb.equal(root.get("filterId"), config.getFilterId()),
					cb.equal(root.get(attrColumn(config.getAttrOrder())), filter.getValue()));

			combinedSpec = combineSpec(combinedSpec, current, config.getFilterCondition());
		}

		if (combinedSpec == null)
			return Collections.emptyList();

		return pageThroughDistinctIds(filterInputDataRepository::findAll, combinedSpec,
				ServiceFilterInputData::getApplicationId);
	}

	private String resolveValueForAttrOrder(ServiceFilterOutputData row, Integer attrOrder) {
	    return switch (attrOrder) {
	        case 1 -> row.getAttr1Value();
	        case 2 -> row.getAttr2Value();
	        case 3 -> row.getAttr3Value();
	        case 4 -> row.getAttr4Value();
	        case 5 -> row.getAttr5Value();
	        case 6 -> row.getAttr6Value();
	        case 7 -> row.getAttr7Value();
	        case 8 -> row.getAttr8Value();
	        case 9 -> row.getAttr9Value();
	        case 10 -> row.getAttr10Value();
	        default -> throw new SPRuntimeError("Unsupported attr_order: " + attrOrder, HttpStatus.BAD_REQUEST);
	    };
	}
	private <T> Specification<T> combineSpec(Specification<T> combined, Specification<T> current,
			String filterCondition) {
		if (combined == null)
			return current;
		FilterOperator op = FilterOperator.fromSymbol(filterCondition);
		return (op == FilterOperator.OR) ? combined.or(current) : combined.and(current);
	}

	private String attrColumn(Integer attrOrder) {
		return switch (attrOrder) {
		case 1 -> "attr1Value";
		case 2 -> "attr2Value";
		case 3 -> "attr3Value";
		case 4 -> "attr4Value";
		case 5 -> "attr5Value";
		case 6 -> "attr6Value";
		case 7 -> "attr7Value";
		case 8 -> "attr8Value";
		case 9 -> "attr9Value";
		case 10 -> "attr10Value";
		default -> throw new SPRuntimeError("Unsupported attr_order: " + attrOrder, HttpStatus.BAD_REQUEST);
		};
	}

	private <T> List<String> pageThroughDistinctIds(BiFunction<Specification<T>, Pageable, Page<T>> pageFetcher,
			Specification<T> spec, Function<T, String> idExtractor) {

		Set<String> ids = new LinkedHashSet<>();
		int pageNumber = 0;
		Page<T> page;

		do {
			Pageable pageable = PageRequest.of(pageNumber, FILTER_BATCH_SIZE,
					Sort.by(Sort.Direction.ASC, "applicationId"));
			page = pageFetcher.apply(spec, pageable);
			page.getContent().forEach(row -> ids.add(idExtractor.apply(row)));
			pageNumber++;
		} while (page.hasNext());

		return new ArrayList<>(ids);
	}
}