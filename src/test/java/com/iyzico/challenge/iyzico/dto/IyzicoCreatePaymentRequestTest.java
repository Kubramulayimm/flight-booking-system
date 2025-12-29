package com.iyzico.challenge.iyzico.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IyzicoCreatePaymentRequestTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldSerialize_withoutNullFields_dueToJsonIncludeNonNull() throws Exception {
        IyzicoCreatePaymentRequest req = IyzicoCreatePaymentRequest.builder()
                .locale("tr")
                .conversationId("conv-1")
                .price("100.0")
                .currency("TRY")
                .build();

        String json = mapper.writeValueAsString(req);

        assertThat(json).contains("\"locale\":\"tr\"");
        assertThat(json).contains("\"conversationId\":\"conv-1\"");
        assertThat(json).contains("\"price\":\"100.0\"");
        assertThat(json).contains("\"currency\":\"TRY\"");
        assertThat(json).doesNotContain("paidPrice");
        assertThat(json).doesNotContain("installment");
    }

    @Test
    void shouldBuildNestedObjectsCorrectly() {
        IyzicoCreatePaymentRequest.PaymentCard card = IyzicoCreatePaymentRequest.PaymentCard.builder()
                .cardHolderName("Test User")
                .cardNumber("5528790000000008")
                .expireMonth("12")
                .expireYear("2030")
                .cvc("123")
                .registerCard(0)
                .build();

        IyzicoCreatePaymentRequest.Buyer buyer = IyzicoCreatePaymentRequest.Buyer.builder()
                .id("buyer-1")
                .name("Kubra")
                .surname("M")
                .email("k@k.com")
                .city("Istanbul")
                .country("TR")
                .ip("127.0.0.1")
                .build();

        IyzicoCreatePaymentRequest.Address billing = IyzicoCreatePaymentRequest.Address.builder()
                .contactName("Kubra M")
                .city("Istanbul")
                .country("TR")
                .addressLine("Some address")
                .build();

        IyzicoCreatePaymentRequest req = IyzicoCreatePaymentRequest.builder()
                .locale("tr")
                .conversationId("conv-2")
                .price("200.0")
                .paidPrice("200.0")
                .currency("TRY")
                .paymentCard(card)
                .buyer(buyer)
                .billingAddress(billing)
                .build();

        assertThat(req.getPaymentCard().getCardHolderName()).isEqualTo("Test User");
        assertThat(req.getBuyer().getId()).isEqualTo("buyer-1");
        assertThat(req.getBillingAddress().getCity()).isEqualTo("Istanbul");
        assertThat(req.getPaidPrice()).isEqualTo("200.0");
    }
}
