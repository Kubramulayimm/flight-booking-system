package com.iyzico.challenge.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BankPaymentRequestTest {

    @Test
    void should_set_and_get_price() {
        BankPaymentRequest req = new BankPaymentRequest();

        BigDecimal price = new BigDecimal("123.45");
        req.setPrice(price);

        assertEquals(price, req.getPrice());
    }

    @Test
    void should_allow_null_price() {
        BankPaymentRequest req = new BankPaymentRequest();

        req.setPrice(null);

        assertNull(req.getPrice());
    }
}
