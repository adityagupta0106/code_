package com.serviceplus.metadata.mvel.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.serviceplus.metadata.mvel.dto.MVELTriggerPoint;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingDTO.FuncParamDTO;
import com.serviceplus.metadata.mvel.dto.MvelEventMappingInvalidationMSGDTO;
import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO.FunctionParameter;
import com.serviceplus.metadata.mvel.entity.MvelFunction;
import com.serviceplus.metadata.mvel.repository.IMvelFunctionRepository;
import com.serviceplus.metadata.mvel.service.MvelFunctionService;

@Component
public class MvelEventMappingValidator {

	@Autowired
	private MvelFunctionService mvelFunctionService;

	@Autowired
	private IMvelFunctionRepository mvelFunctionRepository;

	@SuppressWarnings("unchecked")
	public MvelEventMappingInvalidationMSGDTO validate(MvelEventMappingDTO target) {
		MvelEventMappingInvalidationMSGDTO invalidationMsg = null;

		ResponseEntity<?> responseEntity = mvelFunctionService.getMvelTriggerPoints();
		List<MVELTriggerPoint> mvelTriggerPoints = (List<MVELTriggerPoint>) responseEntity.getBody();
		Boolean eventValid = false;
		for (MVELTriggerPoint mvelTriggerPoint : mvelTriggerPoints) {
			eventValid = mvelTriggerPoint.getValue().equals(target.getEvent());
			if (eventValid)
				break;
		}
		if (!eventValid) {
			if (Objects.isNull(invalidationMsg))
				invalidationMsg = new MvelEventMappingInvalidationMSGDTO();

			invalidationMsg.setEvent("Invalid Event");
			return invalidationMsg;
		}

		MvelFunction mvelFunction = mvelFunctionRepository.findByIdAndServiceId(target.getTriggerPoint().getTriggerFunction().getFuncId().longValue()
				,target.getServiceId().intValue());
		if (Objects.isNull(mvelFunction)) {
			if (Objects.isNull(invalidationMsg))
				invalidationMsg = new MvelEventMappingInvalidationMSGDTO();

			invalidationMsg.getTriggerPoint().getTriggerFunction().setMessage("Invalid Function");
			return invalidationMsg;
		}

		List<FunctionParameter> functionDTO = mvelFunction.getMvelFunctionJson().getParameters();
		
		List<FuncParamDTO> funcParams = target.getTriggerPoint().getTriggerFunction().getFuncParams();
		
		

		
		List<String> mappingParamNames = target.getTriggerPoint().getTriggerFunction().getFuncParams().stream()
				.map(p -> String.valueOf(p.getParamName())).collect(Collectors.toList());


		Set<String> uniqueParams = new HashSet<>();
		List<String> duplicateParams = mappingParamNames.stream()
		        .filter(p -> !uniqueParams.add(p))
		        .collect(Collectors.toList());

		if (!duplicateParams.isEmpty()) {
			if (Objects.isNull(invalidationMsg))
				invalidationMsg = new MvelEventMappingInvalidationMSGDTO();

			List<MvelEventMappingInvalidationMSGDTO.FuncParamDTO> errorParams = duplicateParams.stream().map(p -> {
				MvelEventMappingInvalidationMSGDTO.FuncParamDTO dto = new MvelEventMappingInvalidationMSGDTO.FuncParamDTO();
				dto.setParamName(p + " : duplicate parameter name");
				return dto;
			}).collect(Collectors.toList());

			invalidationMsg.getTriggerPoint().getTriggerFunction().setParams(errorParams);
			return invalidationMsg;
		}


		return invalidationMsg;
	}

}
