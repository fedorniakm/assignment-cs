package com.fedorniakm.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserAgeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserAge {

    String message() default "User age must be over 18 y. o.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
