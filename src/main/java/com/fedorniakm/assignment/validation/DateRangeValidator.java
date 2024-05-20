package com.fedorniakm.assignment.validation;

import com.fedorniakm.assignment.model.DateRange;
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
