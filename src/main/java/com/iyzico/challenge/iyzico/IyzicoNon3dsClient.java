package com.iyzico.challenge.iyzico;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iyzico.challenge.iyzico.dto.IyzicoCreatePaymentRequest;
import com.iyzico.challenge.iyzico.dto.IyzicoCreatePaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class IyzicoNon3dsClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final IyzicoAuthHelper authHelper;

    @Value("${iyzico.base-url}")
    private String baseUrl;

    @Value("${iyzico.api-key}")
    private String apiKey;

    @Value("${iyzico.secret-key}")
    private String secretKey;

    @Value("${iyzico.uri.payment-auth}")
    private String paymentAuthPath;

    @Value("${iyzico.locale:tr}")
    private String locale;

    @Value("${iyzico.currency:TRY}")
    private String currency;

    private static final String CITY_ISTANBUL = "Istanbul";

    public IyzicoCreatePaymentResponse createPayment(BigDecimal price) {
        try {
            String randomKey = String.valueOf(System.currentTimeMillis());

            IyzicoCreatePaymentRequest req = buildDummyRequest(price);

            String bodyJson = objectMapper.writeValueAsString(req);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-iyzi-rnd", randomKey);
            headers.set("Authorization", authHelper.buildAuthorizationHeader(apiKey, secretKey, randomKey, paymentAuthPath, bodyJson));
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

            ResponseEntity<IyzicoCreatePaymentResponse> resp = restTemplate.exchange(
                    baseUrl + paymentAuthPath,
                    HttpMethod.POST,
                    entity,
                    IyzicoCreatePaymentResponse.class
            );

            return resp.getBody();
        } catch (Exception e) {
            throw new IllegalStateException("iyzico createPayment failed", e);
        }
    }

    private IyzicoCreatePaymentRequest buildDummyRequest(BigDecimal amount) {
        String a = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

        IyzicoCreatePaymentRequest.PaymentCard card = IyzicoCreatePaymentRequest.PaymentCard.builder()
                .cardHolderName("John Doe")
                .cardNumber("5526080000000006")
                .expireMonth("12")
                .expireYear("2030")
                .cvc("123")
                .registerCard(0)
                .build();

        IyzicoCreatePaymentRequest.Buyer buyer = IyzicoCreatePaymentRequest.Buyer.builder()
                .id("BY789")
                .name("John")
                .surname("Doe")
                .gsmNumber("+905350000000")
                .email("john.doe@example.com")
                .identityNumber("74300864791")
                .registrationAddress(CITY_ISTANBUL)
                .city(CITY_ISTANBUL)
                .country("Turkey")
                .ip("85.34.78.112")
                .build();

        IyzicoCreatePaymentRequest.Address billing = IyzicoCreatePaymentRequest.Address.builder()
                .contactName("John Doe")
                .city(CITY_ISTANBUL)
                .country("Turkey")
                .addressLine("Some address")
                .build();

        IyzicoCreatePaymentRequest.BasketItem item = IyzicoCreatePaymentRequest.BasketItem.builder()
                .id("BI101")
                .name("Seat Purchase")
                .category1("Flight")
                .itemType("VIRTUAL")
                .price(a)
                .build();

        return IyzicoCreatePaymentRequest.builder()
                .locale(locale)
                .conversationId("conv-" + System.currentTimeMillis())
                .price(a)
                .paidPrice(a)
                .currency(currency)
                .installment(1)
                .basketId("B" + System.currentTimeMillis())
                .paymentChannel("WEB")
                .paymentGroup("PRODUCT")
                .paymentCard(card)
                .buyer(buyer)
                .billingAddress(billing)
                .basketItems(Collections.singletonList(item))
                .build();
    }
}
