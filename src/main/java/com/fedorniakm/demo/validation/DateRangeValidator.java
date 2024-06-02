package com.fedorniakm.demo.validation;

import com.fedorniakm.demo.model.DateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class DateRangeValidator
        implements ConstraintValidator<ValidDateRange, DateRange> {

    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext context) {
        if (Objects.isNull(dateRange)
            || Objects.isNull(dateRange.getFrom())
            || Objects.isNull(dateRange.getTo())) {
            return true;
        }
        return dateRange.getFrom().isBefore(dateRange.getTo());
    }
}
