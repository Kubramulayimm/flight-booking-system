package com.iyzico.challenge.iyzico;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.iyzico.dto.IyzicoCreatePaymentRequest;
import com.iyzico.challenge.iyzico.dto.IyzicoCreatePaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IyzicoNon3dsClientTest {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private IyzicoAuthHelper authHelper;
    private IyzicoNon3dsClient sut;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        objectMapper = mock(ObjectMapper.class);
        authHelper = mock(IyzicoAuthHelper.class);

        sut = new IyzicoNon3dsClient(restTemplate, objectMapper, authHelper);

        ReflectionTestUtils.setField(sut, "baseUrl", "https://api.example.com");
        ReflectionTestUtils.setField(sut, "paymentAuthPath", "/payment/auth");
        ReflectionTestUtils.setField(sut, "apiKey", "api-key");
        ReflectionTestUtils.setField(sut, "secretKey", "secret-key");
        ReflectionTestUtils.setField(sut, "locale", "tr");
        ReflectionTestUtils.setField(sut, "currency", "TRY");
    }

    @Test
    void createPayment_shouldCallRestTemplateWithProperHeadersAndReturnBody() throws Exception {
        BigDecimal price = new BigDecimal("10.5");

        when(objectMapper.writeValueAsString(any(IyzicoCreatePaymentRequest.class)))
                .thenReturn("{\"json\":true}");

        when(authHelper.buildAuthorizationHeader(eq("api-key"), eq("secret-key"), anyString(), eq("/payment/auth"), eq("{\"json\":true}")))
                .thenReturn("IYZWSv2 token");

        IyzicoCreatePaymentResponse responseBody = new IyzicoCreatePaymentResponse();
        responseBody.setStatus("success");
        ResponseEntity<IyzicoCreatePaymentResponse> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(IyzicoCreatePaymentResponse.class)))
                .thenReturn(responseEntity);

        IyzicoCreatePaymentResponse out = sut.createPayment(price);

        assertNotNull(out);
        assertEquals("success", out.getStatus());

        ArgumentCaptor<String> urlCap = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("rawtypes")
        ArgumentCaptor<HttpEntity> entityCap = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(urlCap.capture(), eq(HttpMethod.POST), entityCap.capture(), eq(IyzicoCreatePaymentResponse.class));

        assertEquals("https://api.example.com/payment/auth", urlCap.getValue());

        @SuppressWarnings("unchecked")
        HttpEntity<String> sentEntity = (HttpEntity<String>) entityCap.getValue();

        assertEquals("{\"json\":true}", sentEntity.getBody());

        HttpHeaders headers = sentEntity.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals("IYZWSv2 token", headers.getFirst("Authorization"));
        assertNotNull(headers.getFirst("x-iyzi-rnd"));
        assertTrue(headers.getAccept().contains(MediaType.APPLICATION_JSON));
    }

    @Test
    void createPayment_whenAnyExceptionOccurs_shouldThrowIllegalState() throws Exception {
        when(objectMapper.writeValueAsString(any(IyzicoCreatePaymentRequest.class)))
                .thenThrow(new RuntimeException("boom"));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> sut.createPayment(new BigDecimal("10.00")));

        assertTrue(ex.getMessage().contains("iyzico createPayment failed"));
    }
}
