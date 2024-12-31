package com.s1.kriptoboot.boot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1.kriptoboot.boot.model.CryptoInfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class CryptoService {

    @Value("${okx.api.base.url}")
    private String apiUrl;

    @Value("${okx.api.key}")
    private String apiKey;

    @Value("${okx.api.secret}")
    private String apiSecret;

    @Value("${okx.api.passphrase}")
    private String passphrase;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<Double> priceHistory = new ArrayList<>();

    public String getTimestamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }

    public String generateSignature(String method, String requestPath, String body, String timestamp) throws Exception {
        String preSign = timestamp + method + requestPath + body;
        SecretKeySpec secretKey = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);

        byte[] signedBytes = mac.doFinal(preSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signedBytes);
    }

    public void initializePriceHistory(String cryptoSymbol) {
        String timestamp = getTimestamp();
        String method = "GET";
        String requestPath = "/api/v5/market/candles?instId=" + cryptoSymbol + "&bar=1m&limit=900";
        String body = "";

        try {
            String sign = generateSignature(method, requestPath, body, timestamp);

            HttpHeaders headers = new HttpHeaders();
            headers.add("OK-ACCESS-KEY", apiKey);
            headers.add("OK-ACCESS-SIGN", sign);
            headers.set("OK-ACCESS-TIMESTAMP", timestamp);
            headers.add("OK-ACCESS-PASSPHRASE", passphrase);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = apiUrl + requestPath;

            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.has("data") && jsonNode.get("data").isArray()) {
                    JsonNode data = jsonNode.get("data");
                    for (JsonNode candle : data) {
                        double price = Double.parseDouble(candle.get(4).asText()); // Kapanış fiyatı
                        updatePriceHistory(price);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CryptoInfo getCryptoInfo(String cryptoSymbol) {
        if (priceHistory.isEmpty()) {
            initializePriceHistory(cryptoSymbol);
        }

        String timestamp = getTimestamp();
        String method = "GET";
        String requestPath = "/api/v5/market/ticker?instId=" + cryptoSymbol;
        String body = "";

        try {
            String sign = generateSignature(method, requestPath, body, timestamp);

            HttpHeaders headers = new HttpHeaders();
            headers.add("OK-ACCESS-KEY", apiKey);
            headers.add("OK-ACCESS-SIGN", sign);
            headers.set("OK-ACCESS-TIMESTAMP", timestamp);
            headers.add("OK-ACCESS-PASSPHRASE", passphrase);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url = apiUrl + requestPath;

            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.has("data") && jsonNode.get("data").isArray()) {
                    JsonNode data = jsonNode.get("data").get(0);

                    CryptoInfo cryptoInfo = new CryptoInfo();
                    cryptoInfo.setSymbol(data.get("instId").asText());
                    cryptoInfo.setPrice(data.get("last").asText());
                    cryptoInfo.setHigh24h(data.get("high24h").asText());
                    cryptoInfo.setLow24h(data.get("low24h").asText());

                    double price = Double.parseDouble(cryptoInfo.getPrice());
                    updatePriceHistory(price);

                    // Set priceHistory to the cryptoInfo object
                    cryptoInfo.setPriceHistory(priceHistory); 

                    if (priceHistory.size() >= 14) {
                        cryptoInfo.setEma(calculateEMA(priceHistory, 9));
                        cryptoInfo.setRsi(calculateRSI(priceHistory, 14));
                        cryptoInfo.setSignal(determineSignal(cryptoInfo.getRsi()));
                    } else {
                        cryptoInfo.setEma(0);
                        cryptoInfo.setRsi(0);
                        cryptoInfo.setSignal("Not enough data");
                    }

                    return cryptoInfo;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void updatePriceHistory(double price) {
        priceHistory.add(price);
        if (priceHistory.size() > 100) {
            priceHistory.remove(0);
        }
    }

    private double calculateEMA(List<Double> prices, int period) {
        if (prices.size() < period) return 0;
        double multiplier = 2.0 / (period + 1);
        double ema = prices.get(0);
        for (int i = 1; i < prices.size(); i++) {
            ema = (prices.get(i) - ema) * multiplier + ema;
        }
        return ema;
    }

    private double calculateRSI(List<Double> prices, int period) {
        if (prices.size() < period + 1) return 0;
        double gain = 0, loss = 0;
        for (int i = 1; i <= period; i++) {
            double change = prices.get(i) - prices.get(i - 1);
            if (change > 0) gain += change;
            else loss += Math.abs(change);
        }
        gain /= period;
        loss /= period;
        if (loss == 0) return 100;
        double rs = gain / loss;
        return 100 - (100 / (1 + rs));
    }

    private String determineSignal(double rsi) {
        if (rsi < 30) return "SATIN AL";
        if (rsi > 70) return "SATMA ZAMANI";
        return "NÖTÜR";
    }
}
