package com.example.exchangerates.util;

import com.example.exchangerates.exception.ErrorMessages;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Component
public class HTTPUtil {
    public String makeHTTPRequest(String URL) {
        try {
            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if(status!=200)
                throw new RuntimeException(ErrorMessages.ERROR_CODE_RECEIVED_FROM_EXCHANGE_RATES_SERVICE.getMessage() + status);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        }
        catch (ProtocolException e) {
            throw new RuntimeException(ErrorMessages.INVALID_HTTP_PROTOCOL.getMessage(), e);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(ErrorMessages.MALFORMED_URL.getMessage(), e);
        }
        catch (IOException e) {
            throw new RuntimeException(ErrorMessages.IO_ERROR_EXCHANGE_RATES_SERVICE.getMessage(), e);
        }
    }
}
