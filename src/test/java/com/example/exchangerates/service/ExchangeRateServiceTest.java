package com.example.exchangerates.service;

import com.example.exchangerates.ExchangeRateCurrencies;
import com.example.exchangerates.dto.UpdateExchangeRatesResponse;
import com.example.exchangerates.exception.ErrorMessages;
import com.example.exchangerates.model.CurrencyWithExchangeRates;
import com.example.exchangerates.util.HTTPUtil;
import com.example.exchangerates.util.XMLFileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private static XMLFileUtil xmlFileUtil = mock(XMLFileUtil.class);
    private static HTTPUtil httpUtil = mock(HTTPUtil.class);
    @Spy
    private static ExchangeRateService exchangeRateService = new ExchangeRateService(xmlFileUtil, httpUtil);
    private static Map<ExchangeRateCurrencies,String> expectedExchangeRateResponses = new HashMap<>();
    private static List<CurrencyWithExchangeRates> expectedCurrenciesWithExchangeRates = new ArrayList<>();
    private static String DUMMY_DATA_FILE_USD = "dummyExchangeRatesResponseUSD.json";
    private static String DUMMY_DATA_FILE_EUR = "dummyExchangeRatesResponseEUR.json";

    ExchangeRateServiceTest() throws Exception{
    }

    @BeforeAll
    public static void setExpectedData() throws URISyntaxException, IOException {
        //should be in sync with dummy data & @Value fields in ExchangeRateService
        ReflectionTestUtils.setField(exchangeRateService,"exchangeRatesServiceResponseFields_exchangeRates","conversion_rates");
        ReflectionTestUtils.setField(exchangeRateService,"exchangeRatesFilesLocation","exchange-rates");
        expectedExchangeRateResponses.put(ExchangeRateCurrencies.USD,
                Files.readString(Path.of(ClassLoader.getSystemResource(DUMMY_DATA_FILE_USD).toURI())));
        expectedExchangeRateResponses.put(ExchangeRateCurrencies.EUR,
                Files.readString(Path.of(ClassLoader.getSystemResource(DUMMY_DATA_FILE_EUR).toURI())));
        Map<String, Double> USDExchangeRates = new HashMap<>();
        USDExchangeRates.put("EUR", 0.9961);
        USDExchangeRates.put("GBP", 0.865);
        USDExchangeRates.put("CHF", 0.9613);
        expectedCurrenciesWithExchangeRates.add(new CurrencyWithExchangeRates(ExchangeRateCurrencies.USD,USDExchangeRates));
        Map<String, Double> EURExchangeRates = new HashMap<>();
        EURExchangeRates.put("USD", 1.0039);
        EURExchangeRates.put("GBP", 0.8684);
        EURExchangeRates.put("CHF", 0.9652);
        expectedCurrenciesWithExchangeRates.add(new CurrencyWithExchangeRates(ExchangeRateCurrencies.EUR,EURExchangeRates));
    }

    @Test
    public void shouldUpdateExchangeRates() throws IOException {
        ArgumentCaptor<List<CurrencyWithExchangeRates>> actualCurrenciesWithExchangeRates
                = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> writtenToFileName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> validatedFileName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> fileNameWithoutExtension = ArgumentCaptor.forClass(String.class);

        doReturn(expectedExchangeRateResponses).when(exchangeRateService).fetchExchangeRates();
        doCallRealMethod().when(xmlFileUtil).getFileNameWithExtension(fileNameWithoutExtension.capture());
        doNothing().when(xmlFileUtil).writeFile(actualCurrenciesWithExchangeRates.capture(),writtenToFileName.capture());
        when(xmlFileUtil.validateFile(validatedFileName.capture())).thenReturn(true);

        UpdateExchangeRatesResponse actualUpdateExchangeRateResponse = exchangeRateService.updateExchangeRates();
        actualCurrenciesWithExchangeRates.getValue().forEach(k -> {
            assertTrue(expectedCurrenciesWithExchangeRates.contains(k));
            assertFalse(k.getExchangeRates().containsKey(k));
        });

        String expectedFileName = writtenToFileName.getValue();
        assertEquals(expectedFileName, validatedFileName.getValue());
        assertEquals(expectedFileName, fileNameWithoutExtension.getValue() + ".xml");

        UpdateExchangeRatesResponse expectedUpdateExchangeRatesResponse =
                new UpdateExchangeRatesResponse(UpdateExchangeRatesResponse.UpdateStatus.SUCCESS, expectedFileName);

        assertEquals(expectedUpdateExchangeRatesResponse, actualUpdateExchangeRateResponse);
    }

    @Test
    public void shouldFetchExchangeRateResponses() {
        String expectedResponse = "Dummy Response";

        Map<Currency, String> expectedExchangeRateResponses = new HashMap<>();
        Arrays.stream(ExchangeRateCurrencies.values()).forEach(exchangeRateCurrency ->
            expectedExchangeRateResponses.put(Currency.getInstance(exchangeRateCurrency.name()), expectedResponse));

        doReturn(expectedResponse).when(httpUtil).makeHTTPRequest(any());

        Map<ExchangeRateCurrencies, String> actualExchangeRateResponses = exchangeRateService.fetchExchangeRates();

        Arrays.stream(ExchangeRateCurrencies.values()).forEach(exchangeRateCurrency -> {
            assertTrue(actualExchangeRateResponses.containsKey(exchangeRateCurrency));
            assertEquals(expectedResponse, actualExchangeRateResponses.get(exchangeRateCurrency));
        });
    }

    @Test
    public void shouldThrowException_whenExchangeRateResponsesAreInvalid() throws IOException {
        Map<Currency, String> invalidExchangeRateResponses = new HashMap<>();
        invalidExchangeRateResponses.put(Currency.getInstance("USD"),"INVALID JSON");

        doReturn(invalidExchangeRateResponses).when(exchangeRateService).fetchExchangeRates();

        Exception exception = Assert.assertThrows(RuntimeException.class, () -> exchangeRateService.updateExchangeRates());
        assertEquals(ErrorMessages.ERROR_PARSING_EXCHANGE_RATE_SERVICE_RESPONSE.getMessage(), exception.getMessage());
    }
}
