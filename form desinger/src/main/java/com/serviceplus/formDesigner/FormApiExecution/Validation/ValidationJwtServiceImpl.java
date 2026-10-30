package com.serviceplus.formDesigner.FormApiExecution.Validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.FormApiExecution.Model.ValidationJwtClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.*;

import static com.serviceplus.formDesigner.util.CommonUtil.loadPublicKey;

@Service
public class ValidationJwtServiceImpl implements ValidationJwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger("webServiceCallLogger");

    @Value("${form.validation.jwt.expiry.minutes}")
    private Long expiryMinutes;

    @Value("${form.validation.jwt.public-key}")
    private Resource publicKeyResource;

    private PublicKey publicKey;

    private final ObjectMapper objectMapper;

    public ValidationJwtServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws Exception {
        publicKey = loadPublicKey(publicKeyResource);
    }

    @Override
    public Object validate(String token, String attributeId, Object submittedValue) {

        try {

            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Map<String, ValidationJwtClaims.ValidationAttributeClaim> attributes = objectMapper
                    .convertValue(
                            claims.get("attributes"),
                            new TypeReference<Map<String, ValidationJwtClaims.ValidationAttributeClaim>>() {
                            });

            if (attributes == null || attributes.isEmpty()) {
                LOGGER.warn("Attributes claim not found.");
                return false;
            }

            ValidationJwtClaims.ValidationAttributeClaim attribute = attributes.get(attributeId);

            if (attribute == null) {
                LOGGER.warn("No validation data found for attribute [{}]", attributeId);
                return false;
            }

            List<Object> allowedValues = attribute.getAllowedValues();

            if (allowedValues == null || allowedValues.isEmpty()) {

                List<Map<String, Object>> dynamicData = attribute.getDynamicData();

                if (dynamicData == null || dynamicData.isEmpty()) {
                    return false;
                }

                Object actualValue = dynamicData.getFirst().get("value");

                return Objects.equals(String.valueOf(actualValue), String.valueOf(submittedValue));
            }

            boolean valid = allowedValues.stream().anyMatch(value -> Objects.equals(String.valueOf(value), String.valueOf(submittedValue)));

            LOGGER.info("Validation for attribute [{}] : {}", attributeId, valid);

            return valid;

        } catch (JwtException ex) {

            LOGGER.error("JWT validation failed : {}", ex.getMessage(), ex);
            return false;

        } catch (Exception ex) {

            LOGGER.error("Error while validating JWT : {}", ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public ValidationJwtClaims parse(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        ValidationJwtClaims jwtClaims = new ValidationJwtClaims();
        jwtClaims.setApiId(claims.get("apiId", String.class));

        Map<String, ValidationJwtClaims.ValidationAttributeClaim> attributes =
                objectMapper.convertValue(
                        claims.get("attributes"),
                        new TypeReference<Map<String, ValidationJwtClaims.ValidationAttributeClaim>>() {});

        jwtClaims.setAttributes(attributes);

        return jwtClaims;
    }

}
