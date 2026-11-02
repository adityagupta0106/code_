package com.serviceplus.tracking.service;

import com.serviceplus.tracking.ExceptionHandler.SPRuntimeError;
import com.serviceplus.tracking.dto.*;
import com.serviceplus.tracking.entity.ApplicationTracking;
import com.serviceplus.tracking.repository.ApplicationTrackingRepository;
import com.serviceplus.tracking.feignClient.InstanceConfigRegistryFeignClient;
import com.serviceplus.tracking.feignClient.ServiceMetadataFeignClient;
import com.serviceplus.tracking.feignClient.FormDesignerFeignClient;
import com.serviceplus.tracking.feignClient.UserManagmentFeignClient;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.serviceplus.tracking.utility.ApplicationConstants.*;
import static com.serviceplus.tracking.utility.CommonUtil.isEmpty;

/**
 * Single entry point for public application verification:
 * EMAIL_OTP, MOBILE_OTP, and FORM_ATTRIBUTES.
 * TrackingService owns authorization; FormDesigner owns matching;
 * UserManagement owns OTP generation, SMS/email delivery and OTP validation.
 *
 * Public API endpoints and request/response contracts are unchanged.
 */
@Service
public class PublicTrackingVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(PublicTrackingVerificationService.class);

    private final ApplicationTrackingRepository trackingRepository;
    private final InstanceConfigRegistryFeignClient instanceConfigRegistryFeignClient;
    private final ServiceMetadataFeignClient serviceMetadataFeignClient;
    private final FormDesignerFeignClient formDesignerFeignClient;
    private final UserManagmentFeignClient userManagmentFeignClient;
    private final PublicTrackingAuthorizationService publicAuthorization;

    @Value("${TRACKING_INTERNAL_TOKEN:}")
    private String trackingInternalToken;

    public PublicTrackingVerificationService(
            ApplicationTrackingRepository trackingRepository,
            InstanceConfigRegistryFeignClient instanceConfigRegistryFeignClient,
            ServiceMetadataFeignClient serviceMetadataFeignClient,
            FormDesignerFeignClient formDesignerFeignClient,
            UserManagmentFeignClient userManagmentFeignClient,
            PublicTrackingAuthorizationService publicAuthorization) {
        this.trackingRepository = trackingRepository;
        this.instanceConfigRegistryFeignClient = instanceConfigRegistryFeignClient;
        this.serviceMetadataFeignClient = serviceMetadataFeignClient;
        this.formDesignerFeignClient = formDesignerFeignClient;
        this.userManagmentFeignClient = userManagmentFeignClient;
        this.publicAuthorization = publicAuthorization;
    }

    public ApplicationTrackingResponse trackPublicApplication(ApplicationTrackingRequest requestDTO, HttpServletRequest request) {

        publicAuthorization.requireEnabled();
        String tenant=resolveTenantId(request);
        Integer service=requestDTO.getServiceId();
        String app=requestDTO.getApplId();
        if(service==null || service<=0 || app==null || app.isBlank()) throw PublicTrackingAuthorizationService.denied();
        Map<String,Object> config=modeOConfig(tenant,service,null);
        publicAuthorization.consume(requestDTO.getVerificationProof(),tenant,service,app,publicAuthorization.fingerprint(config));
        List<ApplicationTracking> rows=trackingRepository.findByApplIdAndTenantIdOrderByProcessIdAsc(app,tenant);
        if(rows.isEmpty() || rows.stream().anyMatch(row->!service.equals(row.getServiceId()))) throw PublicTrackingAuthorizationService.denied();
        ApplicationTracking first=rows.getFirst();
        ApplicationTrackingResponse result=new ApplicationTrackingResponse();
        result.setApplId(first.getApplId());result.setApplRefNo(first.getApplRefNo());
        result.setServiceName(first.getServiceName());result.setApplyDate(first.getApplyDate());
        boolean dates=Boolean.TRUE.equals(config.get("displayActionTakenDate"));
        result.setTrackingDetails(rows.stream().map(row->{
            ApplicationTrackingResponse.TrackingDetailDTO detail=new ApplicationTrackingResponse.TrackingDetailDTO();
            detail.setTaskName(row.getTaskName());detail.setActionName(row.getActionName());
            if(dates) detail.setActionOn(row.getActionOn());
            return detail;
        }).toList());
        return result;
    }

    private String resolveTenantId(HttpServletRequest request) {

        String clientDomain = request.getHeader(CLIENT_DOMAIN_HEADER);
        if (clientDomain == null || clientDomain.isBlank()) {
            throw new SPRuntimeError("Client domain is required",HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<?> response =instanceConfigRegistryFeignClient.fetchTenantId(Map.of(CLIENT_DOMAIN_HEADER, clientDomain));
        if (response == null|| !response.getStatusCode().is2xxSuccessful()|| !(response.getBody() instanceof String)) {
            throw new SPRuntimeError("Unable to resolve tenant",HttpStatus.BAD_GATEWAY);
        }
        String tenantId = (String) response.getBody();
        if (tenantId.isBlank()) {
            throw new SPRuntimeError("Tenant ID not found",HttpStatus.BAD_GATEWAY);
        }
        return tenantId;
    }

    public List<Map<String, Object>> getConfiguredServices(HttpServletRequest request) {
        String tenantId = resolveTenantId(request);
        return serviceMetadataFeignClient.getConfigureServices(tenantId);
    }

    public Map<String, Object> getPublicTrackingConfig(Long serviceId, HttpServletRequest request) {
        if (serviceId == null || serviceId <= 0) {
            throw new SPRuntimeError("Invalid service serviceId", HttpStatus.BAD_GATEWAY);
        }
        String tenantId = resolveTenantId(request);
        return serviceMetadataFeignClient.getPublicTrackingConfiguration(serviceId, tenantId);
    }

private Map<String, Object> fetchTrackingAttributes(String tenantId,Integer serviceId,String applId,List<String> attributeIds) {
    if (tenantId == null || tenantId.isBlank()|| serviceId == null || serviceId <= 0|| applId == null || applId.isBlank()|| attributeIds == null || attributeIds.isEmpty()) {

        throw new SPRuntimeError("Invalid tracking lookup request",HttpStatus.BAD_REQUEST);
    }
    if (trackingInternalToken == null|| trackingInternalToken.isBlank()) {

        throw new SPRuntimeError("Tracking internal authentication is not configured",HttpStatus.SERVICE_UNAVAILABLE);}

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("tenantId", tenantId);
    requestBody.put("serviceId", serviceId);
    requestBody.put("applId", applId);
    requestBody.put("attributeIds", attributeIds);

    ResponseEntity<Map<String, Object>> response =formDesignerFeignClient.getTrackingApplicationData(trackingInternalToken,requestBody);
    if (response == null|| !response.getStatusCode().is2xxSuccessful()|| response.getBody() == null) {

        throw new SPRuntimeError("Application verification data unavailable",HttpStatus.BAD_GATEWAY);
    }
    Object values = response.getBody().get("attributeValues");

    if (!(values instanceof Map<?, ?> rawValues)) {
        throw new SPRuntimeError("Invalid form verification response",HttpStatus.BAD_GATEWAY);
    }
    Map<String, Object> result = new HashMap<>();

    for (String attributeId : attributeIds) {
        if (attributeId == null || attributeId.isBlank()) {
            throw new SPRuntimeError("Invalid configured attribute",HttpStatus.BAD_REQUEST);
        }
        Object value = rawValues.get(attributeId);

        if (value != null) {result.put(attributeId, value);}
    }
    return result;
}

public Map<String, Object> sendReferenceEmailOtp(PublicApplicationTrackingRequest dto,HttpServletRequest request) {

    if (dto == null || isEmpty(dto.getApplRefNo())|| isEmpty(dto.getApplyDate())) {
        throw new SPRuntimeError("Reference number and date are required",HttpStatus.BAD_REQUEST);
    }

    LocalDateTime applyDate;
    try {
        applyDate = LocalDate.parse(dto.getApplyDate(),DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .atStartOfDay();
    } catch (Exception ex) {
        throw new SPRuntimeError("Invalid submission date",HttpStatus.BAD_REQUEST);
        }
    String tenantId = resolveTenantId(request);
    List<ApplicationTracking> applications =trackingRepository.findByApplRefNoAndApplyDateAndTenantIdOrderByProcessIdAsc(dto.getApplRefNo(),applyDate,tenantId);

    if (applications.isEmpty()) {
        throw new SPRuntimeError("Unable to process verification request",HttpStatus.BAD_REQUEST);
    }
    ApplicationTracking application = applications.get(0);
    Integer serviceId = application.getServiceId();
    String applId = application.getApplId();
    Map<String, Object> config =serviceMetadataFeignClient.getPublicTrackingConfiguration(serviceId.longValue(), tenantId);
    Object types = config.get("trackingTypes");
    if (!(types instanceof List<?> trackingTypes)|| !trackingTypes.contains("EMAIL_OTP")) {
        throw new SPRuntimeError("Email verification is not available",HttpStatus.BAD_REQUEST);
    }

    Object configuredEmail = config.get("emailAttribute");
    String emailAttributeId = configuredEmail instanceof Map<?, ?> field
            && field.get("attributeId") instanceof String id ? id : null;

    if (emailAttributeId == null|| emailAttributeId.isBlank()) {
        throw new SPRuntimeError("Email verification is not configured",HttpStatus.BAD_REQUEST);
    }

    Map<String, Object> values =fetchTrackingAttributes(tenantId,serviceId,applId,List.of(emailAttributeId));

    Object emailValue = values.get(emailAttributeId);

    if (!(emailValue instanceof String email)|| email.isBlank()) {
        throw new SPRuntimeError("Unable to process verification request",HttpStatus.BAD_REQUEST);
    }
    Map<String, Object> otpRequest = new HashMap<>();
    otpRequest.put("tenantId", tenantId);
    otpRequest.put("serviceId", serviceId);
    otpRequest.put("applId", applId);
    otpRequest.put("mode", "R");
    otpRequest.put("email", email);
    ResponseEntity<Map<String, Object>> response =userManagmentFeignClient.sendTrackingEmailOtp(trackingInternalToken,otpRequest);

    if (response == null|| !response.getStatusCode().is2xxSuccessful()|| response.getBody() == null|| !(response.getBody().get("challengeId")
                 instanceof String challengeId)) {
        throw new SPRuntimeError("Unable to initiate OTP verification",HttpStatus.BAD_GATEWAY);
    }
    return Map.of("challengeId", challengeId,"message", "OTP request processed");
}

    private Map<String,Object> modeOConfig(String tenant,int service,String method) {
        publicAuthorization.requireEnabled();
        if(trackingInternalToken==null || trackingInternalToken.isBlank()) throw new SPRuntimeError("Tracking internal authentication unavailable",HttpStatus.SERVICE_UNAVAILABLE);
        Map<String,Object> config=serviceMetadataFeignClient.getPublicTrackingConfiguration((long)service,tenant);
        if(config==null || !(config.get("serviceId") instanceof Number configuredService) || configuredService.longValue()!=service
            || !(config.get("trackingTypes") instanceof List<?> types)
            || types.isEmpty() || (method!=null && !types.contains(method))) throw PublicTrackingAuthorizationService.denied();
        return config;
    }

    private int modeOService(Map<String,Object> body) {
        if(!(body.get("serviceId") instanceof Number n) || n.longValue()<1 || n.longValue()>Integer.MAX_VALUE
            || n.doubleValue()!=n.intValue()) throw new SPRuntimeError("Invalid service",HttpStatus.BAD_REQUEST);
        return n.intValue();
    }

    private void onlyFields(Map<String,Object> body,String... allowed) {
        if(!Set.of(allowed).containsAll(body.keySet())) throw new SPRuntimeError("Unexpected verification fields",HttpStatus.BAD_REQUEST);
    }

    private Map<String,Object> matchModeO(String tenant,int service,String method,Map<String,Object> input) {
        Map<String,Object> lookup=new HashMap<>(input);
        lookup.put("tenantId",tenant);lookup.put("serviceId",service);lookup.put("method",method);
        return internalBody(formDesignerFeignClient.matchTrackingApplications(trackingInternalToken,lookup));
    }

    private Map<String,Object> internalBody(ResponseEntity<Map<String,Object>> response) {
        if(response==null || !response.getStatusCode().is2xxSuccessful() || response.getBody()==null)
            throw new SPRuntimeError("Verification dependency unavailable",HttpStatus.BAD_GATEWAY);
        return response.getBody();
    }

    private List<String> matchedIds(Map<String,Object> match) {
        if(!(match.get("applicationIds") instanceof List<?> ids) || ids.size()>100
            || ids.stream().anyMatch(id->!(id instanceof String text) || text.isBlank()))
            throw new SPRuntimeError("Verification dependency response invalid",HttpStatus.BAD_GATEWAY);
        return ids.stream().map(String.class::cast).distinct().toList();
    }

    public Map<String,Object> sendServiceTrackingOtp(Map<String,Object> body,HttpServletRequest request) {
        onlyFields(body,"serviceId","method","contact");
        int service=modeOService(body);String tenant=resolveTenantId(request);
        publicAuthorization.rateLimit(tenant,service,request.getRemoteAddr());
        String method=Objects.toString(body.get("method"),"");
        if(!Set.of("MOBILE_OTP","EMAIL_OTP").contains(method) || !(body.get("contact") instanceof String contact) || contact.length()>254)
            throw new SPRuntimeError("Invalid verification request",HttpStatus.BAD_REQUEST);
        Map<String,Object> config=modeOConfig(tenant,service,method);
        Map<String,Object> match=matchModeO(tenant,service,method,Map.of("contact",contact));
        List<String> ids=matchedIds(match);
        Map<String,Object> state=new HashMap<>();
        state.put("tenantId",tenant);state.put("serviceId",service);state.put("method",method);
        state.put("configuration",publicAuthorization.fingerprint(config));state.put("applicationIds",ids);
        if(!ids.isEmpty()) {
            if(!(match.get("contact") instanceof String recipient) || recipient.isBlank()) throw PublicTrackingAuthorizationService.denied();
            try {
                Map<String,Object> sent=internalBody(userManagmentFeignClient.sendTrackingOtp(trackingInternalToken,
                    Map.of("tenantId",tenant,"serviceId",service,"mode","O","method",method,"contact",recipient)));
                if(!(sent.get("challengeId") instanceof String id) || id.isBlank()) throw PublicTrackingAuthorizationService.denied();
                state.put("challengeId",id);state.put("contact",recipient);
            } catch(RuntimeException deliveryFailure) {
                int status=deliveryFailure instanceof feign.FeignException failure?failure.status():0;
                logger.warn("Tracking OTP initiation unavailable. dependency=UserManagement, serviceId={}, httpStatus={}, failureType={}",
                    service,status,deliveryFailure.getClass().getSimpleName());
                state.remove("challengeId");state.remove("contact");state.put("applicationIds",List.of());
            }
        }
        // Decoy state gives identical public responses for unmatched contacts. Never send to an unmatched contact.
        return Map.of("requestId",publicAuthorization.saveRequest(state),"message","If eligible, a verification code has been sent");
    }

    public Map<String,Object> verifyServiceTrackingOtp(Map<String,Object> body,HttpServletRequest request) {
        onlyFields(body,"serviceId","requestId","otp");
        int service=modeOService(body);String tenant=resolveTenantId(request);
        publicAuthorization.rateLimit(tenant,service,request.getRemoteAddr());
        Map<String,Object> state=publicAuthorization.request(Objects.toString(body.get("requestId"),""));
        if(!tenant.equals(state.get("tenantId")) || !Integer.valueOf(service).equals(state.get("serviceId"))) throw PublicTrackingAuthorizationService.denied();
        String method=Objects.toString(state.get("method"),"");
        Map<String,Object> config=modeOConfig(tenant,service,method);
        if(!publicAuthorization.fingerprint(config).equals(state.get("configuration")) || !state.containsKey("challengeId")) throw PublicTrackingAuthorizationService.denied();
        if(!(body.get("otp") instanceof String otp) || !otp.matches("\\d{6}")) throw PublicTrackingAuthorizationService.denied();
        Map<String,Object> response=internalBody(userManagmentFeignClient.verifyTrackingOtp(trackingInternalToken,
            Map.of("challengeId",state.get("challengeId"),"otp",otp)));
        if(!Boolean.TRUE.equals(response.get("verified")) || !(response.get("verificationData") instanceof Map<?,?> verified)
            || !tenant.equals(verified.get("tenantId")) || !Integer.valueOf(service).equals(verified.get("serviceId"))
            || !"O".equals(verified.get("mode")) || !method.equals(verified.get("method"))
            || !"PUBLIC_TRACKING".equals(verified.get("purpose")) || !state.get("contact").equals(verified.get("recipient"))) throw PublicTrackingAuthorizationService.denied();
        Map<String,Object> match=matchModeO(tenant,service,method,Map.of("contact",state.get("contact")));
        List<String> ids=new ArrayList<>(matchedIds(match));ids.retainAll(matchedIds(state));
        if(!publicAuthorization.consumeRequest((String)body.get("requestId"),state)) throw PublicTrackingAuthorizationService.denied();
        return authorizedModeOApplications(tenant,service,method,config,ids);
    }

    public Map<String,Object> verifyServiceTrackingAttributes(Map<String,Object> body,HttpServletRequest request) {
        onlyFields(body,"serviceId","values");
        int service=modeOService(body);String tenant=resolveTenantId(request);
        publicAuthorization.rateLimit(tenant,service,request.getRemoteAddr());
        Map<String,Object> config=modeOConfig(tenant,service,"FORM_ATTRIBUTES");
        if(!(body.get("values") instanceof Map<?,?> values) || values.size()<3 || values.size()>5) throw PublicTrackingAuthorizationService.denied();
        Map<String,Object> match=matchModeO(tenant,service,"FORM_ATTRIBUTES",Map.of("values",values));
        if(Boolean.TRUE.equals(match.get("sufficient"))) {
            List<String> ids=matchedIds(match);
            if(ids.size()==1) return authorizedModeOApplications(tenant,service,"FORM_ATTRIBUTES",config,ids);
        }
        return Map.of("message","Additional verification required","additionalVerificationRequired",true);
    }

    private Map<String,Object> authorizedModeOApplications(String tenant,int service,String method,Map<String,Object> config,List<String> ids) {
        List<Map<String,Object>> applications=new ArrayList<>();
        String configuration=publicAuthorization.fingerprint(config);
        for(String id:ids) {
            List<ApplicationTracking> history=trackingRepository.findByApplIdAndTenantIdOrderByProcessIdAsc(id,tenant);
            if(history.isEmpty() || history.stream().anyMatch(row->!Integer.valueOf(service).equals(row.getServiceId()))) continue;
            ApplicationTracking first=history.getFirst();Map<String,Object> item=new LinkedHashMap<>();
            item.put("applId",id);item.put("applRefNo",first.getApplRefNo());item.put("applyDate",first.getApplyDate());
            item.put("serviceName",first.getServiceName());
            item.put("verificationProof",publicAuthorization.issue(tenant,service,id,method,configuration));
            applications.add(item);
        }
        return Map.of("applications",applications);
    }

}
