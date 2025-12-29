package com.iyzico.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceClientsTest {

    private IyzicoPaymentService iyzicoPaymentService;
    private PaymentServiceClients paymentServiceClients;

    @BeforeEach
    void setUp() {
        iyzicoPaymentService = mock(IyzicoPaymentService.class);
        paymentServiceClients = new PaymentServiceClients(iyzicoPaymentService);
    }

    @Test
    void call_shouldInvokeIyzicoPaymentService_andReturnCompletedFuture() throws Exception {
        // given
        BigDecimal price = new BigDecimal("25.50");

        // when
        CompletableFuture<String> future = paymentServiceClients.call(price);

        // then
        assertNotNull(future);
        assertTrue(future.isDone());
        assertEquals("success", future.get());

        verify(iyzicoPaymentService, times(1)).pay(price);
    }
}
