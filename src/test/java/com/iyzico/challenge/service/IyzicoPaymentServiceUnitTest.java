package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.BankPaymentRequest;
import com.iyzico.challenge.dto.BankPaymentResponse;

import com.iyzico.challenge.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IyzicoPaymentServiceUnitTest {

    private BankService bankService;
    private PaymentRepository paymentRepository;

    private IyzicoPaymentService iyzicoPaymentService;

    @BeforeEach
    void setUp() {
        bankService = mock(BankService.class);
        paymentRepository = mock(PaymentRepository.class);
        iyzicoPaymentService = new IyzicoPaymentService(bankService, paymentRepository);
    }

    @Test
    void pay_whenBankSuccess_shouldPersistPayment() {
        // given
        BigDecimal price = new BigDecimal("10.00");
        BankPaymentResponse bankResp = new BankPaymentResponse();
        bankResp.setResultCode("200");

        when(bankService.pay(any(BankPaymentRequest.class))).thenReturn(bankResp);

        // when
        iyzicoPaymentService.pay(price);

        // then
        verify(bankService).pay(argThat(req -> req != null && price.compareTo(req.getPrice()) == 0));

        verify(paymentRepository).save(argThat(p ->
                p != null
                        && price.compareTo(p.getPrice()) == 0
                        && "200".equals(p.getBankResponse())
        ));

        verifyNoMoreInteractions(bankService, paymentRepository);
    }

    @Test
    void pay_whenBankThrows_shouldNotPersistAnything() {
        // given
        BigDecimal price = new BigDecimal("10.00");

        when(bankService.pay(any(BankPaymentRequest.class)))
                .thenThrow(new RuntimeException("bank timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> iyzicoPaymentService.pay(price));
        assertEquals("bank timeout", ex.getMessage());

        verify(bankService).pay(any(BankPaymentRequest.class));
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void persistBankResponse_shouldSavePayment() {
        // given
        BigDecimal price = new BigDecimal("99.99");
        String resultCode = "200";

        // when
        iyzicoPaymentService.persistBankResponse(price, resultCode);

        // then
        verify(paymentRepository).save(argThat(p ->
                p != null
                        && price.compareTo(p.getPrice()) == 0
                        && resultCode.equals(p.getBankResponse())
        ));
        verifyNoMoreInteractions(paymentRepository);
        verifyNoInteractions(bankService);
    }
}
