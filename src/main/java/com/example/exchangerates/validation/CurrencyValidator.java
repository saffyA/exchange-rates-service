package com.example.exchangerates.validation;

import com.example.exchangerates.ExchangeRateCurrencies;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<CurrencyConstraint, String> {
    @Override
    public void initialize(CurrencyConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        try {
            ExchangeRateCurrencies.valueOf(input);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }
}
