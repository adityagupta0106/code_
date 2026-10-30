package com.serviceplus.formDesigner.FormApiExecution.Validation;

import com.serviceplus.formDesigner.FormApiExecution.Model.ValidationJwtClaims;
import org.springframework.validation.ObjectError;

import java.util.List;

public interface ValidationJwtService {

    Object validate(String token, String attributeId, Object submittedValue);

    ValidationJwtClaims parse(String token);
}
