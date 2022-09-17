package com.example.exchangerates.util;

import com.example.exchangerates.ExchangeRateCurrencies;
import com.example.exchangerates.exception.ErrorMessages;
import com.example.exchangerates.model.CurrencyWithExchangeRates;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UtilTest {

    @Spy
    HTTPUtil httpUtil = new HTTPUtil();

    XMLFileUtil xmlFileUtil = new XMLFileUtil();

    @Test
    void makeHTTPRequest_shouldThrowException_whenURLIsMalformed() {
        Exception exception = Assert.assertThrows(RuntimeException.class,
                () -> httpUtil.makeHTTPRequest("some invalid URL"));
        assertEquals(ErrorMessages.MALFORMED_URL.getMessage(), exception.getMessage());
    }

    @Test
    void shouldGenerateValidXMLString_XSDCheck() {
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

        List<CurrencyWithExchangeRates> currenciesWithExchangeRates = new ArrayList<>();
        Map<String, Double> USDExchangeRates = new HashMap<>();
        USDExchangeRates.put("EUR", 0.9961);
        USDExchangeRates.put("GBP", 0.865);
        USDExchangeRates.put("CHF", 0.9613);
        currenciesWithExchangeRates.add(new CurrencyWithExchangeRates(ExchangeRateCurrencies.USD,USDExchangeRates));
        Map<String, Double> EURExchangeRates = new HashMap<>();
        EURExchangeRates.put("USD", 1.0039);
        EURExchangeRates.put("GBP", 0.8684);
        EURExchangeRates.put("CHF", 0.9652);
        currenciesWithExchangeRates.add(new CurrencyWithExchangeRates(ExchangeRateCurrencies.EUR,EURExchangeRates));

        String actualXMLString = xmlFileUtil.getXMLString(currenciesWithExchangeRates);
        assertTrue(xmlFileUtil.validateContent(xmlHeader + actualXMLString));
    }

    @Test
    void shouldValidateFile_whenXMLIsValid_XSDCheck() {
        assertTrue(xmlFileUtil.validateFile("src/test/resources/dummyValid.xml"));
    }

    @Test
    void shouldThrowException_whenXMLIsInvalid_XSDCheck() {
        Exception exception = Assert.assertThrows(RuntimeException.class, () ->
        xmlFileUtil.validateFile("src/test/resources/dummyInvalid.xml"));

        assertEquals(ErrorMessages.ERROR_VALIDATING_XML_FILE.getMessage(), exception.getMessage());
    }
}
