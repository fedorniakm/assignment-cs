package com.fedorniakm.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class NullOrNotEmptyStringValidator implements ConstraintValidator<NullOrNotEmptyString, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Objects.isNull(value) || !value.isEmpty();
    }
}
