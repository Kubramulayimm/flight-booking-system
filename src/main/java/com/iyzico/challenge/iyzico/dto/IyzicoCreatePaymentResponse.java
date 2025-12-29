package com.iyzico.challenge.iyzico.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IyzicoCreatePaymentResponse {
    private String status;
    private String errorCode;
    private String errorMessage;
    private String conversationId;
    private String paymentId;
    private String paidPrice;
    private String price;
}
