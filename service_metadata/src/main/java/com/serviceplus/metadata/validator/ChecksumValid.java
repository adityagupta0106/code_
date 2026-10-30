package com.serviceplus.metadata.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ChecksumValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ChecksumValid {
    String message() default "Invalid checksum";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
