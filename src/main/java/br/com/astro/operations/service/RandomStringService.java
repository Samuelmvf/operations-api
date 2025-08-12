package br.com.astro.operations.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class RandomStringService {

    private final WebClient webClient;
    private final Random fallbackRandom;

    @Value("${app.random-org.url:https://www.random.org/strings/}")
    private String randomOrgUrl;

    public RandomStringService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://www.random.org")
            .build();
        this.fallbackRandom = new Random();
    }

    @CircuitBreaker(name = "random-org", fallbackMethod = "generateRandomStringFallback")
    @Retry(name = "random-org")
    public String generateRandomString(int length) {
        try {
            String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/strings/")
                    .queryParam("num", 1)
                    .queryParam("len", length)
                    .queryParam("digits", "on")
                    .queryParam("upperalpha", "on")
                    .queryParam("loweralpha", "on")
                    .queryParam("unique", "on")
                    .queryParam("format", "plain")
                    .queryParam("rnd", "new")
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();

            return response != null ? response.trim() : generateRandomStringFallback(length, null);
        } catch (Exception e) {
            return generateRandomStringFallback(length, e);
        }
    }

    public String generateRandomStringFallback(int length, Exception ex) {
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(fallbackRandom.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
}
