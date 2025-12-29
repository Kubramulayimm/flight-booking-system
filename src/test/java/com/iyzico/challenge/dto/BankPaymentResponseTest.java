package com.iyzico.challenge.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankPaymentResponseTest {

    @Test
    void noArgsConstructor_should_create_empty_object() {
        BankPaymentResponse response = new BankPaymentResponse();

        assertNull(response.getResultCode());
    }

    @Test
    void allArgsConstructor_should_set_resultCode() {
        BankPaymentResponse response = new BankPaymentResponse("200");

        assertEquals("200", response.getResultCode());
    }

    @Test
    void setter_should_update_resultCode() {
        BankPaymentResponse response = new BankPaymentResponse();

        response.setResultCode("500");

        assertEquals("500", response.getResultCode());
    }
}
