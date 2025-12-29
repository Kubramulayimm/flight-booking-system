package com.iyzico.challenge.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BankPaymentResponse {

    private String resultCode;

    public BankPaymentResponse(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}


