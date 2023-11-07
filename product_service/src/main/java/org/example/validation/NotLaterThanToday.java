package org.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target( {ElementType.FIELD, ElementType.PARAMETER} )
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NotLaterThanTodayValidator.class)
public @interface NotLaterThanToday {

    public String message() default "Date must be no later than this day";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
