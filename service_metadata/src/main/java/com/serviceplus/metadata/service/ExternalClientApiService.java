package com.serviceplus.metadata.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.serviceplus.metadata.dto.ExternalClientApiDetailDTO;
import com.serviceplus.metadata.dto.ExternalClientApiDetailDTO.ApiParameterDetailDTO;
import com.serviceplus.metadata.dto.ExternalSystemDTO.ApiDetail;
import com.serviceplus.metadata.dto.ExternalSystemDTO.ApiDetail.ApiParameterDetail;
import com.serviceplus.metadata.dto.ExternalSystemDTO.GeneralDetail;
import com.serviceplus.metadata.dto.ExternalSystemUserDTO;
import com.serviceplus.metadata.entity.ExternalSystemRegistration;
import com.serviceplus.metadata.entity.ExternalSystemUser;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.repository.IExternalSystemRegistrationRepository;
import com.serviceplus.metadata.repository.IExternalSystemUserRepository;

@Service
public class ExternalClientApiService {

	private final IExternalSystemRegistrationRepository repository;
	
	private final IExternalSystemUserRepository userRepository;

	public ExternalClientApiService(IExternalSystemRegistrationRepository repository, IExternalSystemUserRepository userRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}

	public ExternalClientApiDetailDTO getExternalClientApiDetail(String clientId, Integer apiType) {

		ExternalSystemRegistration registration = repository.findByClientId(clientId)
				.orElseThrow(() -> new SPRuntimeError("Client not found", HttpStatus.NOT_FOUND));

		ApiDetail apiDetail = registration.getExternalSystemDetail().getApiDetails().stream()
				.filter(api -> apiType.equals(api.getApiType())).findFirst()
				.orElseThrow(() -> new SPRuntimeError("API configuration not found", HttpStatus.NOT_FOUND));

		GeneralDetail generalDetail =registration.getExternalSystemDetail().getGeneralDetail();

		ExternalClientApiDetailDTO dto = new ExternalClientApiDetailDTO();

		dto.setClientId(registration.getClientId());
		dto.setClientName(generalDetail.getClientName());

		dto.setLiveIp(generalDetail.getLiveIp());
		dto.setStagingIp(generalDetail.getStagingIp());

		dto.setApiType(apiDetail.getApiType());

		dto.setIsWhitelistIpReq(generalDetail.getWhitelistIpRequired());

		dto.setApiStagingUrl(apiDetail.getApiStagingUrl());
		dto.setApiProdUrl(apiDetail.getApiProdUrl());

		dto.setuId(apiDetail.getuId());
		dto.setuSecret(apiDetail.getuSecret());

		dto.setApiParameters(
		        Optional.ofNullable(apiDetail.getApiParameterDetails())
		                .orElse(Collections.emptyList())
		                .stream()
		                .map(this::convert)
		                .toList());
		return dto;
	}

	private ApiParameterDetailDTO convert(ApiParameterDetail param) {
	    ApiParameterDetailDTO dto = new ApiParameterDetailDTO();
	    dto.setParamType(param.getParamType());
	    dto.setParamName(param.getParamName());
	    dto.setParamNode(param.getParamNode());
	    dto.setParamValType(param.getParamValType());
	    dto.setParamConstVal(param.getParamConstVal());
	    return dto;
	}

	
	public ExternalSystemUserDTO getByClientId(String clientId) {

	    List<ExternalSystemUser> externalSystemUser = userRepository.findByClientId(clientId);

	    if (externalSystemUser == null||externalSystemUser.isEmpty()) {
	        return null;
	    }

	    ExternalSystemUserDTO dto = new ExternalSystemUserDTO();
	    dto.setId(externalSystemUser.get(0).getId());
	    dto.setExternalSystemRegId(externalSystemUser.get(0).getExternalSystemRegId());
	    dto.setClientId(externalSystemUser.get(0).getClientId());
	    dto.setUserId(externalSystemUser.get(0).getUserId().intValue());
	    dto.setSignNo(externalSystemUser.get(0).getSignNo());
	    dto.setCreatedOn(externalSystemUser.get(0).getCreatedOn());

	    return dto;
	}
}
