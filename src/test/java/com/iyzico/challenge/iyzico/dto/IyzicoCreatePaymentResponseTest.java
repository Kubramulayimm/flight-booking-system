package com.iyzico.challenge.iyzico.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IyzicoCreatePaymentResponseTest {

    @Test
    void shouldCreateWithNoArgsConstructorAndSetFields() {
        // given
        IyzicoCreatePaymentResponse response = new IyzicoCreatePaymentResponse();

        // when
        response.setStatus("success");
        response.setErrorCode("0");
        response.setErrorMessage(null);
        response.setConversationId("conv-123");
        response.setPaymentId("pay-456");
        response.setPaidPrice("100.0");
        response.setPrice("100.0");

        // then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getErrorCode()).isEqualTo("0");
        assertThat(response.getErrorMessage()).isNull();
        assertThat(response.getConversationId()).isEqualTo("conv-123");
        assertThat(response.getPaymentId()).isEqualTo("pay-456");
        assertThat(response.getPaidPrice()).isEqualTo("100.0");
        assertThat(response.getPrice()).isEqualTo("100.0");
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        // when
        IyzicoCreatePaymentResponse response =
                new IyzicoCreatePaymentResponse(
                        "failure",
                        "500",
                        "Bank error",
                        "conv-999",
                        "pay-999",
                        "0",
                        "100.0"
                );

        // then
        assertThat(response.getStatus()).isEqualTo("failure");
        assertThat(response.getErrorCode()).isEqualTo("500");
        assertThat(response.getErrorMessage()).isEqualTo("Bank error");
        assertThat(response.getConversationId()).isEqualTo("conv-999");
        assertThat(response.getPaymentId()).isEqualTo("pay-999");
        assertThat(response.getPaidPrice()).isEqualTo("0");
        assertThat(response.getPrice()).isEqualTo("100.0");
    }
}
