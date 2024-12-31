package com.s1.kriptoboot.boot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s1.kriptoboot.boot.model.SpotBalance;
import com.s1.kriptoboot.boot.model.TotalBalance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OkxApiService {

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

    public String getTimestamp() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withLocale(Locale.US)
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

    public Object getBalance() {
        String timestamp = getTimestamp();
        String method = "GET";
        String requestPath = "/api/v5/account/balance";
        String body = "";

        try {
            String sign = generateSignature(method, requestPath, body, timestamp);

            HttpHeaders headers = new HttpHeaders();
            headers.add("OK-ACCESS-KEY", apiKey);
            headers.add("OK-ACCESS-SIGN", sign);
            headers.set("OK-ACCESS-TIMESTAMP", timestamp);
            headers.add("OK-ACCESS-PASSPHRASE", passphrase);
            headers.add("Content-Type", "application/json");

            String url = apiUrl + requestPath;

            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                if (responseBody != null) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    JsonNode details = jsonNode.get("data").get(0).get("details");

                    // Toplam bakiyeyi alıyoruz
                    TotalBalance totalBalance = new TotalBalance();
                    totalBalance.setTotalEq(jsonNode.get("data").get(0).get("totalEq").asText());

                    // Her birim için spot bakiyeleri alıyoruz
                    List<SpotBalance> balances = new ArrayList<>();
                    for (JsonNode detail : details) {
                        SpotBalance balance = new SpotBalance();
                        balance.setCurrency(detail.get("ccy").asText());
                        balance.setSpotBalance(detail.get("cashBal").asText());
                        balances.add(balance);
                    }

                    // Hem toplam bakiyeyi hem de spot bakiyeleri döndürmeliyiz
                    return new Object[] { totalBalance, balances };
                } else {
                    System.err.println("Response body is empty.");
                    return null;
                }
            } else {
                System.err.println("Error: Received HTTP status code " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error fetching balance: " + e.getMessage());
            return null;
        }
    }
}
