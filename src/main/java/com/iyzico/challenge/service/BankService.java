package com.iyzico.challenge.service;

import com.iyzico.challenge.dto.BankPaymentRequest;
import com.iyzico.challenge.dto.BankPaymentResponse;
import com.iyzico.challenge.iyzico.IyzicoNon3dsClient;
import com.iyzico.challenge.iyzico.dto.IyzicoCreatePaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankService {

    private final IyzicoNon3dsClient iyzicoNon3dsClient;

    /**
     * Bank Latency Simulation (avg: 5 seconds)
     */
    public BankPaymentResponse pay(BankPaymentRequest request) {
        try {
            IyzicoCreatePaymentResponse response = iyzicoNon3dsClient.createPayment(request.getPrice());

            if (response == null || response.getStatus() == null) {
                throw new IllegalStateException("iyzico response is null");
            }

            if (!"success".equalsIgnoreCase(response.getStatus())) {
                throw new IllegalStateException("iyzico payment failed: " + response.getErrorCode() + " - " + response.getErrorMessage());
            }

            Thread.sleep(5000);
            return new BankPaymentResponse("200");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
