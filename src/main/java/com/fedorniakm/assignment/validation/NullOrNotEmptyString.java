package com.fedorniakm.assignment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullOrNotEmptyStringValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotEmptyString {
    String message() default "String value cannot be empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
