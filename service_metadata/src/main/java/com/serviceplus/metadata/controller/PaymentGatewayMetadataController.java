package com.serviceplus.metadata.controller;

import com.serviceplus.metadata.dto.PaymentGatewayConfigurationListResponse;
import com.serviceplus.metadata.dto.PaymentGatewayConfigurationResponse;
import com.serviceplus.metadata.dto.PaymentGatewayMappingRequest;
import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.service.PaymentGatewayMetadataService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.serviceplus.metadata.utility.CommonUtil.getUserSessionDetails;
import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/a")
public class PaymentGatewayMetadataController {

    private static final Logger log = LoggerFactory.getLogger("paymentGatewayLogger");

    private final PaymentGatewayMetadataService paymentGatewayMetadataService;

    public PaymentGatewayMetadataController(PaymentGatewayMetadataService paymentGatewayMetadataService) {
        this.paymentGatewayMetadataService = paymentGatewayMetadataService;
    }

    @GetMapping("/payment-gateway/configuration")
    public ResponseEntity<PaymentGatewayConfigurationResponse> getPaymentGatewayConfiguration(@RequestParam Integer serviceId,
                                                                                              HttpServletRequest request,
                                                                                              @RequestParam String gatewayCode) {

        log.info("Payment gateway configuration request. serviceId={}", serviceId);

        UserSessionDTO user = requireNonNull(getUserSessionDetails(request));

        return ResponseEntity.ok(paymentGatewayMetadataService.getConfiguration(serviceId,gatewayCode,user));
    }

    @PostMapping("/payment-gateway/configuration/save")
    public ResponseEntity<PaymentGatewayConfigurationResponse> savePaymentGatewayConfiguration(@RequestParam Integer serviceId,
                                                                                               @RequestBody PaymentGatewayMappingRequest request,
                                                                                               HttpServletRequest httpServletRequest) {

        log.info("Payment gateway configuration save request. serviceId={}, gatewayCode={}", serviceId, request.getGatewayCode());

        UserSessionDTO user = requireNonNull(getUserSessionDetails(httpServletRequest));

        return ResponseEntity.ok(paymentGatewayMetadataService.saveConfiguration(serviceId,request,user));
    }

    @GetMapping("/payment-gateway/configurations")
    public ResponseEntity<List<PaymentGatewayConfigurationListResponse>> getPaymentGatewayConfigurations(@RequestParam Integer serviceId, HttpServletRequest request) {

        log.info("Payment gateway configurations list request. serviceId={}", serviceId);

        UserSessionDTO user = requireNonNull(getUserSessionDetails(request));

        return ResponseEntity.ok(paymentGatewayMetadataService.getConfigurations(serviceId, user));
    }
}
