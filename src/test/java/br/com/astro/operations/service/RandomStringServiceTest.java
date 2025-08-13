package br.com.astro.operations.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class RandomStringServiceTest {

	@Test
	@DisplayName("fallback generator returns string with requested length")
	void fallback_length_ok() {
		WebClient.Builder builder = WebClient.builder();
		RandomStringService service = new RandomStringService(builder);
		int len = 12;
		String s = service.generateRandomStringFallback(len, null);
		assertNotNull(s);
		assertEquals(len, s.length());
	}
}
