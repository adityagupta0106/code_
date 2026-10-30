package com.serviceplus.metadata.aadhaarConfiguration.service;

import com.serviceplus.metadata.aadhaarConfiguration.dto.AuaMastersResponse;
import com.serviceplus.metadata.aadhaarConfiguration.dto.MasterOptionResponse;
import com.serviceplus.metadata.aadhaarConfiguration.model.AadhaarMappingType;
import com.serviceplus.metadata.aadhaarConfiguration.model.AuaMappingTransformation;
import com.serviceplus.metadata.aadhaarConfiguration.model.AuaResponseAttributeType;
import com.serviceplus.metadata.dto.UserSessionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.serviceplus.metadata.utility.CommonUtil.getUserSessionDetails;
import static java.util.Objects.requireNonNull;

@Service
public class AuaMasterService {

    public AuaMastersResponse getMasters(HttpServletRequest request) {

        UserSessionDTO user = requireNonNull(getUserSessionDetails(request), "User session not found");

        String tenantId = user.getTenantId();

        AuaMastersResponse response = new AuaMastersResponse();


        response.setSourceTypes(toOptions(AadhaarMappingType.values()));
        response.setTransformations(toOptions(AuaMappingTransformation.values()));
        response.setResponseAttributeTypes(toOptions(AuaResponseAttributeType.values()));

        return response;
    }

    private <T extends Enum<T>> List<MasterOptionResponse> toOptions(T[] values) {

        return Arrays.stream(values)
                .map(value -> new MasterOptionResponse(
                        value.name(),
                        createLabel(value.name())
                ))
                .collect(Collectors.toList());
    }

    private String createLabel(String value) {

        return Arrays.stream(value.split("_"))
                .map(word ->
                        word.substring(0, 1).toUpperCase()
                                + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
