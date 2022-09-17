package com.example.exchangerates.model;

import com.example.exchangerates.ExchangeRateCurrencies;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CurrencyWithExchangeRates {
    ExchangeRateCurrencies baseCurrency;
    Map<ExchangeRateCurrencies, Double> exchangeRates = new HashMap<>();

    public CurrencyWithExchangeRates(ExchangeRateCurrencies baseCurrency, Map<String, Double> exchangeRates) {
        this.baseCurrency = baseCurrency;
        exchangeRates.forEach((key ,value) ->  {
            try {
                ExchangeRateCurrencies exchangeRateCurrency = ExchangeRateCurrencies.valueOf(key);
                if(exchangeRateCurrency != baseCurrency)
                    this.exchangeRates.put(exchangeRateCurrency, value);
            }
            catch (IllegalArgumentException ignored){}
        });
    }

    public ExchangeRateCurrencies getBaseCurrency() {
        return baseCurrency;
    }

    public Map<ExchangeRateCurrencies, Double> getExchangeRates() {
        return exchangeRates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyWithExchangeRates that = (CurrencyWithExchangeRates) o;
        return Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(exchangeRates, that.exchangeRates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, exchangeRates);
    }
}
