package com.example.specscout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class ApiTestController {

    @Value("${mobileapi.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();

    @GetMapping("/api-test")
    public String apiTest() {
        try {
            return restClient.get()
                .uri("https://api.mobileapi.dev/devices/search?name={name}&key={key}",
                     "iPhone 15", apiKey)
                .retrieve()
                .body(String.class);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}