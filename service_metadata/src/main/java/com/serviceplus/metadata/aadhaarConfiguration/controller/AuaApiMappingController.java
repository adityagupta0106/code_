package com.serviceplus.metadata.aadhaarConfiguration.controller;

import com.serviceplus.metadata.aadhaarConfiguration.dto.AadhaarDefinerConfigurationResponse;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AuaApiMappingResponse;
import com.serviceplus.metadata.aadhaarConfiguration.dto.AuaMastersResponse;
import com.serviceplus.metadata.aadhaarConfiguration.dto.SaveAuaApiMappingRequest;
import com.serviceplus.metadata.aadhaarConfiguration.service.AuaApiMappingService;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.exception.SPRuntimeError;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.serviceplus.metadata.utility.CommonUtil.getUserSessionDetails;
import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/a")
public class AuaApiMappingController {

    private final AuaApiMappingService mappingService;

    public AuaApiMappingController(AuaApiMappingService mappingService) {
        this.mappingService = mappingService;
    }

//    @GetMapping("/aua/mapping")
//    public ResponseEntity<List<AuaApiMappingResponse>> getApiMapping(@RequestParam Integer serviceId,@RequestParam Long auaId, HttpServletRequest request) {
//        try {
//            return ResponseEntity.ok(mappingService.getMapping(serviceId,auaId, request));
//        } catch (Exception e) {
//            if(e instanceof SPRuntimeError)
//                throw e;
//
//            e.printStackTrace();
//            throw new SPRuntimeError("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/aua/mapping/save")
    public ResponseEntity<Void> saveMappings(@RequestParam Integer serviceId,
                                             @RequestParam String configurationId,
                                             @RequestBody SaveAuaApiMappingRequest data,
                                             HttpServletRequest request) {

        try {
            mappingService.saveMappings(serviceId, configurationId, data, request);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            if (e instanceof SPRuntimeError) {
                throw (SPRuntimeError) e;
            }

            e.printStackTrace();
            throw new SPRuntimeError("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/aua/definer-configuration")
    public ResponseEntity<AadhaarDefinerConfigurationResponse> getDefinerConfiguration(@RequestParam Integer serviceId,
                                                                                       @RequestParam String configurationId,
                                                                                       HttpServletRequest httpRequest) {

        UserSessionDTO user = requireNonNull(getUserSessionDetails(httpRequest), "User session not found");

        AadhaarDefinerConfigurationResponse response = mappingService.getDefinerConfiguration(serviceId, configurationId, user);

        return ResponseEntity.ok(response);
    }
}
