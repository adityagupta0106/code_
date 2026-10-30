package com.serviceplus.metadata.dto;

//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import java.util.Base64;
//import java.util.regex.Pattern;
//
//import com.serviceplus.metadata.dto.ServiceDefinitionDTO.LogoDTO;
//
//public class LogoValidator implements ConstraintValidator<ValidLogo, LogoDTO> {
//
//    private static final Pattern BASE64_PATTERN = Pattern.compile("^data:image/(png|jpeg|jpg);base64,[A-Za-z0-9+/=]+$");
//
//    @Override
//    public boolean isValid(LogoDTO logo, ConstraintValidatorContext context) {
//        if (logo == null) {
//            return true; // Null check is handled by @NotNull if required
//        }
//
//        if (!BASE64_PATTERN.matcher(logo.getBase64()).matches()) {
//            context.buildConstraintViolationWithTemplate("Base64 string must be a valid image data URL")
//                   .addPropertyNode("base64")
//                   .addConstraintViolation();
//            return false;
//        }
//
//        try {
//            String base64Data = logo.getBase64().split(",")[1];
//            Base64.getDecoder().decode(base64Data);
//            return true;
//        } catch (IllegalArgumentException e) {
//            context.buildConstraintViolationWithTemplate("Invalid base64 encoding")
//                   .addPropertyNode("base64")
//                   .addConstraintViolation();
//            return false;
//        }
//    }
//}