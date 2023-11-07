package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class NotLaterThanTodayValidator implements ConstraintValidator<NotLaterThanToday, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDate.now()) || value.isEqual(LocalDate.now());
    }
}
