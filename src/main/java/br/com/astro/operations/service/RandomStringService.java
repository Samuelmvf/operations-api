package br.com.astro.operations.service;

import java.time.Duration;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.debug("Generating random string with length={} via Random.org", length);
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

            String result = response != null ? response.trim() : null;
            if (result != null) {
                log.info("Random.org string generated successfully (len={})", result.length());
                return result;
            }
            log.warn("Random.org returned empty response, falling back");
            return generateRandomStringFallback(length, null);
        } catch (Exception e) {
            log.warn("Error calling Random.org (url={}): {}. Falling back.", randomOrgUrl, e.getMessage());
            return generateRandomStringFallback(length, e);
        }
    }

    public String generateRandomStringFallback(int length, Exception ex) {
        if (ex != null) {
            log.info("Fallback random string generation due to error: {}", ex.getMessage());
        } else {
            log.info("Fallback random string generation due to empty response");
        }
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(fallbackRandom.nextInt(chars.length())));
        }

        String result = sb.toString();
        log.debug("Fallback random string generated (len={})", result.length());
        return result;
    }
}