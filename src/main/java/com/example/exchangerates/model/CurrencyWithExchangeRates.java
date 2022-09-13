package com.example.exchangerates.model;

import com.example.exchangerates.ExchangeRateCurrencies;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CurrencyWithExchangeRates {
    Currency baseCurrency;
    Map<Currency, Double> exchangeRates = new HashMap<>();

    public CurrencyWithExchangeRates(Currency baseCurrency, Map<String, Double> exchangeRates) {
        this.baseCurrency = baseCurrency;
        exchangeRates.forEach((key ,value) ->  {
            try {
                ExchangeRateCurrencies exchangeRateCurrency = ExchangeRateCurrencies.valueOf(key);
                if(Currency.getInstance(exchangeRateCurrency.name()) != baseCurrency)
                    this.exchangeRates.put(Currency.getInstance(key), value);
            }
            catch (IllegalArgumentException ignored){}
        });
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Map<Currency, Double> getExchangeRates() {
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
