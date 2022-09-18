package com.example.exchangerates.model;

import com.example.exchangerates.ExchangeRateCurrencies;

import java.util.Currency;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateResponse that = (ExchangeRateResponse) o;
        return baseCurrency == that.baseCurrency && toCurrency == that.toCurrency && Objects.equals(exchangeRate, that.exchangeRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, toCurrency, exchangeRate);
    }
}
