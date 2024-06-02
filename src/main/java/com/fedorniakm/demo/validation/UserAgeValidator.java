package com.fedorniakm.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.Objects;

public class UserAgeValidator implements ConstraintValidator<ValidUserAge, LocalDate> {

    @Value("${user.age.min}")
    private Integer minAge;

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (Objects.isNull(birthDate)) {
            return true;
        }
        return LocalDate.now().minusYears(minAge).isAfter(birthDate);
    }
}
