package com.serviceplus.metadata.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChecksumValidator implements ConstraintValidator<ChecksumValid, String> {
	
	
    @Override
    public void initialize(ChecksumValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(String checksum, ConstraintValidatorContext context) {
        if (checksum == null) {
            return false;
        }
        
       
      
        return true;
    }
}
