package com.example.exchangerates.controller;

import com.example.exchangerates.ExchangeRateCurrencies;
import com.example.exchangerates.dto.UpdateExchangeRatesResponse;
import com.example.exchangerates.model.ExchangeRateResponse;
import com.example.exchangerates.service.ExchangeRateService;
import com.example.exchangerates.validation.CurrencyConstraint;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class ExchangeRateController {

    ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @PutMapping("/exchange-rates")
    public ResponseEntity<UpdateExchangeRatesResponse> updateExchangeRates() {
        return ResponseEntity.ok(exchangeRateService.updateExchangeRates());
    }

    @GetMapping("/exchange-rates/{baseCurrency}/{toCurrency}")
    public ResponseEntity<ExchangeRateResponse> getExchangeRate
            (@Valid @PathVariable @CurrencyConstraint String baseCurrency,
             @Valid @PathVariable @CurrencyConstraint String toCurrency) {
        return ResponseEntity.ok(exchangeRateService.getExchangeRate
                (ExchangeRateCurrencies.valueOf(baseCurrency), ExchangeRateCurrencies.valueOf(toCurrency)));
    }
}
