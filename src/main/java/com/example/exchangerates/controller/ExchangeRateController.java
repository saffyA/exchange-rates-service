package com.example.exchangerates.controller;

import com.example.exchangerates.dto.UpdateExchangeRatesResponse;
import com.example.exchangerates.service.ExchangeRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ExchangeRateController {

    ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @PutMapping("/exchange-rates")
    public ResponseEntity<UpdateExchangeRatesResponse> updateExchangeRates() {
        return ResponseEntity.ok(exchangeRateService.updateExchangeRates());
    }
}
