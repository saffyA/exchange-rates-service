package com.example.exchangerates.exception;

public enum ErrorMessages {
    ERROR_CODE_RECEIVED_FROM_EXCHANGE_RATES_SERVICE("Status code received from Exchange Rates Service: "),
    INVALID_HTTP_PROTOCOL("Invalid HTTP protocol set while connecting to Exchange Rate Service"),
    MALFORMED_URL("Malformed URL while connecting to Exchange Rates Service"),
    IO_ERROR_EXCHANGE_RATES_SERVICE("Error occurred while reading response from Exchange Rates Service"),
    ERROR_WRITING_XML_TO_FILE("Error occurred while writing XML string to file"),
    ERROR_VALIDATING_XML_FILE("Error occurred while validating XML file"),
    XML_FILE_NOT_CREATED("Exchange rates XML file could not be created"),
    ERROR_PARSING_EXCHANGE_RATE_SERVICE_RESPONSE("Error occurred while parsing Exchange Rate response");
    private String message;
    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
