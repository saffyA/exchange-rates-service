package com.example.exchangerates.model;

import com.example.exchangerates.ExchangeRateCurrencies;

import java.util.Currency;

public class ExchangeRateResponse {
    ExchangeRateCurrencies baseCurrency;
    ExchangeRateCurrencies toCurrency;
    Double exchangeRate;

    public ExchangeRateResponse(ExchangeRateCurrencies baseCurrency, ExchangeRateCurrencies toCurrency, Double exchangeRate) {
        this.baseCurrency = baseCurrency;
        this.toCurrency = toCurrency;
        this.exchangeRate = exchangeRate;
    }

    public ExchangeRateCurrencies getBaseCurrency() {
        return baseCurrency;
    }

    public ExchangeRateCurrencies getToCurrency() {
        return toCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

}
