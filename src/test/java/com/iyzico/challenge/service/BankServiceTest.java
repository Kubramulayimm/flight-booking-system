package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.BankPaymentRequest;
import com.iyzico.challenge.dto.BankPaymentResponse;
import com.iyzico.challenge.iyzico.IyzicoNon3dsClient;
import com.iyzico.challenge.iyzico.dto.IyzicoCreatePaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankServiceTest {

    private IyzicoNon3dsClient iyzicoNon3dsClient;
    private BankService bankService;

    @BeforeEach
    void setUp() {
        iyzicoNon3dsClient = mock(IyzicoNon3dsClient.class);
        bankService = new BankService(iyzicoNon3dsClient);
    }

    @Test
    void pay_shouldReturnSuccessResponse_whenIyzicoReturnsSuccess() {
        // given
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(new BigDecimal("10.00"));

        IyzicoCreatePaymentResponse iyzicoResp = new IyzicoCreatePaymentResponse();
        iyzicoResp.setStatus("success");

        when(iyzicoNon3dsClient.createPayment(any(BigDecimal.class)))
                .thenReturn(iyzicoResp);

        // when
        BankPaymentResponse response = bankService.pay(request);

        // then
        assertNotNull(response);
        assertEquals("200", response.getResultCode());

        verify(iyzicoNon3dsClient).createPayment(new BigDecimal("10.00"));
    }

    @Test
    void pay_shouldThrowException_whenIyzicoResponseIsNull() {
        // given
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(new BigDecimal("5.00"));

        when(iyzicoNon3dsClient.createPayment(any()))
                .thenReturn(null);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> bankService.pay(request)
        );

        assertTrue(ex.getMessage().contains("iyzico response is null"));
    }

    @Test
    void pay_shouldThrowException_whenStatusIsNull() {
        // given
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(new BigDecimal("5.00"));

        IyzicoCreatePaymentResponse iyzicoResp = new IyzicoCreatePaymentResponse();
        iyzicoResp.setStatus(null);

        when(iyzicoNon3dsClient.createPayment(any()))
                .thenReturn(iyzicoResp);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> bankService.pay(request)
        );

        assertTrue(ex.getMessage().contains("iyzico response is null"));
    }

    @Test
    void pay_shouldThrowException_whenStatusIsNotSuccess() {
        // given
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(new BigDecimal("20.00"));

        IyzicoCreatePaymentResponse iyzicoResp = new IyzicoCreatePaymentResponse();
        iyzicoResp.setStatus("failure");
        iyzicoResp.setErrorCode("500");
        iyzicoResp.setErrorMessage("BANK_ERROR");

        when(iyzicoNon3dsClient.createPayment(any()))
                .thenReturn(iyzicoResp);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> bankService.pay(request)
        );

        assertTrue(ex.getMessage().contains("iyzico payment failed"));
        assertTrue(ex.getMessage().contains("500"));
        assertTrue(ex.getMessage().contains("BANK_ERROR"));
    }

    @Test
    void pay_shouldReturnNull_whenThreadInterrupted() throws Exception {
        // given
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(new BigDecimal("30.00"));

        IyzicoCreatePaymentResponse iyzicoResp = new IyzicoCreatePaymentResponse();
        iyzicoResp.setStatus("success");

        when(iyzicoNon3dsClient.createPayment(any()))
                .thenReturn(iyzicoResp);

        Thread.currentThread().interrupt();

        // when
        BankPaymentResponse response = bankService.pay(request);

        // then
        assertNull(response);

        Thread.interrupted();
    }
}
