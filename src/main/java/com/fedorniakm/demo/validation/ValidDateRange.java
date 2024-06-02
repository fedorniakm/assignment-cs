package com.fedorniakm.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "Invalid date range: 'from' must be before 'to'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
