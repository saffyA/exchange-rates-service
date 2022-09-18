# exchange-rates-service

JAVA 11

### Run tests

In the home folder:

```./mvnw test```

### Exchange rates service

Set the following environment variable to your Exchange Rates Service API key

```EXCHANGE_RATES_SERVICE_API_KEY```

### Run application

In the home folder:

```./mvnw spring-boot:run```

### Endpoints

1. End point to trigger manual fetching of exchange rates. This will create and write the XML to the folder exchange-rates in the project home folder

    
```PUT http://localhost:8080/exchange-rates```

2. End point to read current exchange rate in terms of X/Y. Example USD/EUR


```GET http://localhost:8080/exchange-rates/baseCurrency/toCurrency```

