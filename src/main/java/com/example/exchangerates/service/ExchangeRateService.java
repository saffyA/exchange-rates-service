package com.example.exchangerates.service;

import com.example.exchangerates.ExchangeRateCurrencies;
import com.example.exchangerates.dto.UpdateExchangeRatesResponse;
import com.example.exchangerates.exception.ErrorMessages;
import com.example.exchangerates.model.CurrencyWithExchangeRates;
import com.example.exchangerates.util.HTTPUtil;
import com.example.exchangerates.util.XMLFileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public UpdateExchangeRatesResponse updateExchangeRates() throws IOException{
        Map<Currency, String> exchangeRatesResponses = fetchExchangeRates();
        List<CurrencyWithExchangeRates> currenciesWithExchangeRates = parseExchangeRatesResponses(exchangeRatesResponses);
        String fileName = getFileName();
        String fileNameWithXMLExtension = xmlFileUtil.getFileNameWithExtension(fileName);
        xmlFileUtil.writeFile(currenciesWithExchangeRates, fileNameWithXMLExtension);
        if(xmlFileUtil.validateFile(fileNameWithXMLExtension))
            return new UpdateExchangeRatesResponse(UpdateExchangeRatesResponse.UpdateStatus.SUCCESS,fileNameWithXMLExtension);
        else
            throw new IOException(ErrorMessages.XML_FILE_CREATED_IS_INVALID.getMessage());
    }

    public Map<Currency, String> fetchExchangeRates() {
        Map<Currency,String> exchangeRatesResponses = new HashMap<>();
//        exchangeRatesResponses.put(Currency.getInstance("USD"),"{ \"result\":\"success\", \"documentation\":\"https://www.exchangerate-api.com/docs\", \"terms_of_use\":\"https://www.exchangerate-api.com/terms\", \"time_last_update_unix\":1662854401, \"time_last_update_utc\":\"Sun, 11 Sep 2022 00:00:01 +0000\", \"time_next_update_unix\":1662940801, \"time_next_update_utc\":\"Mon, 12 Sep 2022 00:00:01 +0000\", \"base_code\":\"USD\", \"conversion_rates\":{  \"USD\":1,  \"AED\":3.6725,  \"AFN\":88.1461,  \"ALL\":118.2851,  \"AMD\":404.8157,  \"ANG\":1.7900,  \"AOA\":427.4513,  \"ARS\":140.6889,  \"AUD\":1.4630,  \"AWG\":1.7900,  \"AZN\":1.6973,  \"BAM\":1.9481,  \"BBD\":2.0000,  \"BDT\":94.2454,  \"BGN\":1.9481,  \"BHD\":0.3760,  \"BIF\":2036.8098,  \"BMD\":1.0000,  \"BND\":1.3968,  \"BOB\":6.9228,  \"BRL\":5.1924,  \"BSD\":1.0000,  \"BTN\":79.6565,  \"BWP\":13.0159,  \"BYN\":2.6652,  \"BZD\":2.0000,  \"CAD\":1.3027,  \"CDF\":2013.1106,  \"CHF\":0.9613,  \"CLP\":882.3044,  \"CNY\":6.9450,  \"COP\":4453.7286,  \"CRC\":655.4772,  \"CUP\":24.0000,  \"CVE\":109.8317,  \"CZK\":24.3954,  \"DJF\":177.7210,  \"DKK\":7.4311,  \"DOP\":52.9174,  \"DZD\":140.4999,  \"EGP\":19.2776,  \"ERN\":15.0000,  \"ETB\":52.6190,  \"EUR\":0.9961,  \"FJD\":2.2280,  \"FKP\":0.8650,  \"FOK\":7.4311,  \"GBP\":0.8650,  \"GEL\":2.8454,  \"GGP\":0.8650,  \"GHS\":10.2661,  \"GIP\":0.8650,  \"GMD\":55.7438,  \"GNF\":8645.3608,  \"GTQ\":7.7623,  \"GYD\":209.4414,  \"HKD\":7.8509,  \"HNL\":24.5804,  \"HRK\":7.5049,  \"HTG\":120.3747,  \"HUF\":393.8943,  \"IDR\":14785.3442,  \"ILS\":3.4082,  \"IMP\":0.8650,  \"INR\":79.6577,  \"IQD\":1461.2626,  \"IRR\":41913.3432,  \"ISK\":139.4750,  \"JEP\":0.8650,  \"JMD\":151.3231,  \"JOD\":0.7090,  \"JPY\":142.8552,  \"KES\":120.4455,  \"KGS\":81.6202,  \"KHR\":4120.4059,  \"KID\":1.4629,  \"KMF\":490.0346,  \"KRW\":1377.5594,  \"KWD\":0.2996,  \"KYD\":0.8333,  \"KZT\":472.8434,  \"LAK\":17690.8820,  \"LBP\":1507.5000,  \"LKR\":355.5637,  \"LRD\":154.2214,  \"LSL\":17.2795,  \"LYD\":4.9598,  \"MAD\":10.6861,  \"MDL\":19.3766,  \"MGA\":4118.9998,  \"MKD\":61.3187,  \"MMK\":2725.8335,  \"MNT\":3219.3287,  \"MOP\":8.0864,  \"MRU\":37.7977,  \"MUR\":44.2364,  \"MVR\":15.4120,  \"MWK\":1034.2114,  \"MXN\":19.9409,  \"MYR\":4.4906,  \"MZN\":64.7002,  \"NAD\":17.2795,  \"NGN\":423.3765,  \"NIO\":35.8387,  \"NOK\":9.9278,  \"NPR\":127.4504,  \"NZD\":1.6389,  \"OMR\":0.3845,  \"PAB\":1.0000,  \"PEN\":3.8839,  \"PGK\":3.5247,  \"PHP\":56.7965,  \"PKR\":224.3909,  \"PLN\":4.6680,  \"PYG\":6907.6464,  \"QAR\":3.6400,  \"RON\":4.8610,  \"RSD\":116.7938,  \"RUB\":60.3769,  \"RWF\":1064.6820,  \"SAR\":3.7500,  \"SBD\":8.0742,  \"SCR\":13.1970,  \"SDG\":565.0689,  \"SEK\":10.6238,  \"SGD\":1.3968,  \"SHP\":0.8650,  \"SLE\":14.3351,  \"SLL\":14335.0872,  \"SOS\":569.1141,  \"SRD\":26.0545,  \"SSP\":650.4773,  \"STN\":24.4037,  \"SYP\":2505.7644,  \"SZL\":17.2795,  \"THB\":36.4574,  \"TJS\":10.2550,  \"TMT\":3.4996,  \"TND\":2.9263,  \"TOP\":2.3664,  \"TRY\":18.2726,  \"TTD\":6.7626,  \"TVD\":1.4629,  \"TWD\":30.7894,  \"TZS\":2329.6483,  \"UAH\":36.9880,  \"UGX\":3815.1664,  \"UYU\":40.6315,  \"UZS\":10968.9266,  \"VES\":7.9835,  \"VND\":23559.2191,  \"VUV\":117.3753,  \"WST\":2.6414,  \"XAF\":653.3795,  \"XCD\":2.7000,  \"XDR\":0.7659,  \"XOF\":653.3795,  \"XPF\":118.8631,  \"YER\":250.4078,  \"ZAR\":17.2798,  \"ZMW\":15.5022,  \"ZWL\":580.1411 }}");
//        exchangeRatesResponses.put(Currency.getInstance("CHF"),"{ \"result\":\"success\", \"documentation\":\"https://www.exchangerate-api.com/docs\", \"terms_of_use\":\"https://www.exchangerate-api.com/terms\", \"time_last_update_unix\":1662854401, \"time_last_update_utc\":\"Sun, 11 Sep 2022 00:00:01 +0000\", \"time_next_update_unix\":1662940801, \"time_next_update_utc\":\"Mon, 12 Sep 2022 00:00:01 +0000\", \"base_code\":\"USD\", \"conversion_rates\":{  \"USD\":1,  \"AED\":3.6725,  \"AFN\":88.1461,  \"ALL\":118.2851,  \"AMD\":404.8157,  \"ANG\":1.7900,  \"AOA\":427.4513,  \"ARS\":140.6889,  \"AUD\":1.4630,  \"AWG\":1.7900,  \"AZN\":1.6973,  \"BAM\":1.9481,  \"BBD\":2.0000,  \"BDT\":94.2454,  \"BGN\":1.9481,  \"BHD\":0.3760,  \"BIF\":2036.8098,  \"BMD\":1.0000,  \"BND\":1.3968,  \"BOB\":6.9228,  \"BRL\":5.1924,  \"BSD\":1.0000,  \"BTN\":79.6565,  \"BWP\":13.0159,  \"BYN\":2.6652,  \"BZD\":2.0000,  \"CAD\":1.3027,  \"CDF\":2013.1106,  \"CHF\":0.9613,  \"CLP\":882.3044,  \"CNY\":6.9450,  \"COP\":4453.7286,  \"CRC\":655.4772,  \"CUP\":24.0000,  \"CVE\":109.8317,  \"CZK\":24.3954,  \"DJF\":177.7210,  \"DKK\":7.4311,  \"DOP\":52.9174,  \"DZD\":140.4999,  \"EGP\":19.2776,  \"ERN\":15.0000,  \"ETB\":52.6190,  \"EUR\":0.9961,  \"FJD\":2.2280,  \"FKP\":0.8650,  \"FOK\":7.4311,  \"GBP\":0.8650,  \"GEL\":2.8454,  \"GGP\":0.8650,  \"GHS\":10.2661,  \"GIP\":0.8650,  \"GMD\":55.7438,  \"GNF\":8645.3608,  \"GTQ\":7.7623,  \"GYD\":209.4414,  \"HKD\":7.8509,  \"HNL\":24.5804,  \"HRK\":7.5049,  \"HTG\":120.3747,  \"HUF\":393.8943,  \"IDR\":14785.3442,  \"ILS\":3.4082,  \"IMP\":0.8650,  \"INR\":79.6577,  \"IQD\":1461.2626,  \"IRR\":41913.3432,  \"ISK\":139.4750,  \"JEP\":0.8650,  \"JMD\":151.3231,  \"JOD\":0.7090,  \"JPY\":142.8552,  \"KES\":120.4455,  \"KGS\":81.6202,  \"KHR\":4120.4059,  \"KID\":1.4629,  \"KMF\":490.0346,  \"KRW\":1377.5594,  \"KWD\":0.2996,  \"KYD\":0.8333,  \"KZT\":472.8434,  \"LAK\":17690.8820,  \"LBP\":1507.5000,  \"LKR\":355.5637,  \"LRD\":154.2214,  \"LSL\":17.2795,  \"LYD\":4.9598,  \"MAD\":10.6861,  \"MDL\":19.3766,  \"MGA\":4118.9998,  \"MKD\":61.3187,  \"MMK\":2725.8335,  \"MNT\":3219.3287,  \"MOP\":8.0864,  \"MRU\":37.7977,  \"MUR\":44.2364,  \"MVR\":15.4120,  \"MWK\":1034.2114,  \"MXN\":19.9409,  \"MYR\":4.4906,  \"MZN\":64.7002,  \"NAD\":17.2795,  \"NGN\":423.3765,  \"NIO\":35.8387,  \"NOK\":9.9278,  \"NPR\":127.4504,  \"NZD\":1.6389,  \"OMR\":0.3845,  \"PAB\":1.0000,  \"PEN\":3.8839,  \"PGK\":3.5247,  \"PHP\":56.7965,  \"PKR\":224.3909,  \"PLN\":4.6680,  \"PYG\":6907.6464,  \"QAR\":3.6400,  \"RON\":4.8610,  \"RSD\":116.7938,  \"RUB\":60.3769,  \"RWF\":1064.6820,  \"SAR\":3.7500,  \"SBD\":8.0742,  \"SCR\":13.1970,  \"SDG\":565.0689,  \"SEK\":10.6238,  \"SGD\":1.3968,  \"SHP\":0.8650,  \"SLE\":14.3351,  \"SLL\":14335.0872,  \"SOS\":569.1141,  \"SRD\":26.0545,  \"SSP\":650.4773,  \"STN\":24.4037,  \"SYP\":2505.7644,  \"SZL\":17.2795,  \"THB\":36.4574,  \"TJS\":10.2550,  \"TMT\":3.4996,  \"TND\":2.9263,  \"TOP\":2.3664,  \"TRY\":18.2726,  \"TTD\":6.7626,  \"TVD\":1.4629,  \"TWD\":30.7894,  \"TZS\":2329.6483,  \"UAH\":36.9880,  \"UGX\":3815.1664,  \"UYU\":40.6315,  \"UZS\":10968.9266,  \"VES\":7.9835,  \"VND\":23559.2191,  \"VUV\":117.3753,  \"WST\":2.6414,  \"XAF\":653.3795,  \"XCD\":2.7000,  \"XDR\":0.7659,  \"XOF\":653.3795,  \"XPF\":118.8631,  \"YER\":250.4078,  \"ZAR\":17.2798,  \"ZMW\":15.5022,  \"ZWL\":580.1411 }}");
//        exchangeRatesResponses.put(Currency.getInstance("EUR"),"{ \"result\":\"success\", \"documentation\":\"https://www.exchangerate-api.com/docs\", \"terms_of_use\":\"https://www.exchangerate-api.com/terms\", \"time_last_update_unix\":1662854401, \"time_last_update_utc\":\"Sun, 11 Sep 2022 00:00:01 +0000\", \"time_next_update_unix\":1662940801, \"time_next_update_utc\":\"Mon, 12 Sep 2022 00:00:01 +0000\", \"base_code\":\"USD\", \"conversion_rates\":{  \"USD\":1,  \"AED\":3.6725,  \"AFN\":88.1461,  \"ALL\":118.2851,  \"AMD\":404.8157,  \"ANG\":1.7900,  \"AOA\":427.4513,  \"ARS\":140.6889,  \"AUD\":1.4630,  \"AWG\":1.7900,  \"AZN\":1.6973,  \"BAM\":1.9481,  \"BBD\":2.0000,  \"BDT\":94.2454,  \"BGN\":1.9481,  \"BHD\":0.3760,  \"BIF\":2036.8098,  \"BMD\":1.0000,  \"BND\":1.3968,  \"BOB\":6.9228,  \"BRL\":5.1924,  \"BSD\":1.0000,  \"BTN\":79.6565,  \"BWP\":13.0159,  \"BYN\":2.6652,  \"BZD\":2.0000,  \"CAD\":1.3027,  \"CDF\":2013.1106,  \"CHF\":0.9613,  \"CLP\":882.3044,  \"CNY\":6.9450,  \"COP\":4453.7286,  \"CRC\":655.4772,  \"CUP\":24.0000,  \"CVE\":109.8317,  \"CZK\":24.3954,  \"DJF\":177.7210,  \"DKK\":7.4311,  \"DOP\":52.9174,  \"DZD\":140.4999,  \"EGP\":19.2776,  \"ERN\":15.0000,  \"ETB\":52.6190,  \"EUR\":0.9961,  \"FJD\":2.2280,  \"FKP\":0.8650,  \"FOK\":7.4311,  \"GBP\":0.8650,  \"GEL\":2.8454,  \"GGP\":0.8650,  \"GHS\":10.2661,  \"GIP\":0.8650,  \"GMD\":55.7438,  \"GNF\":8645.3608,  \"GTQ\":7.7623,  \"GYD\":209.4414,  \"HKD\":7.8509,  \"HNL\":24.5804,  \"HRK\":7.5049,  \"HTG\":120.3747,  \"HUF\":393.8943,  \"IDR\":14785.3442,  \"ILS\":3.4082,  \"IMP\":0.8650,  \"INR\":79.6577,  \"IQD\":1461.2626,  \"IRR\":41913.3432,  \"ISK\":139.4750,  \"JEP\":0.8650,  \"JMD\":151.3231,  \"JOD\":0.7090,  \"JPY\":142.8552,  \"KES\":120.4455,  \"KGS\":81.6202,  \"KHR\":4120.4059,  \"KID\":1.4629,  \"KMF\":490.0346,  \"KRW\":1377.5594,  \"KWD\":0.2996,  \"KYD\":0.8333,  \"KZT\":472.8434,  \"LAK\":17690.8820,  \"LBP\":1507.5000,  \"LKR\":355.5637,  \"LRD\":154.2214,  \"LSL\":17.2795,  \"LYD\":4.9598,  \"MAD\":10.6861,  \"MDL\":19.3766,  \"MGA\":4118.9998,  \"MKD\":61.3187,  \"MMK\":2725.8335,  \"MNT\":3219.3287,  \"MOP\":8.0864,  \"MRU\":37.7977,  \"MUR\":44.2364,  \"MVR\":15.4120,  \"MWK\":1034.2114,  \"MXN\":19.9409,  \"MYR\":4.4906,  \"MZN\":64.7002,  \"NAD\":17.2795,  \"NGN\":423.3765,  \"NIO\":35.8387,  \"NOK\":9.9278,  \"NPR\":127.4504,  \"NZD\":1.6389,  \"OMR\":0.3845,  \"PAB\":1.0000,  \"PEN\":3.8839,  \"PGK\":3.5247,  \"PHP\":56.7965,  \"PKR\":224.3909,  \"PLN\":4.6680,  \"PYG\":6907.6464,  \"QAR\":3.6400,  \"RON\":4.8610,  \"RSD\":116.7938,  \"RUB\":60.3769,  \"RWF\":1064.6820,  \"SAR\":3.7500,  \"SBD\":8.0742,  \"SCR\":13.1970,  \"SDG\":565.0689,  \"SEK\":10.6238,  \"SGD\":1.3968,  \"SHP\":0.8650,  \"SLE\":14.3351,  \"SLL\":14335.0872,  \"SOS\":569.1141,  \"SRD\":26.0545,  \"SSP\":650.4773,  \"STN\":24.4037,  \"SYP\":2505.7644,  \"SZL\":17.2795,  \"THB\":36.4574,  \"TJS\":10.2550,  \"TMT\":3.4996,  \"TND\":2.9263,  \"TOP\":2.3664,  \"TRY\":18.2726,  \"TTD\":6.7626,  \"TVD\":1.4629,  \"TWD\":30.7894,  \"TZS\":2329.6483,  \"UAH\":36.9880,  \"UGX\":3815.1664,  \"UYU\":40.6315,  \"UZS\":10968.9266,  \"VES\":7.9835,  \"VND\":23559.2191,  \"VUV\":117.3753,  \"WST\":2.6414,  \"XAF\":653.3795,  \"XCD\":2.7000,  \"XDR\":0.7659,  \"XOF\":653.3795,  \"XPF\":118.8631,  \"YER\":250.4078,  \"ZAR\":17.2798,  \"ZMW\":15.5022,  \"ZWL\":580.1411 }}");
//        exchangeRatesResponses.put(Currency.getInstance("GBP"),"{ \"result\":\"success\", \"documentation\":\"https://www.exchangerate-api.com/docs\", \"terms_of_use\":\"https://www.exchangerate-api.com/terms\", \"time_last_update_unix\":1662854401, \"time_last_update_utc\":\"Sun, 11 Sep 2022 00:00:01 +0000\", \"time_next_update_unix\":1662940801, \"time_next_update_utc\":\"Mon, 12 Sep 2022 00:00:01 +0000\", \"base_code\":\"USD\", \"conversion_rates\":{  \"USD\":1,  \"AED\":3.6725,  \"AFN\":88.1461,  \"ALL\":118.2851,  \"AMD\":404.8157,  \"ANG\":1.7900,  \"AOA\":427.4513,  \"ARS\":140.6889,  \"AUD\":1.4630,  \"AWG\":1.7900,  \"AZN\":1.6973,  \"BAM\":1.9481,  \"BBD\":2.0000,  \"BDT\":94.2454,  \"BGN\":1.9481,  \"BHD\":0.3760,  \"BIF\":2036.8098,  \"BMD\":1.0000,  \"BND\":1.3968,  \"BOB\":6.9228,  \"BRL\":5.1924,  \"BSD\":1.0000,  \"BTN\":79.6565,  \"BWP\":13.0159,  \"BYN\":2.6652,  \"BZD\":2.0000,  \"CAD\":1.3027,  \"CDF\":2013.1106,  \"CHF\":0.9613,  \"CLP\":882.3044,  \"CNY\":6.9450,  \"COP\":4453.7286,  \"CRC\":655.4772,  \"CUP\":24.0000,  \"CVE\":109.8317,  \"CZK\":24.3954,  \"DJF\":177.7210,  \"DKK\":7.4311,  \"DOP\":52.9174,  \"DZD\":140.4999,  \"EGP\":19.2776,  \"ERN\":15.0000,  \"ETB\":52.6190,  \"EUR\":0.9961,  \"FJD\":2.2280,  \"FKP\":0.8650,  \"FOK\":7.4311,  \"GBP\":0.8650,  \"GEL\":2.8454,  \"GGP\":0.8650,  \"GHS\":10.2661,  \"GIP\":0.8650,  \"GMD\":55.7438,  \"GNF\":8645.3608,  \"GTQ\":7.7623,  \"GYD\":209.4414,  \"HKD\":7.8509,  \"HNL\":24.5804,  \"HRK\":7.5049,  \"HTG\":120.3747,  \"HUF\":393.8943,  \"IDR\":14785.3442,  \"ILS\":3.4082,  \"IMP\":0.8650,  \"INR\":79.6577,  \"IQD\":1461.2626,  \"IRR\":41913.3432,  \"ISK\":139.4750,  \"JEP\":0.8650,  \"JMD\":151.3231,  \"JOD\":0.7090,  \"JPY\":142.8552,  \"KES\":120.4455,  \"KGS\":81.6202,  \"KHR\":4120.4059,  \"KID\":1.4629,  \"KMF\":490.0346,  \"KRW\":1377.5594,  \"KWD\":0.2996,  \"KYD\":0.8333,  \"KZT\":472.8434,  \"LAK\":17690.8820,  \"LBP\":1507.5000,  \"LKR\":355.5637,  \"LRD\":154.2214,  \"LSL\":17.2795,  \"LYD\":4.9598,  \"MAD\":10.6861,  \"MDL\":19.3766,  \"MGA\":4118.9998,  \"MKD\":61.3187,  \"MMK\":2725.8335,  \"MNT\":3219.3287,  \"MOP\":8.0864,  \"MRU\":37.7977,  \"MUR\":44.2364,  \"MVR\":15.4120,  \"MWK\":1034.2114,  \"MXN\":19.9409,  \"MYR\":4.4906,  \"MZN\":64.7002,  \"NAD\":17.2795,  \"NGN\":423.3765,  \"NIO\":35.8387,  \"NOK\":9.9278,  \"NPR\":127.4504,  \"NZD\":1.6389,  \"OMR\":0.3845,  \"PAB\":1.0000,  \"PEN\":3.8839,  \"PGK\":3.5247,  \"PHP\":56.7965,  \"PKR\":224.3909,  \"PLN\":4.6680,  \"PYG\":6907.6464,  \"QAR\":3.6400,  \"RON\":4.8610,  \"RSD\":116.7938,  \"RUB\":60.3769,  \"RWF\":1064.6820,  \"SAR\":3.7500,  \"SBD\":8.0742,  \"SCR\":13.1970,  \"SDG\":565.0689,  \"SEK\":10.6238,  \"SGD\":1.3968,  \"SHP\":0.8650,  \"SLE\":14.3351,  \"SLL\":14335.0872,  \"SOS\":569.1141,  \"SRD\":26.0545,  \"SSP\":650.4773,  \"STN\":24.4037,  \"SYP\":2505.7644,  \"SZL\":17.2795,  \"THB\":36.4574,  \"TJS\":10.2550,  \"TMT\":3.4996,  \"TND\":2.9263,  \"TOP\":2.3664,  \"TRY\":18.2726,  \"TTD\":6.7626,  \"TVD\":1.4629,  \"TWD\":30.7894,  \"TZS\":2329.6483,  \"UAH\":36.9880,  \"UGX\":3815.1664,  \"UYU\":40.6315,  \"UZS\":10968.9266,  \"VES\":7.9835,  \"VND\":23559.2191,  \"VUV\":117.3753,  \"WST\":2.6414,  \"XAF\":653.3795,  \"XCD\":2.7000,  \"XDR\":0.7659,  \"XOF\":653.3795,  \"XPF\":118.8631,  \"YER\":250.4078,  \"ZAR\":17.2798,  \"ZMW\":15.5022,  \"ZWL\":580.1411 }}");
        for(ExchangeRateCurrencies exchangeRateCurrency : ExchangeRateCurrencies.values()) {
            exchangeRatesResponses.put(Currency.getInstance(exchangeRateCurrency.name()),
                    httpUtil.makeHTTPRequest(exchangeRatesServiceBaseURL + exchangeRatesServiceAPIKey + "/latest/" + exchangeRateCurrency.name()));
        }
        return exchangeRatesResponses;
    }

    private List<CurrencyWithExchangeRates> parseExchangeRatesResponses(Map<Currency, String> exchangeRatesResponses) {
        List<CurrencyWithExchangeRates> currenciesWithExchangeRates = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Map.Entry<Currency, String> entry : exchangeRatesResponses.entrySet()) {
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
        };
        return currenciesWithExchangeRates;
    }

    private String getFileName() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        String folderName = DateTimeFormatter.ISO_LOCAL_DATE.format(now);
        String fileName = DateTimeFormatter.ISO_LOCAL_TIME.format(now);
        return exchangeRatesFilesLocation + "/" + folderName + "/" + fileName;
    }
}