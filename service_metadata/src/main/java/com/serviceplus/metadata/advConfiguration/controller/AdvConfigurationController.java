package com.serviceplus.metadata.advConfiguration.controller;

import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationDeleteRequestDTO;
import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationResponseDTO;
import com.serviceplus.metadata.advConfiguration.dto.AdvConfigurationSaveRequestDTO;
import com.serviceplus.metadata.advConfiguration.service.AdvConfigurationService;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.utility.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adv/configuration")
public class AdvConfigurationController {

    private static final Logger log = LoggerFactory.getLogger(AdvConfigurationController.class);

    private final AdvConfigurationService advConfigurationService;

    public AdvConfigurationController(AdvConfigurationService advConfigurationService) {
        this.advConfigurationService = advConfigurationService;
    }

    
    @GetMapping
    public ResponseEntity<?> getConfiguration(
            @RequestParam(required = false) Integer serviceId,
            @RequestParam(required = false) Integer baseServiceId,
            @RequestParam(required = false) Long advConfigId) {
    	List<AdvConfigurationResponseDTO> list=new ArrayList<>();
        if (advConfigId != null) {
            log.info("REST GET ADV configuration by advConfigId query param: {}", advConfigId);
            AdvConfigurationResponseDTO response = advConfigurationService.getConfigurationById(advConfigId);
            return ResponseEntity.ok(response);
        }
        if (serviceId != null) {
            log.info("REST GET ADV configuration by serviceId query param: {}", serviceId);
            list = advConfigurationService.getConfigurationsByServiceId(serviceId);
            return ResponseEntity.ok(list);
        }
        if (baseServiceId != null) {
            log.info("REST GET ADV configuration by baseServiceId query param: {}", baseServiceId);
            list = advConfigurationService.getConfigurationsByBaseServiceId(baseServiceId);
            return ResponseEntity.ok(list);
        }
        list = advConfigurationService.getAllConfigurations(0, 0);
        return ResponseEntity.ok(list);

    }
    /**
     * Create or update ADV configuration using request body DTO.
     */
    @PostMapping(value = "/save")
    public ResponseEntity<AdvConfigurationResponseDTO> saveConfiguration(
            @Valid @RequestBody AdvConfigurationSaveRequestDTO requestDTO,
            HttpServletRequest request) {
        log.info("REST POST ADV configuration save. serviceId: {}, baseServiceId: {}",
                requestDTO.getServiceId(), requestDTO.getBaseServiceId());
        UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
        AdvConfigurationResponseDTO response = advConfigurationService.saveConfiguration(requestDTO, userSession);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete ADV configuration using query parameters or request body DTO.
     */
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteConfiguration(
            @RequestBody AdvConfigurationDeleteRequestDTO deleteDTO,
            HttpServletRequest request) {

        log.info("REST POST ADV configuration delete");
        UserSessionDTO userSession = CommonUtil.getUserSessionDetails(request);
       
        int deletedCount = advConfigurationService.deleteConfiguration(deleteDTO, userSession);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("deletedCount", deletedCount);
        if (deleteDTO.getAdvConfigId() != null) {
            result.put("advConfigId", deleteDTO.getAdvConfigId());
        }
        if (deleteDTO.getServiceId() != null) {
            result.put("serviceId", deleteDTO.getServiceId());
        }
        return ResponseEntity.ok(result);
    }
}
