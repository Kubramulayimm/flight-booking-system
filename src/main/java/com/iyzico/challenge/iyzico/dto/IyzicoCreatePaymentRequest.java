package com.iyzico.challenge.iyzico.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IyzicoCreatePaymentRequest {
    private String locale;
    private String conversationId;
    private String price;
    private String paidPrice;
    private String currency;
    private Integer installment;
    private String basketId;
    private String paymentChannel;
    private String paymentGroup;

    private PaymentCard paymentCard;
    private Buyer buyer;
    private Address billingAddress;
    private Address shippingAddress;
    private List<BasketItem> basketItems;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PaymentCard {
        private String cardHolderName;
        private String cardNumber;
        private String expireMonth;
        private String expireYear;
        private String cvc;
        private Integer registerCard;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Buyer {
        private String id;
        private String name;
        private String surname;
        private String gsmNumber;
        private String email;
        private String identityNumber;
        private String registrationAddress;
        private String city;
        private String country;
        private String ip;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Address {
        private String contactName;
        private String city;
        private String country;
        private String addressLine;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BasketItem {
        private String id;
        private String name;
        private String category1;
        private String itemType;
        private String price;
    }
}
