package com.example.exchangerates.service;

import com.example.exchangerates.ExchangeRateCurrencies;
import com.example.exchangerates.dto.UpdateExchangeRatesResponse;
import com.example.exchangerates.exception.ErrorMessages;
import com.example.exchangerates.model.CurrencyWithExchangeRates;
import com.example.exchangerates.model.ExchangeRateResponse;
import com.example.exchangerates.util.HTTPUtil;
import com.example.exchangerates.util.XMLFileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExchangeRateService {

    @Value("${exchange.rates.service.base.url}")
    private String exchangeRatesServiceBaseURL;

    @Value("${exchange.rates.service.api.key}")
    private String exchangeRatesServiceAPIKey;

    @Value("${exchange.rates.files.location}")
    private String exchangeRatesFilesLocation;

    @Value("${exchange.rates.service.response.fields.exchange.rates}")
    private String exchangeRatesServiceResponseFields_exchangeRates;

    private XMLFileUtil xmlFileUtil;

    private HTTPUtil httpUtil;

    public ExchangeRateService(XMLFileUtil xmlFileUtil, HTTPUtil httpUtil) {
        this.xmlFileUtil = xmlFileUtil;
        this.httpUtil = httpUtil;
    }

    public UpdateExchangeRatesResponse updateExchangeRates() {
        Map<ExchangeRateCurrencies, String> exchangeRatesResponses = fetchExchangeRates();
        List<CurrencyWithExchangeRates> currenciesWithExchangeRates = parseExchangeRatesResponses(exchangeRatesResponses);
        String fileName = getFileName();
        String fileNameWithXMLExtension = xmlFileUtil.getFileNameWithExtension(fileName);
        xmlFileUtil.writeFile(currenciesWithExchangeRates, fileNameWithXMLExtension);
        xmlFileUtil.validateFile(fileNameWithXMLExtension);
        return new UpdateExchangeRatesResponse(UpdateExchangeRatesResponse.UpdateStatus.SUCCESS,fileNameWithXMLExtension);
    }

    public ExchangeRateResponse getExchangeRate(ExchangeRateCurrencies baseCurrency, ExchangeRateCurrencies toCurrency) {
        Map <ExchangeRateCurrencies, String> exchangeRateServiceResponse = fetchExchangeRates(baseCurrency);
        List<CurrencyWithExchangeRates> currencyWithExchangeRates =
                parseExchangeRatesResponses(exchangeRateServiceResponse);
        Optional<Double> exchangeRate = Optional.ofNullable(
                currencyWithExchangeRates.get(0)
                .getExchangeRates()
                .get(Currency.getInstance(toCurrency.name())));
        if(exchangeRate.isEmpty())
            throw(new RuntimeException(ErrorMessages.EXCHANGE_RATE_NOT_FOUND.getMessage()));
        return new ExchangeRateResponse
                (baseCurrency, toCurrency, exchangeRate.get());
    }

    public Map<ExchangeRateCurrencies, String> fetchExchangeRates() {
        Map<ExchangeRateCurrencies,String> exchangeRatesServiceResponses = new HashMap<>();
        for(ExchangeRateCurrencies exchangeRateCurrency : ExchangeRateCurrencies.values()) {
            exchangeRatesServiceResponses.put(exchangeRateCurrency,
                    httpUtil.makeHTTPRequest(exchangeRatesServiceBaseURL +
                            exchangeRatesServiceAPIKey + "/latest/" + exchangeRateCurrency.name()));
        }
        return exchangeRatesServiceResponses;
    }

    public Map<ExchangeRateCurrencies, String> fetchExchangeRates(ExchangeRateCurrencies baseCurrency) {
        Map<ExchangeRateCurrencies, String> exchangeRateResponse = new HashMap<>();
        exchangeRateResponse.put(baseCurrency,
                httpUtil.makeHTTPRequest(exchangeRatesServiceBaseURL +
                        exchangeRatesServiceAPIKey + "/latest/" + baseCurrency.name()));
        return exchangeRateResponse;
    }

    private List<CurrencyWithExchangeRates> parseExchangeRatesResponses(Map<ExchangeRateCurrencies, String> exchangeRatesResponses) {
        List<CurrencyWithExchangeRates> currenciesWithExchangeRates = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Map.Entry<ExchangeRateCurrencies, String> entry : exchangeRatesResponses.entrySet()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(entry.getValue());
                String exchangeRates = jsonNode.get(exchangeRatesServiceResponseFields_exchangeRates).toString();
                CurrencyWithExchangeRates currencyWithExchangeRates
                        = new CurrencyWithExchangeRates(entry.getKey(),
                        objectMapper.readValue(exchangeRates, new TypeReference<>(){}));
                if(currencyWithExchangeRates.getExchangeRates().size() > 0)
                    currenciesWithExchangeRates.add(currencyWithExchangeRates);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(ErrorMessages.ERROR_PARSING_EXCHANGE_RATE_SERVICE_RESPONSE.getMessage(), e);
            }
        }
        return currenciesWithExchangeRates;
    }

    private String getFileName() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        String folderName = DateTimeFormatter.ISO_LOCAL_DATE.format(now);
        String fileName = DateTimeFormatter.ISO_LOCAL_TIME.format(now);
        return exchangeRatesFilesLocation + "/" + folderName + "/" + fileName;
    }
}
