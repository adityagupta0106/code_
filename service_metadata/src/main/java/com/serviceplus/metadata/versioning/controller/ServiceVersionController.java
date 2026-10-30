package com.serviceplus.metadata.versioning.controller;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.utility.GenericJwtService;
import com.serviceplus.metadata.versioning.dto.StartModificationResponse;
import com.serviceplus.metadata.versioning.enums.ServiceComponent;
import com.serviceplus.metadata.versioning.enums.VersionType;
import com.serviceplus.metadata.versioning.service.ServiceVersionHistoryService;
import com.serviceplus.metadata.versioning.service.ServiceVersionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.serviceplus.metadata.utility.CommonUtil.getUserSessionDetails;

@RestController
@RequestMapping("/a")
public class ServiceVersionController {

    private final ServiceVersionService serviceVersionService;
    private final GenericJwtService genericJwtService;
    private final com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService;

    // Cookie age set to 3 hours (3 * 60 * 60 = 10,800 seconds)
    private static final int THREE_HOURS_SECONDS = 3 * 60 * 60;

    @Value("${service.cookie.https:false}")
    private boolean isHttps;

    @Value("${service.cookie.sameSite:Lax}")
    private String sameSite;

    public ServiceVersionController(
            ServiceVersionService serviceVersionService,
            GenericJwtService genericJwtService,
            com.serviceplus.metadata.versioning.service.ServiceVersionTokenService serviceVersionTokenService) {
        this.serviceVersionService = serviceVersionService;
        this.genericJwtService = genericJwtService;
        this.serviceVersionTokenService = serviceVersionTokenService;
    }

    @PostMapping("/version/modify")
    public ResponseEntity<StartModificationResponse> startModification(
            @RequestParam Integer serviceId,
            @RequestParam VersionType versionType,
            HttpServletResponse response, HttpServletRequest request) {

        UserSessionDTO user =  getUserSessionDetails(request);

        StartModificationResponse result = serviceVersionService.startModification(
                serviceId,
                versionType,
                user
        );

        // Populate claims generically
        Map<String, Object> claims = new HashMap<>();
        claims.put("modificationToken", result.getModificationToken());
        claims.put("modificationId", result.getModificationId());
        claims.put("serviceId", result.getTargetServiceId());
        claims.put("baseServiceId", result.getBaseServiceId());
        claims.put("versionType", result.getVersionType().name());

        if (user != null) {
            claims.put("atk", user.getAtk() != null ? user.getAtk() : "");
            claims.put("userId", user.getUserID());
            claims.put("tenantId", user.getTenantId());
        }

        // Generate generic JWT signed with 3 hours expiration
        String jwtToken = genericJwtService.generateToken(result.getModificationToken(), claims);

        // Issue only 1 cookie: SERVICE_MODIFICATION_JWT valid for 3 hours
        ResponseCookie jwtCookie = ResponseCookie
                .from("SERVICE_MODIFICATION_JWT", jwtToken)
                .httpOnly(true)
                .secure(isHttps)
                .sameSite(sameSite)
                .path("/")
                .maxAge(THREE_HOURS_SECONDS)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok(result);
    }

    /**
     * Cancel modification endpoint:
     * Does NOT strictly depend on cookie or token being present.
     * Can receive modificationToken (header or cookie) OR serviceId parameter.
     * Validates user and tenant ownership on the server side and reverts the modification.
     */
    @PostMapping("/version/cancel")
    public ResponseEntity<Void> cancelModification(
            @RequestParam(required = false) Integer serviceId,
            @RequestHeader(value = "X-Service-Modification-Token", required = false) String headerToken,
            @CookieValue(value = "SERVICE_MODIFICATION_JWT", required = false) String cookieJwt,
            HttpServletResponse response,HttpServletRequest request) {

        UserSessionDTO user = getUserSessionDetails(request);

        String token = headerToken;
        if ((token == null || token.isBlank()) && cookieJwt != null && !cookieJwt.isBlank()) {
            token = (String) genericJwtService.getClaim(cookieJwt, "modificationToken");
            if (token == null) {
                token = genericJwtService.extractSubject(cookieJwt);
            }
        }

        serviceVersionService.cancelModification(token, serviceId, user);

        // Clear the JWT cookie upon cancellation
        ResponseCookie clearCookie = ResponseCookie
                .from("SERVICE_MODIFICATION_JWT", "")
                .httpOnly(true)
                .secure(isHttps)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.ok().build();
    }

    /**
     * Service Copy Endpoint:
     * Creates a brand-new independent service with component tables copied into a new base serviceId.
     * By default, omits SMS configuration and Aadhaar/AUA mappings (or allows callers to customize exclusions).
     */
    @PostMapping("/version/copy")
    public ResponseEntity<Map<String, Object>> copyService(
            @RequestParam Integer sourceServiceId,
            @RequestParam(required = false) String newServiceName,
            @RequestParam(required = false) String newServiceAbbreviation,
            @RequestParam(required = false) List<String> excludedComponents,
            @RequestParam(required = false) Boolean copyNotifications,
            @RequestParam(required = false) Boolean copyAuaMappings,
            UserSessionDTO userSessionDetails) {

        Set<ServiceComponent> exclusions = new HashSet<>(ServiceVersionHistoryService.DEFAULT_COPY_SERVICE_EXCLUSIONS);

        if (excludedComponents != null && !excludedComponents.isEmpty()) {
            for (String compStr : excludedComponents) {
                if (compStr != null && !compStr.isBlank()) {
                    try {
                        exclusions.add(ServiceComponent.valueOf(compStr.trim().toUpperCase()));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        if (Boolean.TRUE.equals(copyNotifications)) {
            exclusions.remove(ServiceComponent.NOTIFICATION);
        }
        if (Boolean.TRUE.equals(copyAuaMappings)) {
            exclusions.remove(ServiceComponent.AUA_MAPPING);
        }

        Map<String, Object> result = serviceVersionService.copyService(
                sourceServiceId,
                newServiceName,
                newServiceAbbreviation,
                exclusions,
                userSessionDetails
        );

        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint to view detailed changes (previous config vs current config, diff summary)
     * made during service modification.
     */
    @GetMapping("/version/change-log")
    public ResponseEntity<com.serviceplus.metadata.versioning.dto.ServiceVersionChangesResponse> getChangeLog(@RequestParam Integer serviceId) {
        return ResponseEntity.ok(serviceVersionTokenService.getDetailedChanges(serviceId));
    }
}