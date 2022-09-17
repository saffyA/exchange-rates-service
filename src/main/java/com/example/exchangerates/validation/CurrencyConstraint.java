package com.example.exchangerates.validation;

import com.example.exchangerates.ExchangeRateCurrencies;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CurrencyValidator.class)
@Target( {ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrencyConstraint {
    String message() default "Invalid Currency";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
