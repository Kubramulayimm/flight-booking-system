package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.BankPaymentRequest;
import com.iyzico.challenge.dto.BankPaymentResponse;
import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class IyzicoPaymentService {

    private Logger logger = LoggerFactory.getLogger(IyzicoPaymentService.class);

    private final BankService bankService;
    private final PaymentRepository paymentRepository;


    public void pay(BigDecimal price) {
        //pay with bank
        BankPaymentRequest request = new BankPaymentRequest();
        request.setPrice(price);
        BankPaymentResponse response = bankService.pay(request);

        //insert records
        persistBankResponse(price, response.getResultCode());
        logger.info("Payment saved successfully!");
    }

    public void persistBankResponse(BigDecimal price, String resultCode) {
        Payment payment = new Payment();
        payment.setPrice(price);
        payment.setBankResponse(resultCode);
        paymentRepository.save(payment);
    }
}
