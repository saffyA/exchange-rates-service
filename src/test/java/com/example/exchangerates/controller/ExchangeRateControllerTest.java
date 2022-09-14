package com.example.exchangerates.controller;

import com.example.exchangerates.dto.UpdateExchangeRatesResponse;
import com.example.exchangerates.exception.ErrorMessages;
import com.example.exchangerates.exception.GlobalExceptionHandler;
import com.example.exchangerates.service.ExchangeRateService;
import com.example.exchangerates.util.HTTPUtil;
import com.example.exchangerates.util.XMLFileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExchangeRateControllerTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private static XMLFileUtil xmlFileUtil = mock(XMLFileUtil.class);
    private static HTTPUtil httpUtil = mock(HTTPUtil.class);

    @Spy
    private static ExchangeRateService exchangeRateService = new ExchangeRateService(xmlFileUtil, httpUtil);

    @InjectMocks
    ExchangeRateController exchangeRateController = new ExchangeRateController(exchangeRateService);

    private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(exchangeRateController).setControllerAdvice(GlobalExceptionHandler.class).build();

    private static Map<Currency,String> expectedExchangeRatesResponses = new HashMap<>();
    private static String DUMMY_DATA_FILE_USD = "dummyExchangeRatesResponseUSD.json";
    private static String DUMMY_DATA_FILE_EUR = "dummyExchangeRatesResponseEUR.json";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    ExchangeRateControllerTest() throws Exception{
    }

    @BeforeAll
    public static void setExpectedData() throws URISyntaxException, IOException {
        //should be in sync with dummy data
        ReflectionTestUtils.setField(exchangeRateService,"exchangeRatesServiceResponseFields_exchangeRates","conversion_rates");
        ReflectionTestUtils.setField(exchangeRateService,"exchangeRatesFilesLocation","exchange-rates");
        expectedExchangeRatesResponses.put(Currency.getInstance("USD"),
                Files.readString(Path.of(ClassLoader.getSystemResource(DUMMY_DATA_FILE_USD).toURI())));
        expectedExchangeRatesResponses.put(Currency.getInstance("EUR"),
                Files.readString(Path.of(ClassLoader.getSystemResource(DUMMY_DATA_FILE_EUR).toURI())));
    }


    @Test
    void shouldUpdateExchangeRates_andReturn200() throws Exception {
        ArgumentCaptor<String> validatedFileName = ArgumentCaptor.forClass(String.class);

        doReturn(expectedExchangeRatesResponses).when(exchangeRateService).fetchExchangeRates();
        doCallRealMethod().when(xmlFileUtil).getFileNameWithExtension(any());
        doNothing().when(xmlFileUtil).writeFile(any(),any());
        when(xmlFileUtil.validateFile(validatedFileName.capture())).thenReturn(true);

        MockHttpServletResponse actualResponse = mockMvc.perform(put("/exchange-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        String actualResponseJSON = actualResponse.getContentAsString();

        String expectedFileName = validatedFileName.getValue();

        UpdateExchangeRatesResponse mockUpdateExchangeRatesResponse =
                new UpdateExchangeRatesResponse(UpdateExchangeRatesResponse.UpdateStatus.SUCCESS, expectedFileName);
        String expectedResponseJSON = objectMapper.writeValueAsString(mockUpdateExchangeRatesResponse);
        assertEquals(expectedResponseJSON, actualResponseJSON);
    }

    @Test
    void shouldReturn500_whenGeneratedFileNotValid() throws Exception {

        doReturn(expectedExchangeRatesResponses).when(exchangeRateService).fetchExchangeRates();
        doCallRealMethod().when(xmlFileUtil).getFileNameWithExtension(any());
        doNothing().when(xmlFileUtil).writeFile(any(),any());
        doThrow(new RuntimeException(ErrorMessages.ERROR_VALIDATING_XML_FILE.getMessage()))
                .when(xmlFileUtil).validateFile(any());

        MockHttpServletResponse actualResponse = mockMvc.perform(put("/exchange-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andReturn().getResponse();
        String actualResponseJSON = actualResponse.getContentAsString();

        assertEquals(ErrorMessages.ERROR_VALIDATING_XML_FILE.getMessage(), actualResponseJSON);
    }
}
