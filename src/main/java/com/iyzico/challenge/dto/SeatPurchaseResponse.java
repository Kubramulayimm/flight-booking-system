package com.iyzico.challenge.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class SeatPurchaseResponse {
    private Long paymentId;
    private Long flightId;
    private Long seatId;
    private String seatNo;
    private BigDecimal price;
    private String passengerName;
    private String status;
    private Date createdAt;
}
